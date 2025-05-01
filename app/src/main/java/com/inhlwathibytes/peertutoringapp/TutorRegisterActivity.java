package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TutorRegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextSurname, editTextSubjectName, editTextStudentNumber,
            editTextSubjectDescription, editTextEmail, editTextPhone, editTextPassword, editTextConfirmPassword;
    private Spinner spinnerCategory;
    private Button buttonRegisterTutor;
    private TextView textViewLoginFromTutor;
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
        dbHelper = new DatabaseHelper(this);

        // Populate Spinner
        String[] categories = {"Science", "Engineering", "Mathematics", "Business", "Arts"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Register Button
        buttonRegisterTutor.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String surname = editTextSurname.getText().toString();
            String subject = editTextSubjectName.getText().toString();
            String studentNumber = editTextStudentNumber.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String description = editTextSubjectDescription.getText().toString();
            String email = editTextEmail.getText().toString();
            String phone = editTextPhone.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            if (name.isEmpty() || surname.isEmpty() || subject.isEmpty() || studentNumber.isEmpty() ||
                    description.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(TutorRegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(TutorRegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = dbHelper.insertTutor(name, surname, subject, studentNumber, category, description, email, phone, password);
                if (inserted) {
                    Toast.makeText(TutorRegisterActivity.this, "Tutor registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TutorRegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(TutorRegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login text click
        textViewLoginFromTutor.setOnClickListener(v -> {
            startActivity(new Intent(TutorRegisterActivity.this, LoginActivity.class));
        });
    }
}
