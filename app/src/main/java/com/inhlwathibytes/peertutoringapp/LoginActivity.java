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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private RadioGroup radioGroupRole;
    private RadioButton radioStudent, radioTutor;
    private Button buttonLogin;
    private TextView textViewRegisterStudent, textViewRegisterTutor;
    private DatabaseHelper dbHelper;

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
            int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if Student or Tutor is selected
            if (selectedRoleId == -1) {
                Toast.makeText(LoginActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedRoleId == R.id.radioStudent) {
                // Login as Student
                if (validateStudentLogin(email, password)) {
                    Intent intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password for Student", Toast.LENGTH_SHORT).show();
                }
            } else if (selectedRoleId == R.id.radioTutor) {
                // Login as Tutor
                if (validateTutorLogin(email, password)) {
                    Intent intent = new Intent(LoginActivity.this, TutorDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password for Tutor", Toast.LENGTH_SHORT).show();
                }
            }
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
