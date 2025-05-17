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
import androidx.appcompat.widget.SearchView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import java.util.ArrayList;
import java.util.Arrays;
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

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inhlwathibytes.peertutoringapp.models.Language;
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
    private EditText editTextBio, editTextHighestAchievement, editTextPostalCode, editTextProvince, editTextCity, editTextStreet, editTextAge;
    private CheckBox checkBoxIsAvailable;
    private EditText editTextYearsOfExperience;
    private LinearLayout languageCheckboxGroup;
    private ImageView imageViewProfilePhoto;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;
    private ChipGroup chipGroup;
    private Button selectLanguagesButton;


    private Button btnStartRegistration, btnNext1, btnNext2, btnBack1, btnBack2, btnSubmit, btnUploadPhoto;

    List<Integer> selectedLanguageIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_register_as_tutor);
        initializeViews();
        setupListeners();
        loadLanguages(); // simulate loading checkboxes
    }

    private static final List<Language> ALL_LANGUAGES = Arrays.asList(
            new Language(1, "English"),   new Language(2, "Zulu"),
            new Language(3, "Afrikaans"), new Language(4, "Spanish"),
            new Language(5, "French"),    new Language(6, "Mandarin"),
            new Language(7, "Hindi"),     new Language(8, "Arabic"),
            new Language(9, "Portuguese"),new Language(10, "Bengali"),
            new Language(11, "Russian"),  new Language(12, "Japanese"),
            new Language(13, "German"),   new Language(14, "Swahili"),
            new Language(15, "Urdu"),     new Language(16, "Turkish"),
            new Language(17, "Korean"),   new Language(18, "Italian"),
            new Language(19, "Vietnamese"),new Language(20, "Persian")
    );

    private void showLanguageMultiSelectDialog() {
        // inflate custom view
        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_multiselect_languages, null);

        SearchView searchView = dialogView.findViewById(R.id.searchView);
        ListView listView = dialogView.findViewById(R.id.languageListView);

        // adapter of language names
        ArrayAdapter<Language> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                new ArrayList<>(ALL_LANGUAGES)
        );
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        // restore any previous checks
        boolean[] checked = new boolean[ALL_LANGUAGES.size()];
        for (int i = 0; i < ALL_LANGUAGES.size(); i++) {
            if (selectedLanguageIds.contains(ALL_LANGUAGES.get(i).getId())) {
                checked[i] = true;
            }
        }
        for (int i = 0; i < checked.length; i++) {
            listView.setItemChecked(i, checked[i]);
        }

        // filter as user types
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String s) { return false; }
            @Override public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Languages")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    // read checked items
                    selectedLanguageIds.clear();
                    chipGroup.removeAllViews();
                    for (int i = 0; i < listView.getCount(); i++) {
                        if (listView.isItemChecked(i)) {
                            Language lang = adapter.getItem(i);
                            selectedLanguageIds.add(lang.getId());
                            addChip(lang);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addChip(Language lang) {
        Chip chip = new Chip(this);
        chip.setText(lang.getName());
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(c -> {
            chipGroup.removeView(chip);
            selectedLanguageIds.remove(Integer.valueOf(lang.getId()));
        });
        chipGroup.addView(chip);
    }

    private void initializeViews() {
        viewFlipper = findViewById(R.id.viewFlipper);
        editTextBio = findViewById(R.id.editTextBio);
        editTextHighestAchievement = findViewById(R.id.editTextHighestAchievement);
        editTextCity = findViewById(R.id.editTextCity);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextAge = findViewById(R.id.editTextAge);
//        editTextQualifications = findViewById(R.id.editTextQualifications);
//        editTextAchievements = findViewById(R.id.editTextAchievements);
        checkBoxIsAvailable = findViewById(R.id.checkBoxIsAvailable);
        editTextYearsOfExperience = findViewById(R.id.editTextYearsOfExperience);
        languageCheckboxGroup = findViewById(R.id.languageCheckboxGroup);
        imageViewProfilePhoto = findViewById(R.id.imageViewProfilePhoto);
        chipGroup = findViewById(R.id.languageChipGroup);
        selectLanguagesButton = findViewById(R.id.selectLanguagesButton);
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
            String highestAchievement = editTextHighestAchievement.getText().toString().trim();
            String street = editTextStreet.getText().toString().trim();
            String city = editTextCity.getText().toString().trim();
            String province = editTextProvince.getText().toString().trim();
            String postalCode = editTextPostalCode.getText().toString().trim();
            String age = editTextAge.getText().toString().trim();


            if (highestAchievement.length() == 0){
                editTextHighestAchievement.setError("Achievement field empty");
                editTextHighestAchievement.requestFocus();
            }
            else if (bio.length() < 40) {
                editTextBio.setError("Bio must be at least 40 characters");
                editTextBio.requestFocus();
            }else if (age.length() == 0) {
                editTextAge.setError("Age is required");
                editTextAge.requestFocus();
            } else if (street.length() == 0 || city.length() == 0 || province.length() == 0 || postalCode.length() == 0) {
                Toast.makeText(this, "Address is not complete", Toast.LENGTH_SHORT).show();
            } else {
                viewFlipper.showNext();
            }
        });



        selectLanguagesButton.setOnClickListener(v -> {
            try {
                showLanguageMultiSelectDialog();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "f-" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

//        btnNext2.setOnClickListener(v -> {
//            int childCount = languageCheckboxGroup.getChildCount();
//            boolean languageSelected = false;
//            String yearsOfexperience = editTextYearsOfExperience.getText().toString().trim();
//
//
//            for (int i = 0; i < childCount; i++) {
//                View child = languageCheckboxGroup.getChildAt(i);
//                if (child instanceof CheckBox) {
//                    CheckBox checkBox = (CheckBox) child;
//                    if (checkBox.isChecked()) {
//                        languageSelected = true;
//                        break;
//                    }
//                }
//            }
//
//            if (selectedLanguageIds.isEmpty()) {
//                Toast.makeText(this, "Please select at least one language", Toast.LENGTH_SHORT).show();
//            } else if (editTextYearsOfExperience.getText().toString().trim().isEmpty()) {
//                editTextYearsOfExperience.setError("Years of Experience field empty");
//                editTextYearsOfExperience.requestFocus();
//            }else {
//                viewFlipper.showNext();
//            }
//        });

        btnNext2.setOnClickListener(v -> {
            String yearsOfExperience = editTextYearsOfExperience.getText().toString().trim();

            if (selectedLanguageIds.isEmpty()) {
                Toast.makeText(this, "Please select at least one language", Toast.LENGTH_SHORT).show();
            } else if (yearsOfExperience.isEmpty()) {
                editTextYearsOfExperience.setError("Years of Experience field empty");
                editTextYearsOfExperience.requestFocus();
            } else {
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
            String highestAchievement = editTextHighestAchievement.getText().toString().trim();
            String street = editTextStreet.getText().toString().trim();
            String city = editTextCity.getText().toString().trim();
            String province = editTextProvince.getText().toString().trim();
            String postalCode = editTextPostalCode.getText().toString().trim();
            boolean isAvailable = checkBoxIsAvailable.isChecked();

            int age = 0;
            try {
                age = Integer.parseInt(editTextAge.getText().toString().trim());
            } catch (NumberFormatException e) {
                // Handle invalid number input (e.g. show error)
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
                return;
            }

            int yearsOfExperience = 0;
            try {
                yearsOfExperience = Integer.parseInt(editTextYearsOfExperience.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid experience value", Toast.LENGTH_SHORT).show();
                return;
            }



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

            String profilePhotoPath = null;
            if (selectedImageBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageBytes = stream.toByteArray();
                profilePhotoPath = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            }

            // 2. Build request object
            RegisterTutorRequest request = new RegisterTutorRequest(
                    bio,
                    highestAchievement,
                    street,
                    city,
                    province,
                    age,
                    postalCode,
                    isAvailable,
                    yearsOfExperience,
                    selectedLanguageIds,
                    profilePhotoPath
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
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonRequest = gson.toJson(request);
//                    Log.d("TutorRequest", jsonRequest);
//
//                    Log.d("Check", "Reached here");

                    if (response.isSuccessful()) {
                        Log.d("response success", response.toString());
                        Toast.makeText(RegisterAsTutor.this, "Tutorship created!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterAsTutor.this, SplashActivity.class));
                        finish();
                    } else {
                        Log.d("response failure", response.toString());
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

