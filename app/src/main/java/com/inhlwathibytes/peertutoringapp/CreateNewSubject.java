package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.inhlwathibytes.peertutoringapp.models.Language;
import com.inhlwathibytes.peertutoringapp.network.CreateSubjectRequestApi;
import com.inhlwathibytes.peertutoringapp.models.CreateSubjectRequest;
import com.inhlwathibytes.peertutoringapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewSubject extends BaseActivity {

    private ViewFlipper viewFlipper;
    private Button nextButton, backButton, submitButton;
    private EditText subjectNameInput, deliveryModeInput, levelInput, hourlyRateInput, availabilityInput, outlineInput;

    // Optional inputs (you’ll connect these later for file handling)
    private String coverImagePath = null;
    private String introVideoLink = null;
    private ChipGroup chipGroup;
    private Button selectLanguagesButton;
    List<Integer> selectedLanguageIds = new ArrayList<>();


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_create_new_subject);



        // ViewFlipper + Navigation
        viewFlipper = findViewById(R.id.viewFlipper);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
        submitButton = findViewById(R.id.submitButton);
        chipGroup = findViewById(R.id.languageChipGroup);
        selectLanguagesButton = findViewById(R.id.selectLanguagesButton);

        // Step 1 inputs
        subjectNameInput = findViewById(R.id.subjectName);
        availabilityInput = findViewById(R.id.availability);
        deliveryModeInput  = findViewById(R.id.deliveryMode);

        // Step 2 inputs
        levelInput = findViewById(R.id.level);
        hourlyRateInput = findViewById(R.id.hourlyRate);
        outlineInput = findViewById(R.id.outline);


        selectLanguagesButton.setOnClickListener(v -> {
            try {
                showLanguageMultiSelectDialog();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "f-" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        updateButtonVisibility();

        nextButton.setOnClickListener(v -> {
            if (canProceedToNextStep()) {
                viewFlipper.showNext();
                updateButtonVisibility();
            }
        });

        backButton.setOnClickListener(v -> {
            viewFlipper.showPrevious();
            updateButtonVisibility();
        });

        submitButton.setOnClickListener(v -> {
            if (validateAllFields()) {
                submitSubject();
            }
        });
    }

    private boolean canProceedToNextStep() {
        int currentStep = viewFlipper.getDisplayedChild();

        if (currentStep == 0) {
            if (isEmpty(subjectNameInput)) {
                showToast("Please enter the subject name");
                return false;
            }
            if (selectedLanguageIds.isEmpty()) {
                Toast.makeText(this, "Please select at least one language", Toast.LENGTH_SHORT).show();
            }
            if (isEmpty(availabilityInput)) {
                showToast("Please enter availability");
                return false;
            }
            if (isEmpty(deliveryModeInput)) {
                showToast("Please enter mode of delivery");
                return false;
            }

        }

        if (currentStep == 1) {
            if (isEmpty(levelInput)) {
                showToast("Please enter the level");
                return false;
            }
            if (isEmpty(outlineInput)) {
                showToast("Please enter the outline");
                return false;
            }
            if (isEmpty(hourlyRateInput)) {
                showToast("Please enter the hourly rate");
                return false;
            }
        }

        return true;
    }

    private boolean validateAllFields() {
        return !isEmpty(subjectNameInput)
                && !isEmpty(levelInput)
                && !isEmpty(hourlyRateInput)
                && !isEmpty(availabilityInput)
                && !isEmpty(outlineInput);
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void updateButtonVisibility() {
        int currentIndex = viewFlipper.getDisplayedChild();
        int lastIndex = viewFlipper.getChildCount() - 1;

        backButton.setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
        nextButton.setVisibility(currentIndex == lastIndex ? View.GONE : View.VISIBLE);
        submitButton.setVisibility(currentIndex == lastIndex ? View.VISIBLE : View.GONE);
    }

    private void submitSubject() {
        String subjectName = subjectNameInput.getText().toString().trim();
        String level = levelInput.getText().toString().trim();
        String hourlyRateStr = hourlyRateInput.getText().toString().trim();
        String availability = availabilityInput.getText().toString().trim();
        String outline = outlineInput.getText().toString().trim();
        String deliveryMode = deliveryModeInput.getText().toString().trim();

        double hourlyRate;

        try {
            hourlyRate = Double.parseDouble(hourlyRateStr);
        } catch (NumberFormatException e) {
            showToast("Hourly rate must be a number");
            return;
        }

        CreateSubjectRequest request = new CreateSubjectRequest(
                subjectName,
                level,
                hourlyRate,
                availability,
                outline,
                deliveryMode,
                coverImagePath,
                introVideoLink
        );

        String token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateSubjectRequestApi api = RetrofitClient.getRetrofitInstance().create(CreateSubjectRequestApi.class);
        Call<Void> call = api.createSubject("Bearer " + token, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Subject created successfully!");
                    startActivity(new Intent(CreateNewSubject.this, MyOfferedSubjects.class));
                } else {
                    showToast("Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Submission failed: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(CreateNewSubject.this, message, Toast.LENGTH_SHORT).show();
    }
}
