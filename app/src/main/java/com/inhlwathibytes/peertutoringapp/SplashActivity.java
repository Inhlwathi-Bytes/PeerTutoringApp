package com.inhlwathibytes.peertutoringapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.content.SharedPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.util.Log;import android.util.Base64;
import org.json.JSONObject;


import com.inhlwathibytes.peertutoringapp.models.UserResponseMode;
import com.inhlwathibytes.peertutoringapp.network.RetrofitClient;
import com.inhlwathibytes.peertutoringapp.network.UserModeApi;

public class SplashActivity extends AppCompatActivity {

    UserModeApi modeApi = RetrofitClient.getRetrofitInstance().create(UserModeApi.class);

    private boolean isTokenExpired(String token) {
        try {
            // Split the token into parts
            String[] parts = token.split("\\.");
            if (parts.length != 3) return true;

            // Decode payload (second part)
            String payloadJson = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
            JSONObject payload = new JSONObject(payloadJson);

            if (!payload.has("exp")) return true;

            long exp = payload.getLong("exp");
            long currentTime = System.currentTimeMillis() / 1000;

            return currentTime >= exp;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // treat as expired if any error occurs
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String token = prefs.getString("jwt_token", null);


            if (token != null && !isTokenExpired(token)) {
                Call<UserResponseMode> call = modeApi.getUserMode("Bearer " + token);
                call.enqueue(new Callback<UserResponseMode>() {
                    @Override
                    public void onResponse(Call<UserResponseMode> call, Response<UserResponseMode> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String mode = response.body().getMode();

                            // Save to SharedPreferences
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("user_mode", mode);
                            editor.apply();

                            Log.d("response", mode);
                            Intent intent;
                            if ("tutor".equalsIgnoreCase(mode)) {
                                intent = new Intent(SplashActivity.this, TutorDashboardActivity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, StudentDashboardActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            // handle error, maybe force logout
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponseMode> call, Throwable t) {
                        // handle failure, maybe show retry UI
                    }
                });
            } else {
                if (token != null && !isTokenExpired(token)) {
                    // Delete expired token
                    prefs.edit().remove("jwt_token").apply();
                }
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}