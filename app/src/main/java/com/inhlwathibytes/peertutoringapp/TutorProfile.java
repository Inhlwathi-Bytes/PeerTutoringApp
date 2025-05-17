package com.inhlwathibytes.peertutoringapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inhlwathibytes.peertutoringapp.network.MyProfileApi;
import com.inhlwathibytes.peertutoringapp.network.RetrofitClient;
import com.inhlwathibytes.peertutoringapp.models.MyProfileTutorResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TutorProfile extends BaseActivity {

    private TextView textViewName, textViewEmail, textViewBio,
            textViewAge, textViewExperience, textViewAchievement,
            textViewRating, textViewAvailability, textViewStreet,
            textViewCity, textViewProvince, textViewPostalCode;

    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_tutor_profile);

        // Bind views
        profileImage = findViewById(R.id.profileImage);
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewBio = findViewById(R.id.textViewBio);
        textViewAge = findViewById(R.id.textViewAge);
        textViewExperience = findViewById(R.id.textViewExperience);
        textViewAchievement = findViewById(R.id.textViewAchievement);
        textViewRating = findViewById(R.id.textViewRating);
        textViewAvailability = findViewById(R.id.textViewAvailability);
        textViewStreet = findViewById(R.id.textViewStreet);
        textViewCity = findViewById(R.id.textViewCity);
        textViewProvince = findViewById(R.id.textViewProvince);
        textViewPostalCode = findViewById(R.id.textViewPostalCode);

        fetchTutorProfile();
    }

    private void fetchTutorProfile() {
        MyProfileApi apiService = RetrofitClient.getRetrofitInstance().create(MyProfileApi.class);

        // 3. Get token from SharedPreferences
        String token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<MyProfileTutorResponse> call = apiService.getTutorProfile("Bearer " + token);

        call.enqueue(new Callback<MyProfileTutorResponse>() {
            @Override
            public void onResponse(Call<MyProfileTutorResponse> call, Response<MyProfileTutorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyProfileTutorResponse tutor = response.body();

                    textViewName.setText(tutor.getName() + " " + tutor.getSurname());
                    textViewEmail.setText(tutor.getEmail());
                    textViewBio.setText(tutor.getBio());
                    textViewAge.setText(String.valueOf(tutor.getAge()));
                    textViewExperience.setText(String.valueOf(tutor.getYearsOfExperience()));
                    textViewAchievement.setText(tutor.getHighestAchievement());
                    textViewRating.setText(String.format("%.1f / 5.0", tutor.getRating()));
                    textViewAvailability.setText(tutor.getIsAvailable() ? "Yes" : "No");
                    textViewStreet.setText(tutor.getStreet());
                    textViewCity.setText(tutor.getCity());
                    textViewProvince.setText(tutor.getProvince());
                    textViewPostalCode.setText(tutor.getPostalCode());

                    // Load profile photo from Base64 string
                    if (tutor.getProfilePhotoPath() != null && !tutor.getProfilePhotoPath().isEmpty()) {
                        // Decode the Base64 string into a byte array
                        byte[] decodedBytes = Base64.decode(tutor.getProfilePhotoPath(), Base64.DEFAULT);

                        // Convert the byte array into a Bitmap
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                        // Load the Bitmap into the ImageView using Glide
                        Glide.with(TutorProfile.this)
                                .load(decodedBitmap)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(profileImage);
                    }

                } else {
                    Toast.makeText(TutorProfile.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyProfileTutorResponse> call, Throwable t) {
                Toast.makeText(TutorProfile.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
