package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Base64;
import java.io.InputStream;
import java.io.IOException;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.POST;
import retrofit2.http.Body;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inhlwathibytes.peertutoringapp.models.RegisterTutorRequest;
import com.inhlwathibytes.peertutoringapp.network.RetrofitClient;
import com.inhlwathibytes.peertutoringapp.network.TutorshipApi;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;

public class RegisterAsTutor extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ViewFlipper viewFlipper;
    private EditText editTextBio, editTextQualifications, editTextAchievements;
    private CheckBox checkBoxIsAvailable;
    private EditText editTextYearsOfExperience;
    private LinearLayout languageCheckboxGroup;
    private ImageView imageViewProfilePhoto;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;


    private Button btnStartRegistration, btnNext1, btnNext2, btnBack1, btnBack2, btnSubmit, btnUploadPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_register_as_tutor);
        initializeViews();
        setupListeners();
        loadLanguages(); // simulate loading checkboxes
    }

    private void initializeViews() {
        viewFlipper = findViewById(R.id.viewFlipper);
        editTextBio = findViewById(R.id.editTextBio);
        editTextQualifications = findViewById(R.id.editTextQualifications);
        editTextAchievements = findViewById(R.id.editTextAchievements);
        checkBoxIsAvailable = findViewById(R.id.checkBoxIsAvailable);
        editTextYearsOfExperience = findViewById(R.id.editTextYearsOfExperience);
        languageCheckboxGroup = findViewById(R.id.languageCheckboxGroup);
        imageViewProfilePhoto = findViewById(R.id.imageViewProfilePhoto);

        btnStartRegistration = findViewById(R.id.btnStartRegistration);
        btnNext1 = findViewById(R.id.btnNext1);
        btnNext2 = findViewById(R.id.btnNext2);
        btnBack1 = findViewById(R.id.btnBack1);
        btnBack2 = findViewById(R.id.btnBack2);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
    }

    private void setupListeners() {
        // ViewFlipper navigation
        btnNext1.setOnClickListener(v -> {
            String bio = editTextBio.getText().toString().trim();
            String qualification = editTextQualifications.getText().toString().trim();
            String achievement = editTextAchievements.getText().toString().trim();

            if(qualification.length() == 0) {
                editTextQualifications.setError("Qualification field empty");
                editTextQualifications.requestFocus();
            } else if (achievement.length() == 0){
                editTextAchievements.setError("Achievement field empty");
                editTextAchievements.requestFocus();
            }
            else if (bio.length() < 40) {
                editTextBio.setError("Bio must be at least 40 characters");
                editTextBio.requestFocus();
            } else {
                viewFlipper.showNext();
            }
        });

        btnNext2.setOnClickListener(v -> {
            int childCount = languageCheckboxGroup.getChildCount();
            boolean languageSelected = false;
            String yearsOfexperience = editTextYearsOfExperience.getText().toString().trim();


            for (int i = 0; i < childCount; i++) {
                View child = languageCheckboxGroup.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child;
                    if (checkBox.isChecked()) {
                        languageSelected = true;
                        break;
                    }
                }
            }

            if (!languageSelected) {
                Toast.makeText(this, "Please select at least one language", Toast.LENGTH_SHORT).show();
            }
            else if(yearsOfexperience.length() == 0){
                editTextYearsOfExperience.setError("Years of Experience field empty");
                editTextYearsOfExperience.requestFocus();
            }else {
                viewFlipper.showNext();
            }
        });

        btnStartRegistration.setOnClickListener(v -> viewFlipper.showNext());
        btnBack1.setOnClickListener(v -> viewFlipper.showPrevious());
        btnBack2.setOnClickListener(v -> viewFlipper.showPrevious());

        // Image upload button
        btnUploadPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Submit button (to be connected to API call later)
        btnSubmit.setOnClickListener(v -> {
            // 1. Collect form values
            String bio = editTextBio.getText().toString().trim();
            String qualifications = editTextQualifications.getText().toString().trim();
            String achievements = editTextAchievements.getText().toString().trim();
            boolean isAvailable = checkBoxIsAvailable.isChecked();

            int yearsOfExperience = 0;
            try {
                yearsOfExperience = Integer.parseInt(editTextYearsOfExperience.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid experience value", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Integer> selectedLanguageIds = new ArrayList<>();
            for (int i = 0; i < languageCheckboxGroup.getChildCount(); i++) {
                View child = languageCheckboxGroup.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child;
                    if (checkBox.isChecked()) {
                        selectedLanguageIds.add((Integer) checkBox.getTag()); // assumes tag is an Integer
                    }
                }
            }


//            List<Integer> selectedSubjectIds = new ArrayList<>();
//            for (int i = 0; i < subjectCheckboxGroup.getChildCount(); i++) {
//                View child = subjectCheckboxGroup.getChildAt(i);
//                if (child instanceof CheckBox checkBox && checkBox.isChecked()) {
//                    selectedSubjectIds.add((Integer) checkBox.getTag());
//                }
//            }

            String profilePhotoBase64 = null;
            if (selectedImageBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageBytes = stream.toByteArray();
                profilePhotoBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            }

            // 2. Build request object
            RegisterTutorRequest request = new RegisterTutorRequest(
                    bio,
                    qualifications,
                    achievements,
                    isAvailable,
                    yearsOfExperience,
                    selectedLanguageIds,
                    profilePhotoBase64
            );




            // 3. Get token from SharedPreferences
            String token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("jwt_token", null);

            if (token == null) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Send request using Retrofit
            TutorshipApi tutorshipApi = RetrofitClient.getRetrofitInstance().create(TutorshipApi.class);
            Call<Void> call = tutorshipApi.registerTutorship("Bearer " + token, request);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
//                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    String jsonRequest = gson.toJson(request);
//                    Log.d("TutorRequest", jsonRequest);
//
//                    Log.d("Check", "Reached here");

                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterAsTutor.this, "Tutorship created!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterAsTutor.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterAsTutor.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void loadLanguages() {
        // Simulated hardcoded language list; you'd normally fetch this from an API
        String[] languageNames = {"English", "Zulu", "Xhosa", "Afrikaans"};
        int[] languageIds = {1, 2, 3, 4}; // match backend IDs

        for (int i = 0; i < languageNames.length; i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(languageNames[i]);
            checkBox.setTag(languageIds[i]); // keep the language ID
            languageCheckboxGroup.addView(checkBox);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageViewProfilePhoto.setImageBitmap(selectedImageBitmap); // optional: update the preview
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

