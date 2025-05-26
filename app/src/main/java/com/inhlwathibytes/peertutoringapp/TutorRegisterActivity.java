package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TutorRegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextSurname, editTextSubjectName, editTextStudentNumber,
            editTextSubjectDescription, editTextEmail, editTextPhone, editTextPassword, editTextConfirmPassword;
    private Spinner spinnerCategory;
    private Button buttonRegisterTutor;
    private TextView textViewLoginFromTutor;
    private FloatingActionButton fabBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_register);

        // Initialize Views
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextSubjectName = findViewById(R.id.editTextSubjectName);
        editTextStudentNumber = findViewById(R.id.editTextStudentNumber);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        editTextSubjectDescription = findViewById(R.id.editTextSubjectDescription);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegisterTutor = findViewById(R.id.buttonRegisterTutor);
        textViewLoginFromTutor = findViewById(R.id.textViewLoginFromTutor);
        fabBack = findViewById(R.id.fabBack);
        dbHelper = new DatabaseHelper(this);

        // Populate Spinner
        String[] categories = {
                "Choose Category",
                "Health Science",
                "Engineering",
                "Mathematics",
                "Business",
                "Arts",
                "Accounting",
                "IT",
                "Taxation",
                "Psychology",
                "Education",
                "Law",
                "Architecture",
                "Environmental Science",
                "Agriculture",
                "Hospitality",
                "Tourism",
                "Media Studies",
                "Communication",
                "Political Science",
                "Economics",
                "Finance",
                "Pharmacy",
                "Nursing",
                "Dentistry",
                "Veterinary Science",
                "Aviation",
                "Marine Biology",
                "Culinary Arts",
                "Fashion Design",
                "Sports Science"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

// Set the hint as the initial selection
        spinnerCategory.setSelection(0, false);

// Add a listener to validate that the hint isn't selected when submitting
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // You can add any logic you want when an item is selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        // Register Button
        buttonRegisterTutor.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String surname = editTextSurname.getText().toString().trim();
            String subject = editTextSubjectName.getText().toString().trim();
            String studentNumber = editTextStudentNumber.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String description = editTextSubjectDescription.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            // Validate all fields are filled
            if (name.isEmpty() || surname.isEmpty() || subject.isEmpty() || studentNumber.isEmpty() ||
                    description.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(TutorRegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate student number (8-10 digits)
            if (!studentNumber.matches("\\d{8,10}")) {
                editTextStudentNumber.setError("Student/Staff number must be 8-10 digits");
                editTextStudentNumber.requestFocus();
                return;
            }

            // Validate email starts with student number
            if (!email.startsWith(studentNumber)) {
                editTextEmail.setError("Email must start with your student/staff number");
                editTextEmail.requestFocus();
                return;
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Enter a valid email address");
                editTextEmail.requestFocus();
                return;
            }

            // Validate phone number (10 digits)
            if (!phone.matches("\\d{10}")) {
                editTextPhone.setError("Phone number must be 10 digits");
                editTextPhone.requestFocus();
                return;
            }

            // Validate password match
            if (!password.equals(confirmPassword)) {
                editTextConfirmPassword.setError("Passwords do not match");
                editTextConfirmPassword.requestFocus();
                return;
            }

            // Validate strong password (at least 8 chars, with uppercase, lowercase, number, and special char)
            if (!isPasswordStrong(password)) {
                editTextPassword.setError("Password must be at least 8 characters and include:\n- Uppercase letter\n- Lowercase letter\n- Number\n- Special character");
                editTextPassword.requestFocus();
                return;
            }
            if (spinnerCategory.getSelectedItemPosition() == 0) { // 0 is the hint position
                Toast.makeText(TutorRegisterActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            // All validations passed - proceed with registration
            boolean inserted = dbHelper.insertTutor(name, surname, subject, studentNumber, category, description, email, phone, password);
            if (inserted) {
                Toast.makeText(TutorRegisterActivity.this, "Tutor registered successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TutorRegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(TutorRegisterActivity.this, "Registration failed! Email or student number may already exist.", Toast.LENGTH_SHORT).show();
            }
        });

        // Login text click
        textViewLoginFromTutor.setOnClickListener(v -> {
            startActivity(new Intent(TutorRegisterActivity.this, LoginActivity.class));
        });

        // Back button click
        fabBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    // Password strength validator
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