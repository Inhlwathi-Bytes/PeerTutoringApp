package com.inhlwathibytes.peertutoringapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

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
        ViewGroup root = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_base_drawer, null);
        View content = getLayoutInflater().inflate(layoutResID, null);
        FrameLayout container = root.findViewById(R.id.content_frame);
        container.addView(content);
        setContentView(root);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Class<?> targetActivity = null;

            if (id == R.id.nav_logout) {
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                prefs.edit().remove("jwt_token").apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                //startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_find_tutors) {
                targetActivity = FindTutorActivity.class;
            } else if (id == R.id.nav_bookings) {
                //startActivity(new Intent(this, BookingsActivity.class));
            } else if (id == R.id.nav_my_tutors) {
                targetActivity = MyTutorsActivity.class;
            } else if (id == R.id.nav_offer_tutorship) {
                startActivity(new Intent(this, OfferTutoringActivity.class));
            } else if (id == R.id.nav_student_dashboard) {
                targetActivity = StudentDashboardActivity.class;
            }

            if (targetActivity != null && !this.getClass().equals(targetActivity)) {
                startActivity(new Intent(this, targetActivity));
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }
}
