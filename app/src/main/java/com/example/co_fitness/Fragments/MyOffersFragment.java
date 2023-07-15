package com.example.co_fitness.Fragments;

import static com.example.co_fitness.Constants.DB_OFFER_ID;

import android.content.DialogInterface;
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
import com.example.co_fitness.Model.User;
import com.example.co_fitness.Model.Offer;
import com.example.co_fitness.databinding.FragmentMyOffersBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

public class MyOffersFragment extends Fragment {

    private FragmentMyOffersBinding binding;
    private OffersAdapter offerAdapter;

    private ArrayList<Offer> offers = new ArrayList<Offer>();

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = mDatabase.getReference();

    private ValueEventListener valueEventListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyOffersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        loadOffers();
        return root;
    }

    private void loadOffers() {
        dbRef= FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS);
        if (valueEventListener != null)
            dbRef.removeEventListener(valueEventListener);
        valueEventListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Offer offer = snap.getValue(Offer.class);
                    if (!isOfferExpired(offer)&& offer.isUserInOffer(CurrentUser.getInstance().getUserProfile().getUid()))
                        offers.add(offer);
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
        }
    }

    private void showMyAlertDialog(Offer offer, int position){
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle("Delete Offer");
        alertDialogBuilder.setMessage("Are you sure you want to delete this offer?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_menu_delete);
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> users = offer.getUsersID();
                for (String userId : users) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS).child(userId);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null && user.getOffers()!= null && user.getOffers().contains(offer.getId())) {
                                user.getOffers().remove(offer.getId());
                                userRef.setValue(user);
                            }
                            offers.clear(); // Clear the offers list
                            loadOffers(); // Reload the offers list
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                DatabaseReference offerRef = FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS).child(offer.getId());
                offerRef.removeValue();            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }


    private void initViews() {
        if (offers == null || offers.isEmpty()) {
            binding.myOffersLSTOffers.setVisibility(View.GONE);
            binding.myOffersTVNoGroups.setVisibility(View.VISIBLE);
        } else {
            binding.myOffersLSTOffers.setVisibility(View.VISIBLE);
            binding.myOffersTVNoGroups.setVisibility(View.GONE);
        }
        offerAdapter = new OffersAdapter(this, offers);
        binding.myOffersLSTOffers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.myOffersLSTOffers.setAdapter(offerAdapter);
    }


    private void setCallbacks() {
        offerAdapter.setOfferCallback(new OfferCallback() {
            @Override
            public void leaveClicked(Offer offer, int position) {
                CurrentUser.getInstance().getUserProfile().getOffers().remove(offer.getId());
                offer.removeUser(CurrentUser.getInstance().getUserProfile().getUid());

                //save the offer to the user and the user to the offer
                dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS);
                dbRef.child(offer.getId()).setValue(offer);
                dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
                dbRef.child(CurrentUser.getInstance().getUserProfile().getUid()).setValue(CurrentUser.getInstance().getUserProfile());

                offers.clear(); // Clear the offers list
                loadOffers(); // Reload the offers list
           }
            @Override
            public void itemClicked(Offer offer, int position) {
                ((MainPageActivity)getActivity()).replaceFragments(ShowOfferFragment.class, DB_OFFER_ID,offer.getId(),"");
            }

            @Override
            public void deleteClicked(Offer offer, int position) {
                showMyAlertDialog(offer,position);
            }

            @Override
            public void joinClicked(Offer offer, int position) {}
            @Override
            public void editClicked(Offer offer, int position) {
                ((MainPageActivity)getActivity()).replaceFragments(CreateOfferFragment.class, DB_OFFER_ID,offer.getId(),"edit");
            }


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