package com.example.co_fitness.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.co_fitness.Callbacks.OfferCallback;
import com.example.co_fitness.Constants;
import com.example.co_fitness.Fragments.MyOffersFragment;
import com.example.co_fitness.Model.CurrentUser;
import com.example.co_fitness.Model.Offer;
import com.example.co_fitness.R;
import com.example.co_fitness.Model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {
    private Fragment fragment;
    private ArrayList<Offer> myOffersList;
    private ArrayList<Offer> allOffersList;
    private OfferCallback offerCallback;


    public OffersAdapter(Fragment fragment, ArrayList<Offer> allOffersList) {
        this.myOffersList = new ArrayList<>();
        this.allOffersList = allOffersList;
        this.fragment = fragment;
    }

    public OffersAdapter setOfferCallback(OfferCallback offerCallback) {
        this.offerCallback = offerCallback;
        return this;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item, parent, false);
        OfferViewHolder offerViewHolder = new OfferViewHolder(view);
        return offerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OffersAdapter.OfferViewHolder holder, int position) {
        Offer offer = getItem(position);
        holder.offer_TV_type.setText(offer.getType());
        holder.offer_TV_time.setText(offer.getDate() + "  " + offer.getTime() + "");
        holder.offer_TV_place.setText(offer.getPlace());
        holder.offer_TV_level.setText(offer.getLevel());
        holder.offer_TV_cost.setText(offer.getCost());
        holder.offer_TV_capacity.setText(offer.getNumOfUsers() + "/" + offer.getCapacity());
        holder.getCreatedUserFromDB(offer.getCreatedUserId());
        chooseIcon(offer.getType(), holder);
        if(offer.isUserInOffer(CurrentUser.getInstance().getUserProfile().getUid())){
            if(offer.getCreatedUserId().equals(CurrentUser.getInstance().getUserProfile().getUid())) {
                holder.offer_BTN_join.setVisibility(View.GONE);
                holder.offer_BTN_leave.setVisibility(View.GONE);
                if (fragment instanceof MyOffersFragment) {
                    holder.offer_BTN_delete.setVisibility(View.VISIBLE);
                    holder.offer_BTN_edit.setVisibility(View.VISIBLE);
                }else {
                    holder.offer_BTN_delete.setVisibility(View.INVISIBLE);
                    holder.offer_BTN_edit.setVisibility(View.INVISIBLE);
                }
            }else {
                holder.offer_BTN_join.setVisibility(View.GONE);
                holder.offer_BTN_leave.setVisibility(View.VISIBLE);
                holder.offer_BTN_delete.setVisibility(View.INVISIBLE);
                holder.offer_BTN_edit.setVisibility(View.INVISIBLE);
            }
        }else{
            holder.offer_BTN_join.setVisibility(View.VISIBLE);
            holder.offer_BTN_leave.setVisibility(View.GONE);
            holder.offer_BTN_delete.setVisibility(View.INVISIBLE);
            holder.offer_BTN_edit.setVisibility(View.INVISIBLE);
        }
    }
    private void chooseIcon(String type, @NonNull OffersAdapter.OfferViewHolder holder) {
        switch (type) {
            case "Tennis":
                holder.offer_IMG_type.setImageResource(R.drawable.ic_tennis);
                break;
            case "Football":
                holder.offer_IMG_type.setImageResource(R.drawable.ic_football);
                break;
            case "Basketball":
                holder.offer_IMG_type.setImageResource(R.drawable.ic_basketball);
                break;
            case "Running/Jogging":
                holder.offer_IMG_type.setImageResource(R.drawable.ic_running);
                break;
        }
    }

    private Offer getItem(int position) {
        return allOffersList.get(position);
    }

    @Override
    public int getItemCount() {
        return allOffersList == null ? 0 : allOffersList.size();
    }

    public void updateOffers(ArrayList<Offer> allOffers) {
        this.myOffersList = allOffers;
        this.allOffersList = allOffers;
        notifyDataSetChanged();
    }

    public ArrayList<Offer> getAllOffersList() {
        return allOffersList;
    }
    public class OfferViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView offer_IMG_img;
        private MaterialTextView offer_TV_type;
        private MaterialTextView offer_TV_place;
        private MaterialTextView offer_TV_time;
        private MaterialTextView offer_TV_capacity;
        private MaterialTextView offer_TV_level;
        private MaterialTextView offer_TV_cost;
        private MaterialButton offer_BTN_join;
        private MaterialButton offer_BTN_leave;
        private MaterialButton offer_BTN_delete;
        private MaterialButton offer_BTN_edit;
        private ImageView offer_IMG_type;


        private User createdUser = new User();

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            itemView.setOnClickListener(view -> offerCallback.itemClicked(getItem(getAdapterPosition()), getAdapterPosition()));
            offer_BTN_join.setOnClickListener(view -> offerCallback.joinClicked(getItem(getAdapterPosition()), getAdapterPosition()));
            offer_BTN_leave.setOnClickListener(view -> offerCallback.leaveClicked(getItem(getAdapterPosition()), getAdapterPosition()));
            offer_BTN_delete.setOnClickListener(view -> offerCallback.deleteClicked(getItem(getAdapterPosition()), getAdapterPosition()));
            offer_BTN_edit.setOnClickListener(view -> offerCallback.editClicked(getItem(getAdapterPosition()), getAdapterPosition()));
            if (fragment instanceof MyOffersFragment) {
                offer_BTN_join.setVisibility(View.INVISIBLE);
                offer_BTN_leave.setVisibility(View.VISIBLE);
                offer_BTN_delete.setVisibility(View.VISIBLE);
                offer_BTN_edit.setVisibility(View.VISIBLE);

            } else { //in home fragment
                offer_BTN_join.setVisibility(View.VISIBLE);
                offer_BTN_leave.setVisibility(View.INVISIBLE);
                offer_BTN_delete.setVisibility(View.INVISIBLE);
                offer_BTN_edit.setVisibility(View.INVISIBLE);
            }
        }

        public void getCreatedUserFromDB(String userId) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                    Glide.
                            with(fragment.getContext()).
                            load(snapshot.child("image").getValue()).
                            into(offer_IMG_img);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        private void initViews() {
            offer_IMG_img = itemView.findViewById(R.id.offer_IMG_img);
            offer_TV_type = itemView.findViewById(R.id.offer_TV_type);
            offer_TV_place = itemView.findViewById(R.id.offer_TV_place);
            offer_TV_time = itemView.findViewById(R.id.offer_TV_time);
            offer_TV_capacity = itemView.findViewById(R.id.offer_TV_capacity);
            offer_TV_level = itemView.findViewById(R.id.offer_TV_level);
            offer_TV_cost = itemView.findViewById(R.id.offer_TV_cost);
            offer_BTN_join = itemView.findViewById(R.id.offer_BTN_join);
            offer_BTN_leave = itemView.findViewById(R.id.offer_BTN_leave);
            offer_BTN_delete = itemView.findViewById(R.id.offer_BTN_delete);
            offer_BTN_edit = itemView.findViewById(R.id.offer_BTN_edit);
            offer_IMG_type = itemView.findViewById(R.id.offer_IMG_type);
        }
    }
}
