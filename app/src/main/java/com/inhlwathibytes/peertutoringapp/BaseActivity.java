package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.annotation.LayoutRes;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupDrawer(int layoutResID) {
        // Inflate the root layout and content layout
        ViewGroup root = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_base_drawer, null);
        View content = getLayoutInflater().inflate(layoutResID, null);
        FrameLayout container = root.findViewById(R.id.content_frame);
        container.addView(content);
        setContentView(root);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup ActionBarDrawerToggle for DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Retrieve user mode from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userMode = prefs.getString("user_mode", "student");  // Default to student if not set

        if ("tutor".equals(userMode)) {
            navigationView.getMenu().clear();  // Clear any existing menu items
            navigationView.inflateMenu(R.menu.tutor_nav_menu);  // Tutor menu
        } else {
            navigationView.getMenu().clear();  // Clear any existing menu items
            navigationView.inflateMenu(R.menu.nav_menu);  // Student menu
        }

        // Set item click listener for navigation
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Class<?> targetActivity = null;

            if (id == R.id.nav_logout) {
                prefs.edit().remove("jwt_token").apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                // startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_find_tutors) {
                targetActivity = FindTutorActivity.class;
            } else if (id == R.id.nav_bookings) {
                // startActivity(new Intent(this, BookingsActivity.class));
            } else if (id == R.id.nav_my_tutors) {
                targetActivity = MyTutorsActivity.class;
            } else if (id == R.id.nav_offer_tutorship) {
                startActivity(new Intent(this, RegisterAsTutor.class));
                return true;
            } else if (id == R.id.nav_student_dashboard) {
                targetActivity = StudentDashboardActivity.class;
            } else if (id == R.id.nav_tutor_profile) {
                // targetActivity = TutorProfileActivity.class;
            } else if (id == R.id.nav_subjects) {
                // targetActivity = ManageSubjectsActivity.class;
            } else if (id == R.id.nav_students) {
                // targetActivity = ManageStudentsActivity.class;
            } else if (id == R.id.nav_mode_switch) {
                // startActivity(new Intent(this, ModeChangeActivity.class));
            }

            if (targetActivity != null && !this.getClass().equals(targetActivity)) {
                startActivity(new Intent(this, targetActivity));
            }

            drawerLayout.closeDrawers();
            return true;
        });

    }
}
