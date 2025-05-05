package com.inhlwathibytes.peertutoringapp;

import android.os.Bundle;


public class StudentDashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_student_dashboard);

        // If there's any dashboard-specific setup, do it here
    }
}
