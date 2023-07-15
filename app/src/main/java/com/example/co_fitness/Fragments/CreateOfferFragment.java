package com.example.co_fitness.Fragments;

import static com.example.co_fitness.Constants.DB_OFFER_ID;
import static com.example.co_fitness.Constants.DB_SPORT_TYPE;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.co_fitness.Activities.MainPageActivity;
import com.example.co_fitness.Constants;
import com.example.co_fitness.Model.CurrentUser;
import com.example.co_fitness.Model.Offer;
import com.example.co_fitness.Utils.SignalSingleton;
import com.example.co_fitness.databinding.FragmentCreateOfferBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.LabelFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CreateOfferFragment extends Fragment {

    private FragmentCreateOfferBinding binding;
    private DatabaseReference mDatabase;
    private int time_hour, time_minute;
    private String type;
    private String id;
    private Offer offer;
    private String[] levelLabels = {"Beginner", "Average", "Pro"};


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateOfferBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initViews();
        initListeners();
        return root;
    }

    private void initViews(){
        Bundle arguments = getArguments();
        if (arguments != null) {
            String action = arguments.getString("action");
            if(action != "")
                if(action.equals("edit")) {
                    id = arguments.getString(DB_OFFER_ID);
                    getOfferFromDB(id);
                }else{
                    type = arguments.getString(DB_SPORT_TYPE);
                    binding.createBTNEdit.setVisibility(View.GONE);
                }
        }
        binding.createLBLSportType.setText("Sport Type: "+getArguments().getString(DB_SPORT_TYPE));
        binding.createSLDLevel.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < levelLabels.length) {
                    return levelLabels[index];
                }
                return "";
            }
        });
    }

    private void initListeners(){
        binding.createBTNEdit.setOnClickListener(view -> saveChanges());
        binding.createBTNCreate.setOnClickListener(view -> checkOfferDetails(id));
        binding.createBTNBack.setOnClickListener(view -> selectWhereToMove(id));
        binding.createBTNTime.setOnClickListener(view -> setTime());
        binding.createBTNDate.setOnClickListener(view -> setDate());
        binding.createCBCost.setOnClickListener(view -> setCost());
    }

    private void initOffer() {
        if(offer.getType() != null)
            binding.createLBLSportType.setText(offer.getType());
        if(offer.getDate() != null && !offer.getDate().isEmpty())
            binding.createTILDate.getEditText().setText(offer.getDate());
        if(offer.getTime() != null && !offer.getTime().isEmpty())
            binding.createTILTime.getEditText().setText(offer.getTime());
        if(offer.getPlace() != null && !offer.getPlace().isEmpty())
            binding.createEDTWhere.setText(offer.getPlace());
        if(offer.getLevel() != null && ! offer.getLevel().isEmpty())
            binding.createSLDLevel.setValue(getNumOfLevel(offer.getLevel()));
        if(offer.getCost() != null && !offer.getCost().isEmpty()){
            if(offer.getCost().equals("Free"))
                binding.createCBCost.setChecked(true);
            else
                binding.createTFCost.setText(offer.getCost());
        }
        if(offer.getCapacity()!= 0)
            binding.createTFCapacity.setText(String.valueOf(offer.getCapacity()));
        if(offer.getDescription() != null && !offer.getDescription().isEmpty())
            binding.createTFDescription.setText(offer.getDescription());

        binding.createBTNCreate.setVisibility(View.GONE);
        binding.createBTNEdit.setVisibility(View.VISIBLE);

    }
    private void saveChanges(){
        checkOfferDetails(id);
    }

    public void getOfferFromDB(String offerId) {
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS);
        mDatabase.child(offerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    offer = snapshot.getValue(Offer.class);
                    initOffer();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void selectWhereToMove(String id){
        if(id != null && !id.isEmpty())
            ((MainPageActivity)getActivity()).replaceFragments(MyOffersFragment.class, "","", "");
        else
            ((MainPageActivity)getActivity()).replaceFragments(ChooseSportTypeFragment.class, "","", "");
    }
    private void setTime(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), android.R.style.Theme_Holo_Dialog_MinWidth,new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                time_hour = hour;
                time_minute = minute;
                String time = hour + ":" + minute;
                SimpleDateFormat f24hours = new SimpleDateFormat("HH:mm");
                try {
                    Date date = f24hours.parse(time);
                    binding.createTILTime.getEditText().setText(f24hours.format(date));

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        },12,0,true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(time_hour,time_minute);
        timePickerDialog.show();
    }

    private void setDate(){
        MaterialDatePicker datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
        datePicker.show(getParentFragmentManager(), "tag");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            binding.createTILDate.getEditText().setText(simpleDateFormat.format(new Date((long)selection)));
        });
    }
    private void setCost(){
        if(binding.createCBCost.isChecked())
            binding.createLAYCost.setVisibility(View.INVISIBLE);
        else
            binding.createLAYCost.setVisibility(View.VISIBLE);
    }

    private void checkOfferDetails(String id) {
        boolean flag = true;
        if (binding.createTILDate.getEditText().getText().toString().equals("00/00/0000")) {
            SignalSingleton.getInstance().toast("You must choose a date");
            flag = false;
        } else if (binding.createTILTime.getEditText().getText().toString().equals("00:00")) {
            SignalSingleton.getInstance().toast("You must choose a time");
            flag = false;
        } else if (!isDateAndTimeValid(binding.createTILDate.getEditText().getText().toString(), binding.createTILTime.getEditText().getText().toString())) {
            SignalSingleton.getInstance().toast("You cannot select a past date or time");
            flag = false;
        }else if (binding.createEDTWhere.getText().toString().isEmpty()) {
            SignalSingleton.getInstance().toast("You must choose a place");
            flag = false;
        } else if (!binding.createCBCost.isChecked() && binding.createTFCost.getText().toString().isEmpty()) {
            SignalSingleton.getInstance().toast("You must enter if there is a cost");
            flag = false;
        } else if (binding.createTFCapacity.getText().toString().isEmpty()) {
            SignalSingleton.getInstance().toast("You must enter capacity");
            flag = false;
        }
        if (flag) {
            if(id != null && !id.isEmpty() && !id.equals(""))
                createOffer(id);
            else
                createOffer("");
        }
    }

    private boolean isDateAndTimeValid(String dateStr, String timeStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date currentDateTime = new Date();
            Date selectedDateTime = dateFormat.parse(dateStr + " " + timeStr);
            int dateComparison = selectedDateTime.compareTo(currentDateTime);
            if (dateComparison == 0) {
                return selectedDateTime.after(currentDateTime); // Return true if the selected time is after the current time
            }
            // Compare the selected date and time with the current date and time
            return dateComparison > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createOffer(String id) {
        Offer newOffer;
        String time  = binding.createTILTime.getEditText().getText().toString();
        String date  = binding.createTILDate.getEditText().getText().toString();
        String where = binding.createEDTWhere.getText().toString();
        String level = getLevel();
        String cost  = getCost();
        int capacity = Integer.parseInt(String.valueOf(binding.createTFCapacity.getText()));
        String description = binding.createTFDescription.getText().toString();

        if(id.equals(""))
            newOffer = new Offer(CurrentUser.getInstance().getUserProfile().getUid(), type, capacity,description, date, time, where, level,cost);
        else {
            newOffer = offer;
            newOffer.setTime(time);
            newOffer.setDate(date);
            newOffer.setPlace(where);
            newOffer.setLevel(level);
            newOffer.setCost(cost);
            newOffer.setCapacity(capacity);
            newOffer.setDescription(description);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DB_OFFERS);
        mDatabase.child(newOffer.getId()).setValue(newOffer);
        CurrentUser.getInstance().getUserProfile().addOffer(newOffer.getId());
        if(id.equals("")) {
            SignalSingleton.getInstance().toast("Offer Created");
            saveOfferToUser(newOffer);
        }
        else
            SignalSingleton.getInstance().toast("Offer Updated");
    }

    private String getLevel() {
        switch ((int) binding.createSLDLevel.getValue()) {
            case 0:
                return "Beginner";
            case 1:
                return "Average";
            case 2:
                return "Pro";
        }
        return "";
    }

    private int getNumOfLevel(String level){
        switch (level){
            case "Beginner":
                return 0;
            case "Average":
                return 1;
            case "Pro":
                return 2;
        }
        return 0;

    }
    private void saveOfferToUser(Offer newOffer) {
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
        mDatabase.child(CurrentUser.getInstance().getUserProfile().getUid()).setValue(CurrentUser.getInstance().getUserProfile());
        ArrayList<String> offers = CurrentUser.getInstance().getUserProfile().getOffers();
        if(offers != null) {
            offers.add(newOffer.getId());
            mDatabase.child(CurrentUser.getInstance().getUserProfile().getUid()).child("offers").setValue(offers);
        }
//        else
//            SignalSingleton.getInstance().toast("Error saving offer to user");
    }

    private String getCost() {
        if(binding.createCBCost.isChecked())
            return "Free";
        return binding.createTFCost.getText().toString();
    }


    private void setValuesNull() {
        binding.createEDTWhere.setText(null);
        binding.createTFCapacity.setText(null);
        binding.createTFDescription.setText(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        setValuesNull();
    }
}