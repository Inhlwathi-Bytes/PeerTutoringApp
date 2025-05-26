package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class StudentDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CardView findTutorCard, myTutorsCard, appointmentStatusCard, bookAppointment;
    private TextView tutorsCountText, appointmentsCountText;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize database helper and session manager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        findTutorCard = findViewById(R.id.cardFindTutor);
        myTutorsCard = findViewById(R.id.cardMyTutors);
        appointmentStatusCard = findViewById(R.id.cardAppointmentStatus);
        bookAppointment = findViewById(R.id.cardBookAppointment);

        // Find the TextViews
        tutorsCountText = findViewById(R.id.textTutorsCount);
        appointmentsCountText = findViewById(R.id.textAppointmentsCount);

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

        // Set click listeners for cards
        findTutorCard.setOnClickListener(v -> {
            startActivity(new Intent(this, FindTutorActivity.class));
        });

        myTutorsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, MyTutorsActivity.class));
        });

        bookAppointment.setOnClickListener(v -> {
            startActivity(new Intent(this, BookAppointmentActivity.class));
        });

        appointmentStatusCard.setOnClickListener(v -> {
            startActivity(new Intent(this, AppointmentStatusActivity.class));
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        // Update the stats whenever the activity resumes
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        if (sessionManager.isLoggedIn() && "student".equals(sessionManager.getUserRole())) {
            int studentId = sessionManager.getUserId();

            // Update tutors count
            Cursor tutorsCursor = dbHelper.getApprovedRequestsForStudent(studentId);
            int tutorsCount = tutorsCursor != null ? tutorsCursor.getCount() : 0;
            if (tutorsCursor != null) {
                tutorsCursor.close();
            }
            tutorsCountText.setText(String.valueOf(tutorsCount));

            // Update appointments count
            Cursor appointmentsCursor = dbHelper.getAllAppointmentsForStudent(studentId);
            int appointmentsCount = appointmentsCursor != null ? appointmentsCursor.getCount() : 0;
            if (appointmentsCursor != null) {
                appointmentsCursor.close();
            }
            appointmentsCountText.setText(String.valueOf(appointmentsCount));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
        } else if (id == R.id.nav_find_tutor) {
            startActivity(new Intent(this, FindTutorActivity.class));
        } else if (id == R.id.nav_my_tutors) {
            startActivity(new Intent(this, MyTutorsActivity.class));
        } else if (id == R.id.nav_appointments) {
            startActivity(new Intent(this, BookAppointmentActivity.class));
        } else if (id == R.id.nav_status) {
            startActivity(new Intent(this, AppointmentStatusActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));

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