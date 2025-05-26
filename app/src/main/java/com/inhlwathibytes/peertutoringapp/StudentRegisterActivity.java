package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StudentRegisterActivity extends AppCompatActivity {

    private EditText editTextStudentName, editTextStudentSurname, editTextStudentNumber, editTextInstitution,
            editTextStudentEmail, editTextStudentPhone, editTextStudentPassword, editTextStudentConfirmPassword;
    private Button buttonRegisterStudent;
    private TextView textViewLoginFromStudent;
    private FloatingActionButton fabBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

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
        fabBack = findViewById(R.id.fabBack);
        dbHelper = new DatabaseHelper(this);

        // Back button click listener
        fabBack.setOnClickListener(v -> {
            finish(); // Close current activity and go back
        });

        // Register Button
        buttonRegisterStudent.setOnClickListener(v -> {
            String name = editTextStudentName.getText().toString().trim();
            String surname = editTextStudentSurname.getText().toString().trim();
            String studentNumber = editTextStudentNumber.getText().toString().trim();
            String institution = editTextInstitution.getText().toString().trim();
            String email = editTextStudentEmail.getText().toString().trim();
            String phone = editTextStudentPhone.getText().toString().trim();
            String password = editTextStudentPassword.getText().toString().trim();
            String confirmPassword = editTextStudentConfirmPassword.getText().toString().trim();

            // Validate all fields are filled
            if (name.isEmpty() || surname.isEmpty() || studentNumber.isEmpty() || institution.isEmpty() ||
                    email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(StudentRegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate student number (8-10 digits)
            if (!studentNumber.matches("\\d{8,10}")) {
                editTextStudentNumber.setError("Student number must be 8-10 digits");
                editTextStudentNumber.requestFocus();
                return;
            }

            // Validate email starts with student number
            if (!email.startsWith(studentNumber)) {
                editTextStudentEmail.setError("Email must start with your student number");
                editTextStudentEmail.requestFocus();
                return;
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextStudentEmail.setError("Enter a valid email address");
                editTextStudentEmail.requestFocus();
                return;
            }

            // Validate phone number (10 digits)
            if (!phone.matches("\\d{10}")) {
                editTextStudentPhone.setError("Phone number must be 10 digits");
                editTextStudentPhone.requestFocus();
                return;
            }

            // Validate password match
            if (!password.equals(confirmPassword)) {
                editTextStudentConfirmPassword.setError("Passwords do not match");
                editTextStudentConfirmPassword.requestFocus();
                return;
            }

            // Validate strong password
            if (!isPasswordStrong(password)) {
                editTextStudentPassword.setError("Password must contain:\n- At least 8 characters\n- 1 uppercase letter\n- 1 lowercase letter\n- 1 number\n- 1 special character");
                editTextStudentPassword.requestFocus();
                return;
            }

            // All validations passed - proceed with registration
            boolean inserted = dbHelper.insertStudent(name, surname, studentNumber, institution, email, phone, password);
            if (inserted) {
                Toast.makeText(StudentRegisterActivity.this, "Student registered successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentRegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(StudentRegisterActivity.this, "Registration failed! Email or student number may already exist.", Toast.LENGTH_SHORT).show();
            }
        });

        // Auto-fill email when student number changes
        editTextStudentNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String studentNumber = editTextStudentNumber.getText().toString().trim();
                if (studentNumber.matches("\\d{8,10}")) {
                    editTextStudentEmail.setText(studentNumber + "@gmail.com");
                }
            }
        });

        // Login text click
        textViewLoginFromStudent.setOnClickListener(v -> {
            startActivity(new Intent(StudentRegisterActivity.this, LoginActivity.class));
        });
    }

    // Password strength validation
    private boolean isPasswordStrong(String password) {
        // At least 8 characters
        if (password.length() < 8) return false;

        // Contains at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) return false;

        // Contains at least one lowercase letter
        if (!password.matches(".*[a-z].*")) return false;


        // Contains at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) return false;

        return true;
    }
}