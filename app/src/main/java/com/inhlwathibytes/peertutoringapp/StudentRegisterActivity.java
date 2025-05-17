package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.inhlwathibytes.peertutoringapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Headers;


public class StudentRegisterActivity extends AppCompatActivity {

    private EditText editTextStudentName, editTextStudentSurname, editTextStudentNumber, editTextLocation,
            editTextStudentEmail, editTextStudentPhone, editTextStudentPassword, editTextStudentConfirmPassword;
    private Button buttonRegisterStudent;
    private TextView textViewLoginFromStudent;
    private DatabaseHelper dbHelper;

    private AuthApi authApi;

    //Request class
    public static class RegisterRequest {
        private String firstName;
        private String lastName;
        private String studentNumber;
        private String location;
        private String email;
        private String phoneNumber;
        private String password;

        public RegisterRequest(String firstName, String lastName, String studentNumber, String location, String email, String phoneNumber, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.studentNumber = studentNumber;
            this.location = location;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.password = password;
        }

        // Add getters/setters if needed
    }
    public class RegisterResponse {
        private String token;

        public String getToken() {
            return token;
        }
    }

    public interface AuthApi {
        @Headers("Content-Type: application/json")
        @POST("/api/auth/register") // Adjust this to match your .NET endpoint
        Call<RegisterResponse> registerStudent(@Body StudentRegisterActivity.RegisterRequest request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

//        // Replace with your backend's IP and port
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://199.168.4.240:7147/") // 10.0.2.2 is localhost for Android Emulator
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();

        authApi = RetrofitClient.getRetrofitInstance().create(AuthApi.class);

        // Now you can use authApi to make the call

        // Initialize Views
        editTextStudentName = findViewById(R.id.editTextStudentName);
        editTextStudentSurname = findViewById(R.id.editTextStudentSurname);
        editTextStudentNumber = findViewById(R.id.editTextStudentNumber);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextStudentEmail = findViewById(R.id.editTextStudentEmail);
        editTextStudentPhone = findViewById(R.id.editTextStudentPhone);
        editTextStudentPassword = findViewById(R.id.editTextStudentPassword);
        editTextStudentConfirmPassword = findViewById(R.id.editTextStudentConfirmPassword);
        buttonRegisterStudent = findViewById(R.id.buttonRegisterStudent);
        textViewLoginFromStudent = findViewById(R.id.textViewLoginFromStudent);
        dbHelper = new DatabaseHelper(this);

        // Register Button
        buttonRegisterStudent.setOnClickListener(v -> {
            String name = editTextStudentName.getText().toString();
            String surname = editTextStudentSurname.getText().toString();
            String studentNumber = editTextStudentNumber.getText().toString();
            String location = editTextLocation.getText().toString();
            String email = editTextStudentEmail.getText().toString();
            String phone = editTextStudentPhone.getText().toString();
            String password = editTextStudentPassword.getText().toString();
            String confirmPassword = editTextStudentConfirmPassword.getText().toString();

            if (name.isEmpty() || surname.isEmpty() || studentNumber.isEmpty() || location.isEmpty() ||
                    email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(StudentRegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(StudentRegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                RegisterRequest request = new RegisterRequest(
                        name,
                        surname,
                        studentNumber,
                        location,
                        email,
                        phone,
                        password
                );

                Call<RegisterResponse> call = authApi.registerStudent(request);
                call.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getToken();

                            // Save token to SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            prefs.edit().putString("jwt_token", token).apply();

                            Toast.makeText(StudentRegisterActivity.this, "Registered and logged in!", Toast.LENGTH_SHORT).show();

                            // Navigate directly to student dashboard
                            startActivity(new Intent(StudentRegisterActivity.this, SplashActivity.class));
                            finish();
                        } else {
                            Toast.makeText(StudentRegisterActivity.this, "Registration failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        Toast.makeText(StudentRegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // Login text click
        textViewLoginFromStudent.setOnClickListener(v -> {
            startActivity(new Intent(StudentRegisterActivity.this, LoginActivity.class));
        });
    }
}
