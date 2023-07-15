package com.example.co_fitness.Adapters;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.co_fitness.Model.User;
import com.example.co_fitness.R;
import com.google.android.material.textview.MaterialTextView;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private Fragment fragment;
    private ArrayList<User> usersList;


    public UsersAdapter(Fragment fragment, ArrayList<User> usersList) {
        this.usersList = usersList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(view);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserViewHolder holder, int position) {
        User user = getItem(position);
        holder.user_TV_name.setText(user.getName());
        holder.user_TV_gender.setText(user.getGender());
        holder.user_TV_age.setText(getAge(user.getDateOfBirth()));
        holder.user_TV_phone.setText(user.getPhoneNumber());
        Glide.with(fragment)
                .load(user.getImage())
                .into(holder.user_IMG_img);
    }

    private String getAge(String dateOfBirth){
        String[] date = dateOfBirth.split("/");
        int year = Integer.parseInt(date[2]);
        int age = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            age = LocalDate.now(Clock.systemDefaultZone()).getYear() - year;
        }
        else{
            age = 2023 - year;
        }
        return age + "";
    }

    private User getItem(int position) {
        return usersList.get(position);
    }

    @Override
    public int getItemCount() {
        return usersList == null ? 0 : usersList.size();
    }

    public void updateUsers(ArrayList<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView user_IMG_img;
        private MaterialTextView user_TV_name;
        private MaterialTextView user_TV_gender;
        private MaterialTextView user_TV_age;
        private MaterialTextView user_TV_phone;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            user_TV_phone = itemView.findViewById(R.id.  user_TV_phone);
            user_TV_age = itemView.findViewById(R.id.    user_TV_age);
            user_TV_gender = itemView.findViewById(R.id. user_TV_gender);
            user_TV_name = itemView.findViewById(R.id.   user_TV_name);
            user_IMG_img = itemView.findViewById(R.id.   user_IMG_img);
        }
    }
}
