package com.example.co_fitness.Fragments;

import static com.example.co_fitness.Constants.DB_BASKETBALL;
import static com.example.co_fitness.Constants.DB_FOOTBALL;
import static com.example.co_fitness.Constants.DB_RUNNING;
import static com.example.co_fitness.Constants.DB_SPORT_TYPE;
import static com.example.co_fitness.Constants.DB_TENNIS;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.co_fitness.Activities.MainPageActivity;
import com.example.co_fitness.R;
import com.example.co_fitness.Utils.SignalSingleton;
import com.example.co_fitness.databinding.FragmentChooseSportsTypeBinding;

public class ChooseSportTypeFragment extends Fragment {

    private FragmentChooseSportsTypeBinding binding;

    private String chosen="";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChooseSportsTypeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initViews();
        return root;
    }

    private void initViews() {
        binding.chooseTypeBTNContinue.setOnClickListener(view -> continueToFillDetails());
        binding.chooseTypeBTNTennis.setOnClickListener(v -> chooseSportType(DB_TENNIS));
        binding.chooseTypeBTNRunning.setOnClickListener(v -> chooseSportType(DB_RUNNING));
        binding.chooseTypeBTNFootball.setOnClickListener(v -> chooseSportType(DB_FOOTBALL));
        binding.chooseTypeBTNBasketball.setOnClickListener(v -> chooseSportType(DB_BASKETBALL));
    }

    private void continueToFillDetails() {
        if (chosen.isEmpty()) {
            SignalSingleton.getInstance().toast("You must choose an option");

            //TODO: show error message
        } else{
            Intent intent = new Intent(getActivity().getBaseContext(), MainPageActivity.class);
            intent.putExtra(DB_SPORT_TYPE, chosen);
            ((MainPageActivity)getActivity()).replaceFragments(CreateOfferFragment.class, DB_SPORT_TYPE,chosen,"create");
        }
    }

    private void chooseSportType(String sportType) {
        chosen = sportType;

        switch (sportType) {
            case DB_TENNIS:
                binding.chooseTypeBTNTennis.setBackground(getResources().getDrawable(R.drawable.circle_tennis));
                binding.chooseTypeBTNFootball.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                binding.chooseTypeBTNBasketball.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                binding.chooseTypeBTNRunning.setBackground(getResources().getDrawable(R.drawable.circle_grey));

                break;
            case DB_FOOTBALL:
                binding.chooseTypeBTNFootball.setBackground(getResources().getDrawable(R.drawable.circle_football));
                binding.chooseTypeBTNTennis.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                binding.chooseTypeBTNBasketball.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                binding.chooseTypeBTNRunning.setBackground(getResources().getDrawable(R.drawable.circle_grey));


                break;
            case DB_BASKETBALL:
                binding.chooseTypeBTNBasketball.setBackground(getResources().getDrawable(R.drawable.circle_basketball));
                binding.chooseTypeBTNTennis.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                binding.chooseTypeBTNFootball.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                binding.chooseTypeBTNRunning.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                break;
            case DB_RUNNING:
                binding.chooseTypeBTNRunning.setBackground(getResources().getDrawable(R.drawable.circle_running));
                binding.chooseTypeBTNTennis.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                binding.chooseTypeBTNFootball.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                binding.chooseTypeBTNBasketball.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                break;
        }
    }
}