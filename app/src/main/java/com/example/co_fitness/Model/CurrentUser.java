package com.example.co_fitness.Model;

import androidx.annotation.NonNull;

import com.example.co_fitness.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentUser {

    private static CurrentUser currentUser = null;
    private User userProfile = null;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDB;

    private CurrentUser() {
        firebaseDB = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadUserProfile();
        }
    }

    public static CurrentUser getInstance(){
        if (currentUser == null)
            currentUser = new CurrentUser();
        return currentUser;
    }

    public User getUserProfile() {
        return userProfile;
    }

    public CurrentUser setUserProfile(User userProfile) {
        this.userProfile = userProfile;
        return this;
    }

    private void loadUserProfile() {
        DatabaseReference dbRef = firebaseDB.getReference(Constants.DB_USERS);
        dbRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userProfile = snapshot.getValue(User.class);
                }
                else
                    userProfile = null;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public String getUid() {
        if(getUserProfile() != null)
            return getUserProfile().getUid();
        return null;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "userProfile=" + userProfile +
                ", user=" + user +
                '}';
    }
}
