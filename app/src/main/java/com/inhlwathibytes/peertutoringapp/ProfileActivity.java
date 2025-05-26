package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        // Check if user is logged in as student
        if (!sessionManager.isLoggedIn() || !"student".equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Please login as a student", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update navigation header with user info
        updateNavHeader();

        // Display student profile information
        displayStudentProfile();

        // Initialize and set up the back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txtName = headerView.findViewById(R.id.txtName);
        TextView txtEmail = headerView.findViewById(R.id.txtEmail);

        String studentName = sessionManager.getUserName();
        String studentEmail = sessionManager.getUserEmail();

        if (studentName != null && !studentName.isEmpty()) {
            txtName.setText(studentName);
        }
        if (studentEmail != null && !studentEmail.isEmpty()) {
            txtEmail.setText(studentEmail);
        }
    }

    private void displayStudentProfile() {
        // Get student ID from session
        int studentId = sessionManager.getUserId();

        // Get student details from database
        Student student = dbHelper.getStudentById(studentId);

        if (student != null) {
            TextView textName = findViewById(R.id.textStudentName);
            TextView textSurname = findViewById(R.id.textStudentSurname);
            TextView textStudentNumber = findViewById(R.id.textStudentNumber);
            TextView textInstitution = findViewById(R.id.textInstitution);
            TextView textEmail = findViewById(R.id.textStudentEmail);
            TextView textPhone = findViewById(R.id.textStudentPhone);

            textName.setText(student.getName());
            textSurname.setText(student.getSurname());
            textStudentNumber.setText(student.getStudentNumber());
            textInstitution.setText(student.getInstitution());
            textEmail.setText(student.getEmail());
            textPhone.setText(student.getPhone());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            startActivity(new Intent(this, StudentDashboardActivity.class));
        } else if (id == R.id.nav_find_tutor) {
            startActivity(new Intent(this, FindTutorActivity.class));
        } else if (id == R.id.nav_my_tutors) {
            startActivity(new Intent(this, MyTutorsActivity.class));
        } else if (id == R.id.nav_appointments) {
            startActivity(new Intent(this, BookAppointmentActivity.class));
        } else if (id == R.id.nav_status) {
            startActivity(new Intent(this, AppointmentStatusActivity.class));
        } else if (id == R.id.nav_profile) {
            // Already on profile activity
        } else if (id == R.id.nav_logout) {
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