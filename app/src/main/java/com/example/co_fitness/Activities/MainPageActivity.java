package com.example.co_fitness.Activities;

import static com.example.co_fitness.Constants.DB_SPORT_TYPE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.co_fitness.Fragments.MyOffersFragment;
import com.example.co_fitness.Fragments.UserProfileFragment;
import com.example.co_fitness.R;
import com.example.co_fitness.Fragments.ChooseSportTypeFragment;
import com.example.co_fitness.Fragments.CreateOfferFragment;
import com.example.co_fitness.Fragments.HomeFragment;
import com.example.co_fitness.Utils.My_Screen_Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private CreateOfferFragment createOfferFragment = new CreateOfferFragment();
    private static BottomNavigationView bottomNavigationView;
    private String data;

    private String lastPage;
    private String currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        My_Screen_Utils.hideSystemUI(this);
        bottomNavigationView = findViewById(R.id.homepage_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        currentPage = "HomeFragment";
        bottomNavigationView.setSelectedItemId(R.id.menu_FRG_home);
        data = getDetails(DB_SPORT_TYPE);
    }

    private String getDetails(String str) {
        Intent intent = getIntent();
        String message = intent.getStringExtra(str);
        return message;
    }


    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void sendDetails(String key,String data) {
        Bundle bundle = new Bundle();
        bundle.putString(key, data);
        createOfferFragment.setArguments(bundle);
    }

        public void replaceFragments(Class fragmentClass, String key, String value, String action) {
            lastPage = currentPage;
            currentPage = fragmentClass.getSimpleName();
            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bundle bundle = new Bundle();
            bundle.putString(key, value);
            bundle.putString("action", action);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.homepage_FRAME_main, fragment)
                    .commit();
        }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        if(item.getItemId() == R.id.menu_FRG_home) {
            this.replaceFragments(HomeFragment.class, "", "","");
            return true;
        } else if (item.getItemId() == R.id.menu_FRG_new_offer) {
            this.replaceFragments(ChooseSportTypeFragment.class, "", "","");
            return true;
        } else if (item.getItemId() == R.id.menu_FRG_offers) {
            this.replaceFragments(MyOffersFragment.class, "", "","");
            return true;
        } else if (item.getItemId() == R.id.menu_FRG_profile) {
            this.replaceFragments(UserProfileFragment.class, "", "","");
//            Intent intent = new Intent(this, UserProfileActivity.class);
//            startActivity(intent);
            return true;
        }
        return false;
    }


    public String getLastPage() {
        return lastPage;
    }

    public String getCurrentPage() {
        return currentPage;
    }

}