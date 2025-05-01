package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentRegisterActivity extends AppCompatActivity {

    private EditText editTextStudentName, editTextStudentSurname, editTextStudentNumber, editTextInstitution,
            editTextStudentEmail, editTextStudentPhone, editTextStudentPassword, editTextStudentConfirmPassword;
    private Button buttonRegisterStudent;
    private TextView textViewLoginFromStudent;
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
                boolean inserted = dbHelper.insertStudent(name, surname, studentNumber, institution, email, phone, password);
                if (inserted) {
                    Toast.makeText(StudentRegisterActivity.this, "Student registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StudentRegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(StudentRegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login text click
        textViewLoginFromStudent.setOnClickListener(v -> {
            startActivity(new Intent(StudentRegisterActivity.this, LoginActivity.class));
        });
    }
}
