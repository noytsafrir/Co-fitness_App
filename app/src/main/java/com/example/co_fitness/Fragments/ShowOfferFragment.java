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
import com.example.co_fitness.Adapters.UsersAdapter;
import com.example.co_fitness.Constants;
import com.example.co_fitness.Model.User;
import com.example.co_fitness.Model.Offer;
import com.example.co_fitness.R;
import com.example.co_fitness.databinding.FragmentOfferBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ShowOfferFragment extends Fragment {

    private FragmentOfferBinding binding;
    private DatabaseReference databaseReference;
    private String offerId;
    private Offer offer;
    private UsersAdapter userAdapter;
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<String> usersId = new ArrayList<String>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOfferBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initViews();
        initListeners();
        return root;
    }

    private void initViews(){
        offerId = getArguments().getString(DB_OFFER_ID);
        getOfferFromDB(offerId);
    }

    public void getOfferFromDB(String offerId) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS);
        databaseReference.child(offerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    offer=snapshot.getValue(Offer.class);
                    setOfferDetails();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setOfferDetails(){
        usersId = offer.getUsersID();
        binding.offerTVType.setText(offer.getType());
        chooseIcon(offer.getType());
        binding.offerTVDate.setText(offer.getDate());
        binding.offerTVTime.setText(offer.getTime());
        binding.offerTVLocation.setText(offer.getPlace());
        binding.offerTVLevel.setText(offer.getLevel());
        if(offer.getCost().equals("Free"))
            binding.offerTVCost.setText("Free");
        else
            binding.offerTVCost.setText(offer.getCost() + "₪");

        if(offer.getDescription().equals("")) {
            binding.offerTVDetails.setVisibility(View.GONE);
            binding.offerIMGDetails.setVisibility(View.GONE);
        }
        else
            binding.offerTVDetails.setText(offer.getDescription());
        getUsersList();
    }

    private void getUsersList() {
        if (usersId != null){
            for (String userId : usersId) {
                databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
                databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User userProfile = snapshot.getValue(User.class);
                            users.add(userProfile);
                            if (users.size() == usersId.size()) {
                                setUserAdapterAndRecycleView();
                            }
                        } else {
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }

    private void setUserAdapterAndRecycleView() {
        userAdapter = new UsersAdapter(this, users);
        binding.offerRCVUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.offerRCVUsers.setAdapter(userAdapter);
    }


    private void chooseIcon(String type) {
        switch (type) {
            case "Tennis":
                binding.offerIMGType.setImageResource(R.drawable.ic_tennis);
                break;
            case "Football":
                binding.offerIMGType.setImageResource(R.drawable.ic_football);
                break;
            case "Basketball":
                binding.offerIMGType.setImageResource(R.drawable.ic_basketball);
                break;
            case "Running/Jogging":
                binding.offerIMGType.setImageResource(R.drawable.ic_running);
                break;
        }
    }

    private void initListeners(){
        binding.offerBTNBack.setOnClickListener(view -> returnToLastPage());
    }

    private void returnToLastPage() {
        String lastPage = ((MainPageActivity)getActivity()).getLastPage();
        if(lastPage.equals("MyOffersFragment"))
            ((MainPageActivity)getActivity()).replaceFragments(MyOffersFragment.class, "","","");
        else
            ((MainPageActivity)getActivity()).replaceFragments(HomeFragment.class, "","","");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}