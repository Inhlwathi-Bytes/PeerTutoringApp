package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.net.Uri;
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

    private Button btnNext1, btnNext2, btnBack1, btnBack2, btnSubmit, btnUploadPhoto;

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

//        btnNext2.setOnClickListener(v -> viewFlipper.showNext());
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
            // Handle form submission logic here
            Toast.makeText(RegisterAsTutor.this, "Form Submitted", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageViewProfilePhoto.setImageURI(selectedImageUri);
        }
    }
}

