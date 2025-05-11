package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Headers;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private RadioGroup radioGroupRole;
    private RadioButton radioStudent, radioTutor;
    private Button buttonLogin;
    private TextView textViewRegisterStudent, textViewRegisterTutor;
//199.168.4.240
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.0.173:7147/") // use your local IP and .NET port (e.g., 7147)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    AuthApi authApi = retrofit.create(AuthApi.class);

    public class LoginRequest {
        private String email;
        private String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters and setters if needed (Retrofit uses them internally)
    }

    public class TokenResponse {
        @SerializedName("token")
        private String token;

        public String getToken() {
            return token;
        }
    }

    public interface AuthApi {
        @Headers("Content-Type: application/json")
        @POST("api/auth/login") // or just "login" depending on your controller route prefix
        Call<TokenResponse> loginUser(@Body LoginRequest loginRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegisterStudent = findViewById(R.id.textViewRegisterStudent);


        // Login Button Click Listener
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, password);

            Call<TokenResponse> call = authApi.loginUser(request);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();

                        //Log.d("SplashActivity", "Retrieved token: " + token);

                        // Save token
                        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("jwt_token", token);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Go to dashboard
                        startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // "Register As A Student" click
        textViewRegisterStudent.setOnClickListener(myView -> {
            Intent intent = new Intent(LoginActivity.this, StudentRegisterActivity.class);
            startActivity(intent);
        });
    }
}
