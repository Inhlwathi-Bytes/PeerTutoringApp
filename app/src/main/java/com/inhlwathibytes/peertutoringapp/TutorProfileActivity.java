package com.inhlwathibytes.peertutoringapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class TutorProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextInputEditText editName, editSurname, editStudentNumber, editSubject,
            editCategory, editDescription, editEmail, editPhone;
    private Button btnUpdate;
    private FloatingActionButton fabBack;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        // Initialize views
        toolbar = findViewById(R.id.tutor_toolbar);
        editName = findViewById(R.id.editName);
        editSurname = findViewById(R.id.editSurname);
        editStudentNumber = findViewById(R.id.editStudentNumber);
        editSubject = findViewById(R.id.editSubject);
        editCategory = findViewById(R.id.editCategory);
        editDescription = findViewById(R.id.editDescription);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
        fabBack = findViewById(R.id.fabBack);
        drawerLayout = findViewById(R.id.tutor_drawer_layout);
        navigationView = findViewById(R.id.tutor_nav_view);

        // Setup toolbar
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Check if user is logged in as tutor
        if (!sessionManager.isLoggedIn() || !"tutor".equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Please login as a tutor", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup navigation drawer
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update navigation header with tutor info
        updateNavHeader();

        // Load tutor data
        loadTutorData();

        // Set up button click listeners
        btnUpdate.setOnClickListener(v -> updateProfile());
        fabBack.setOnClickListener(v -> onBackPressed());
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.tutor_txtName);
        TextView navEmail = headerView.findViewById(R.id.tutor_txtEmail);

        navName.setText(sessionManager.getUserName());
        navEmail.setText(sessionManager.getUserEmail());
    }

    private void loadTutorData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TUTORS,
                null,
                DatabaseHelper.TUTOR_ID + " = ?",
                new String[]{String.valueOf(sessionManager.getUserId())},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            editName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_NAME)));
            editSurname.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_SURNAME)));
            editStudentNumber.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_STUDENT_NUMBER)));
            editSubject.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_SUBJECT)));
            editCategory.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_CATEGORY)));
            editDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_DESCRIPTION)));
            editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_EMAIL)));
            editPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_PHONE)));
            cursor.close();
        }
    }

    private void updateProfile() {
        String name = editName.getText().toString().trim();
        String surname = editSurname.getText().toString().trim();
        String studentNumber = editStudentNumber.getText().toString().trim();
        String subject = editSubject.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || surname.isEmpty() || studentNumber.isEmpty() ||
                subject.isEmpty() || category.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TUTOR_NAME, name);
        values.put(DatabaseHelper.TUTOR_SURNAME, surname);
        values.put(DatabaseHelper.TUTOR_STUDENT_NUMBER, studentNumber);
        values.put(DatabaseHelper.TUTOR_SUBJECT, subject);
        values.put(DatabaseHelper.TUTOR_CATEGORY, category);
        values.put(DatabaseHelper.TUTOR_DESCRIPTION, description);
        values.put(DatabaseHelper.TUTOR_EMAIL, email);
        values.put(DatabaseHelper.TUTOR_PHONE, phone);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.update(
                DatabaseHelper.TABLE_TUTORS,
                values,
                DatabaseHelper.TUTOR_ID + " = ?",
                new String[]{String.valueOf(sessionManager.getUserId())});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            sessionManager.updateUserDetails(name, email, phone);
            updateNavHeader();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tutor_nav_dashboard) {
            startActivity(new Intent(this, TutorDashboardActivity.class));
        } else if (id == R.id.tutor_nav_requests) {
            startActivity(new Intent(this, StudentRequestActivity.class));
        } else if (id == R.id.tutor_nav_appointments) {
            startActivity(new Intent(this, AppointmentsActivity.class));
        } else if (id == R.id.tutor_nav_schedule) {
            startActivity(new Intent(this, ScheduleActivity.class));
        } else if (id == R.id.tutor_nav_students) {
            startActivity(new Intent(this, MyStudentsActivity.class));
        } else if (id == R.id.tutor_nav_profile) {
            // Already on profile activity
        } else if (id == R.id.tutor_nav_logout) {
            sessionManager.logoutUser();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}