package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class TutorDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // UI Components
    private CardView studentRequestCard, appointmentsCard, scheduleCard, myStudentsCard;
    private TextView studentsCountText, schedulesCountText, tutorNameText;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_dashboard);

        // Initialize database and session
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Initialize views
        initializeViews();

        // Setup toolbar and navigation
        setupNavigation();

        // Load data
        loadCounts();
        setupCardClickListeners();

        // Set tutor name
        String tutorName = sessionManager.getUserName();
        if (tutorName != null && !tutorName.isEmpty()) {
            tutorNameText.setText(tutorName);
        }
    }

    private void initializeViews() {
        // Toolbar and navigation
        toolbar = findViewById(R.id.tutor_toolbar);
        drawerLayout = findViewById(R.id.tutor_drawer_layout);
        navigationView = findViewById(R.id.tutor_nav_view);

        // Dashboard cards
        studentRequestCard = findViewById(R.id.cardStudentRequest);
        appointmentsCard = findViewById(R.id.cardAppointments);
        scheduleCard = findViewById(R.id.cardSchedule);
        myStudentsCard = findViewById(R.id.cardMyStudents);

        // Stats
        studentsCountText = findViewById(R.id.studentsCount);
        schedulesCountText = findViewById(R.id.schedulesCount);
        tutorNameText = findViewById(R.id.textTutorName);

        // Back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupNavigation() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Update header with tutor info
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.tutor_txtName);
        TextView navEmail = headerView.findViewById(R.id.tutor_txtEmail);

        navName.setText(sessionManager.getUserName());
        navEmail.setText(sessionManager.getUserEmail());
    }

    private void loadCounts() {
        if (sessionManager.isLoggedIn() && "tutor".equals(sessionManager.getUserRole())) {
            int tutorId = sessionManager.getUserId();
            loadStudentCount(tutorId);
            loadScheduleCount(tutorId);
        }
    }

    private void loadStudentCount(int tutorId) {
        Cursor cursor = dbHelper.getApprovedStudentsForTutor(tutorId);
        int count = cursor != null ? cursor.getCount() : 0;
        studentsCountText.setText(String.valueOf(count));
        if (cursor != null) cursor.close();
    }

    private void loadScheduleCount(int tutorId) {
        Cursor cursor = dbHelper.getApprovedAppointmentsForTutor(tutorId);
        int count = cursor != null ? cursor.getCount() : 0;
        schedulesCountText.setText(String.valueOf(count));
        if (cursor != null) cursor.close();
    }

    private void setupCardClickListeners() {
        studentRequestCard.setOnClickListener(v ->
                startActivity(new Intent(this, StudentRequestActivity.class)));

        appointmentsCard.setOnClickListener(v ->
                startActivity(new Intent(this, AppointmentsActivity.class)));

        scheduleCard.setOnClickListener(v ->
                startActivity(new Intent(this, ScheduleActivity.class)));

        myStudentsCard.setOnClickListener(v ->
                startActivity(new Intent(this, MyStudentsActivity.class)));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tutor_nav_dashboard) {
            // Already here
        } else if (id == R.id.tutor_nav_requests) {
            startActivity(new Intent(this, StudentRequestActivity.class));
        } else if (id == R.id.tutor_nav_appointments) {
            startActivity(new Intent(this, AppointmentsActivity.class));
        } else if (id == R.id.tutor_nav_schedule) {
            startActivity(new Intent(this, ScheduleActivity.class));
        } else if (id == R.id.tutor_nav_students) {
            startActivity(new Intent(this, MyStudentsActivity.class));
        } else if (id == R.id.tutor_nav_profile) {
            startActivity(new Intent(this, TutorProfileActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        loadCounts();
    }
}