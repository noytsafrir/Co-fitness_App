package com.example.co_fitness.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.co_fitness.Activities.LoginActivity;
import com.example.co_fitness.Activities.MainPageActivity;
import com.example.co_fitness.Constants;
import com.example.co_fitness.Model.CurrentUser;
import com.example.co_fitness.Model.User;
import com.example.co_fitness.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private StorageReference storageReference;
    private static final int IMAGE_UPLOAD_REQUEST_CODE = 1;
    private Uri imageUri;
    private String imageUrl;
    private String fileName;
    private boolean isImageUploaded = true;
    private FirebaseDatabase firebaseDB;
    private CircleImageView user_IMG_profile;
    private MaterialButton user_BTN_date_of_birth;
    private CircularProgressIndicator user_CPI_upload;
    private MaterialButton user_BTN_save;
    private MaterialButton user_BTN_return;
    private TextInputLayout user_EDT_phone_number;
    private TextInputLayout user_EDT_address;
    private TextInputLayout user_EDT_date_of_birth;
    private TextInputLayout user_EDT_name;
    private AppCompatSpinner user_SP_gender;

    private ActivityResultLauncher<String> mGetContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            imageUri = uri;
                            user_IMG_profile.setImageURI(imageUri);
                            setImageNotUploaded();
                            uploadImage();
                        }
                    }
                });
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDB = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(Constants.DB_PROFILE_IMAGES);
        initView(view);
        setButtonsListener();
        initUser();
        setImageUploaded();
    }

    private void initView(View view) {
        user_IMG_profile = view.findViewById(R.id.user_IMG_profile);
        user_BTN_date_of_birth = view.findViewById(R.id.user_BTN_date_of_birth);
        user_CPI_upload = view.findViewById(R.id.user_CPI_upload);
        user_BTN_save = view.findViewById(R.id.user_BTN_save);
        user_BTN_return = view.findViewById(R.id.user_BTN_return);
        user_EDT_phone_number = view.findViewById(R.id.user_EDT_phone_number);
        user_EDT_address = view.findViewById(R.id.user_EDT_address);
        user_EDT_date_of_birth = view.findViewById(R.id.user_EDT_date_of_birth);
        user_EDT_name = view.findViewById(R.id.user_EDT_name);
        user_SP_gender = view.findViewById(R.id.user_SP_gender);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null || CurrentUser.getInstance().getUserProfile() == null)
            goToLoginActivity();
        initUser();
    }

    private void setButtonsListener() {
        user_IMG_profile.setOnClickListener(v -> checkPermissionAndUploadImage());
        user_BTN_save.setOnClickListener(v -> updateUserProfile(imageUrl));
        user_BTN_return.setOnClickListener(v -> goToMainActivity());
        user_BTN_date_of_birth.setOnClickListener(v -> setDate());
    }

    private void setDate() {
        MaterialDatePicker datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
        datePicker.show(getParentFragmentManager(), "tag");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            user_EDT_date_of_birth.getEditText().setText(simpleDateFormat.format(new Date((long) selection)));
        });
    }

    private void updateUserProfile(String imageUrl) {
        if (isImageUploaded) {
            User userProfile = CurrentUser.getInstance().getUserProfile();
            String name = user_EDT_name.getEditText().getText().toString();
            String gender = user_SP_gender.getSelectedItem().toString();
            String dateOfBirth = user_EDT_date_of_birth.getEditText().getText().toString();
            String address = user_EDT_address.getEditText().getText().toString();
            String phoneNumber = user_EDT_phone_number.getEditText().getText().toString();


            userProfile
                    .setName(name)
                    .setGender(gender)
                    .setDateOfBirth(dateOfBirth)
                    .setImage(imageUrl)
                    .setAddress(address)
                    .setPhoneNumber(phoneNumber)
                    .setIsRegistered(true);

            firebaseDB.getInstance().getReference(Constants.DB_USERS).child(userProfile.getUid()).setValue(userProfile);
            CurrentUser.getInstance().setUserProfile(userProfile);
            goToMainActivity();
            requireActivity().finish();
        } else {
            Toast.makeText(requireContext(), "Please upload the image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        user_CPI_upload.setVisibility(View.VISIBLE);
        this.fileName = CurrentUser.getInstance().getUserProfile().getUid();
        StorageReference reference = storageReference.child(this.fileName);
        reference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                    downloadUrlTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            imageUrl = task.getResult().toString();
                            setImageUploaded();
                            user_CPI_upload.setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    user_CPI_upload.setVisibility(View.INVISIBLE);
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }


    private void checkPermissionAndUploadImage() {
        // Check if permission to read external storage is granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if not granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_UPLOAD_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, IMAGE_UPLOAD_REQUEST_CODE);
            }
        } else {
            // Permission already granted, start image selection process
            mGetContent.launch("image/*");
//            openImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMAGE_UPLOAD_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start image selection process
                openImagePicker();
            } else {
                // Permission denied
                Log.e("Permission Denied", "Storage permission denied");
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_UPLOAD_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_UPLOAD_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            imageUri = data.getData();
            user_IMG_profile.setImageURI(imageUri);
            setImageNotUploaded();
            uploadImage();
        }
    }

    private void setImageUploaded() {
        isImageUploaded = true;
        user_IMG_profile.setImageURI(imageUri);
    }

    private void setImageNotUploaded() {
        isImageUploaded = false;
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(requireActivity(), MainPageActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void initUser() {
        User userProfile = CurrentUser.getInstance().getUserProfile();

        if (userProfile.getName() != null && !userProfile.getName().isEmpty())
            user_EDT_name.getEditText().setText(userProfile.getName());
        if (userProfile.getPhoneNumber() != null && !userProfile.getPhoneNumber().isEmpty())
            user_EDT_phone_number.getEditText().setText(userProfile.getPhoneNumber());
        if (userProfile.getAddress() != null && !userProfile.getAddress().isEmpty())
            user_EDT_address.getEditText().setText(userProfile.getAddress());
        if (userProfile.getDateOfBirth() != null && !userProfile.getDateOfBirth().isEmpty())
            user_EDT_date_of_birth.getEditText().setText(userProfile.getDateOfBirth());
        if (userProfile.getGender() != null && !userProfile.getGender().isEmpty())
            user_SP_gender.setSelection(getGender(userProfile.getGender()));
        if (userProfile.getImage() != null && !userProfile.getImage().isEmpty()) {
            imageUrl = userProfile.getImage();
            Glide.with(this)
                    .load(userProfile.getImage())
                    .into(user_IMG_profile);
        }

        if (userProfile.getIsRegistered())
            user_BTN_return.setVisibility(View.VISIBLE);
        else
            user_BTN_return.setVisibility(View.GONE);
    }

    private int getGender(String gender) {
        switch (gender) {
            case "Male":
                return 0;
            case "Female":
                return 1;
            case "Undefined":
                return 2;
        }
        return 0;
    }
}