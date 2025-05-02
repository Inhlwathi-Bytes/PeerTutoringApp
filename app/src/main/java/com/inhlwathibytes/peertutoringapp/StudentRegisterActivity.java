package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class StudentRegisterActivity extends AppCompatActivity {

    private EditText editTextStudentName, editTextStudentSurname, editTextStudentNumber, editTextInstitution,
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
        private String institution;
        private String email;
        private String phoneNumber;
        private String password;

        public RegisterRequest(String firstName, String lastName, String studentNumber, String institution, String email, String phoneNumber, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.studentNumber = studentNumber;
            this.institution = institution;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.password = password;
        }

        // Add getters/setters if needed
    }

    public interface AuthApi {
        @Headers("Content-Type: application/json")
        @POST("/api/auth/register") // Adjust this to match your .NET endpoint
        Call<Void> registerStudent(@Body StudentRegisterActivity.RegisterRequest request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        // Replace with your backend's IP and port
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://199.168.4.240:7147/") // 10.0.2.2 is localhost for Android Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authApi = retrofit.create(AuthApi.class);

        // Now you can use authApi to make the call

        // Initialize Views
        editTextStudentName = findViewById(R.id.editTextStudentName);
        editTextStudentSurname = findViewById(R.id.editTextStudentSurname);
        editTextStudentNumber = findViewById(R.id.editTextStudentNumber);
        editTextInstitution = findViewById(R.id.editTextInstitution);
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
            String institution = editTextInstitution.getText().toString();
            String email = editTextStudentEmail.getText().toString();
            String phone = editTextStudentPhone.getText().toString();
            String password = editTextStudentPassword.getText().toString();
            String confirmPassword = editTextStudentConfirmPassword.getText().toString();

            if (name.isEmpty() || surname.isEmpty() || studentNumber.isEmpty() || institution.isEmpty() ||
                    email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(StudentRegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(StudentRegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                RegisterRequest request = new RegisterRequest(
                        name,
                        surname,
                        studentNumber,
                        institution,
                        email,
                        phone,
                        password
                );

                Call<Void> call = authApi.registerStudent(request);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(StudentRegisterActivity.this, "Registered with API!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StudentRegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(StudentRegisterActivity.this, "Registration failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        System.out.println("API Error: " + t.getMessage());
                        Toast.makeText(StudentRegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
