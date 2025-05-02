package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
    private DatabaseHelper dbHelper;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://199.168.4.240:7147/") // use your local IP and .NET port (e.g., 7147)
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

    public interface AuthApi {
        @Headers("Content-Type: application/json")
        @POST("api/auth/login") // or just "login" depending on your controller route prefix
        Call<Void> loginUser(@Body LoginRequest loginRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        radioStudent = findViewById(R.id.radioStudent);
        radioTutor = findViewById(R.id.radioTutor);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegisterStudent = findViewById(R.id.textViewRegisterStudent);
        textViewRegisterTutor = findViewById(R.id.textViewRegisterTutor);
        dbHelper = new DatabaseHelper(this);

        // Login Button Click Listener
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, password);

            Call<Void> call = authApi.loginUser(request);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // Navigate to home screen if needed
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // "Register As A Student" click
        textViewRegisterStudent.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, StudentRegisterActivity.class);
            startActivity(intent);
        });

        // "Register As A Tutor" click
        textViewRegisterTutor.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, TutorRegisterActivity.class);
            startActivity(intent);
        });
    }

    // Validate Student Login
    private boolean validateStudentLogin(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_STUDENTS +
                        " WHERE " + DatabaseHelper.STUDENT_EMAIL + " = ? AND " + DatabaseHelper.STUDENT_PASSWORD + " = ?",
                new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true; // Valid student login
        }

        return false; // Invalid student login
    }

    // Validate Tutor Login
    private boolean validateTutorLogin(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_TUTORS +
                        " WHERE " + DatabaseHelper.TUTOR_EMAIL + " = ? AND " + DatabaseHelper.TUTOR_PASSWORD + " = ?",
                new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true; // Valid tutor login
        }

        return false; // Invalid tutor login
    }
}
