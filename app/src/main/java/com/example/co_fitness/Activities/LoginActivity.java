package com.example.co_fitness.Activities;//package com.example.co_fitness.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.co_fitness.Model.CurrentUser;
import com.example.co_fitness.R;
import com.example.co_fitness.Constants;
import com.example.co_fitness.Model.User;
import com.example.co_fitness.Utils.My_Screen_Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        My_Screen_Utils.hideSystemUI(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            login();
        } else {
            loadUser();
        }
    }

    private void login() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setTheme(R.style.LoginTheme)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult
            (new FirebaseAuthUIActivityResultContract(), result -> onSignInResult(result));

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {}


    private void loadUser() {
        databaseRef = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
        databaseRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    CurrentUser.getInstance().setUserProfile(snapshot.getValue(User.class));
                else
                    createNewUser();
                if(!CurrentUser.getInstance().getUserProfile().getIsRegistered())
                    goToProfileFragment();
                else
                    goToMainActivity();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNewUser() {
        User newUser = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
        CurrentUser.getInstance().setUserProfile(newUser);
        databaseRef = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
        databaseRef.child(firebaseUser.getUid()).setValue(newUser);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainPageActivity.class);
        startActivity(intent);
        finish();
    }


    private void goToProfileFragment() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
        finish();
    }



}