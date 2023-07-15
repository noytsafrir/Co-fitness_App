package com.example.co_fitness.Fragments;

import static com.example.co_fitness.Constants.DB_OFFER_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.co_fitness.Activities.MainPageActivity;
import com.example.co_fitness.Adapters.OffersAdapter;
import com.example.co_fitness.Callbacks.OfferCallback;
import com.example.co_fitness.Constants;
import com.example.co_fitness.Model.CurrentUser;
import com.example.co_fitness.Model.Offer;
import com.example.co_fitness.R;
import com.example.co_fitness.Utils.SignalSingleton;
import com.example.co_fitness.databinding.FragmentHomeBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private OffersAdapter offerAdapter;
    private ArrayList<Offer> offers = new ArrayList<Offer>();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = mDatabase.getReference();
    private String selectedOption = "All";
    private ValueEventListener valueEventListener;
    private ChipGroup classificationChipGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        classificationChipGroup = root.findViewById(R.id.classification_chip_group);
        classificationChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                // Handle chip selection
                Chip selectedChip = group.findViewById(checkedId);
                if (selectedChip != null) {
                    String selectedOption = selectedChip.getText().toString();
                    handleClassificationOptionSelected(selectedOption);
                }else {
                    handleClassificationOptionSelected("All");
                }
            }
        });
        reloadOffers();
        return root;
    }

    private void handleClassificationOptionSelected(String selectedOption) {
        if(selectedOption.equals("Running"))
            this.selectedOption="Running/Jogging";
        else
            this.selectedOption = selectedOption;
        reloadOffers();

    }

    public void reloadOffers() {
        offers.clear(); // Clear the offers list
        dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS);

        if (valueEventListener != null)
            dbRef.removeEventListener(valueEventListener);
        valueEventListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Offer offer = snap.getValue(Offer.class);
                    if (!isOfferExpired(offer)) {
                        if (selectedOption.equals("All"))
                            offers.add(offer);
                        else if (offer.getType().equals(selectedOption))
                            offers.add(offer);
                    }
                }

                Collections.sort(offers, new Comparator<Offer>() {
                    @Override
                    public int compare(Offer o1, Offer o2) {
                        int dateComparison = o1.getDate().compareTo(o2.getDate());
                        if (dateComparison != 0) {
                            return dateComparison; // Sort by date in descending order
                        }
                        return o1.getTime().compareTo(o2.getTime()); // Sort by time in descending order
                    }
                });

                initFragmentData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private boolean isOfferExpired(Offer offer) {
        // Get the date and time of the offer
        String offerDate = offer.getDate();
        String offerTime = offer.getTime();
        String offerDateTimeString = offerDate + " " + offerTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date offerDateTime = dateFormat.parse(offerDateTimeString);
            Date currentDateTime = new Date();   // current date and time
            if (offerDateTime != null && offerDateTime.before(currentDateTime)) {
                return true; // Offer is expired
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void initFragmentData() {
        if(isVisible()) {
            initViews();
            setCallbacks();
            initListeners();
        }
    }

    private void initViews() {
        binding.homeTVWelcome.setText("Hello " + CurrentUser.getInstance().getUserProfile().getName());
        if (offers == null || offers.isEmpty()) {
            binding.homeLSTOffers.setVisibility(View.GONE);
            binding.homeTVNoGroups.setVisibility(View.VISIBLE);
        } else {
            binding.homeLSTOffers.setVisibility(View.VISIBLE);
            binding.homeTVNoGroups.setVisibility(View.GONE);
        }

        offerAdapter = new OffersAdapter(this, offers);
        binding.homeLSTOffers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.homeLSTOffers.setAdapter(offerAdapter);
    }

    private void initListeners() {
        binding.homeLogout.setOnClickListener(view -> ((MainPageActivity)getContext()).signOut());
    }

    private void setCallbacks() {
        offerAdapter.setOfferCallback(new OfferCallback() {
            @Override
            public void joinClicked(Offer offer, int position) {
                if (offer.getCapacity() <= offer.getNumOfUsers()) {
                    SignalSingleton.getInstance().toast("The group is full");
                } else {
                    if(!CurrentUser.getInstance().getUserProfile().getOffers().contains(offer.getId())) {
                        CurrentUser.getInstance().getUserProfile().getOffers().add(offer.getId());
                        offer.addUser(CurrentUser.getInstance().getUserProfile().getUid());
                    }
                    //save the offer to the user and the user to the offer
                    dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS);
                    dbRef.child(offer.getId()).setValue(offer);
                    dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
                    dbRef.child(CurrentUser.getInstance().getUserProfile().getUid()).setValue(CurrentUser.getInstance().getUserProfile());

                    reloadOffers();
                }
            }
            @Override
            public void leaveClicked(Offer item, int position) {
                CurrentUser.getInstance().getUserProfile().getOffers().remove(item.getId());
                item.removeUser(CurrentUser.getInstance().getUserProfile().getUid());
                //save the offer to the user and the user to the offer
                dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS);
                dbRef.child(item.getId()).setValue(item);
                dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
                dbRef.child(CurrentUser.getInstance().getUserProfile().getUid()).setValue(CurrentUser.getInstance().getUserProfile());
                reloadOffers();
            }

            @Override
            public void itemClicked(Offer offer, int position) {
                ((MainPageActivity)getActivity()).replaceFragments(ShowOfferFragment.class, DB_OFFER_ID,offer.getId(), "");
            }

            @Override
            public void deleteClicked(Offer offer, int position) {}

            @Override
            public void editClicked(Offer offer, int position) {}
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (valueEventListener != null)
            dbRef.removeEventListener(valueEventListener);
    }
}