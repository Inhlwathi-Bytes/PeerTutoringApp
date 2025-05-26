package com.inhlwathibytes.peertutoringapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.widget.TextView;
import android.widget.Toast;

public class BookAppointmentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner spinnerTutors, spinnerDuration;
    private EditText editTextDate, editTextTime, editTextSubject;
    private Button buttonBook;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<Integer> tutorIds = new ArrayList<>();
    private Calendar calendar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        calendar = Calendar.getInstance();

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        spinnerTutors = findViewById(R.id.spinnerTutors);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextSubject = findViewById(R.id.editTextSubject);
        buttonBook = findViewById(R.id.buttonBook);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Setup toolbar
        setSupportActionBar(toolbar);

        // Check if user is logged in as student
        if (!sessionManager.isLoggedIn() || !"student".equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Please login as a student", Toast.LENGTH_SHORT).show();
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

        // Update navigation header with user info
        updateNavHeader();

        // Setup duration spinner
        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,
                R.array.duration_options, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);

        // Set up date picker
        editTextDate.setOnClickListener(v -> showDatePicker());

        // Set up time picker
        editTextTime.setOnClickListener(v -> showTimePicker());

        // Load approved tutors
        loadApprovedTutors();

        // Book appointment button click
        buttonBook.setOnClickListener(v -> bookAppointment());

        // Setup back button
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

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    updateTimeLabel();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // 24-hour format
        );
        timePickerDialog.show();
    }

    private void updateDateLabel() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        editTextTime.setText(sdf.format(calendar.getTime()));
    }

    private void loadApprovedTutors() {
        List<String> tutorNames = new ArrayList<>();
        tutorIds.clear();

        Cursor requestsCursor = dbHelper.getApprovedRequestsForStudent(sessionManager.getUserId());

        if (requestsCursor != null && requestsCursor.moveToFirst()) {
            do {
                int tutorId = requestsCursor.getInt(requestsCursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_TUTOR_ID));
                Tutor tutor = dbHelper.getTutorById(tutorId);
                if (tutor != null) {
                    tutorNames.add(tutor.getName() + " " + tutor.getSurname());
                    tutorIds.add(tutorId);
                }
            } while (requestsCursor.moveToNext());
        }

        if (requestsCursor != null) {
            requestsCursor.close();
        }

        if (tutorNames.isEmpty()) {
            Toast.makeText(this, "No approved tutors available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ArrayAdapter<String> tutorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tutorNames);
        tutorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTutors.setAdapter(tutorAdapter);
    }

    private void bookAppointment() {
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String duration = spinnerDuration.getSelectedItem().toString();
        String subject = editTextSubject.getText().toString().trim();
        int selectedPosition = spinnerTutors.getSelectedItemPosition();

        if (date.isEmpty() || time.isEmpty() || subject.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPosition >= 0 && selectedPosition < tutorIds.size()) {
            int tutorId = tutorIds.get(selectedPosition);
            boolean success = dbHelper.insertAppointment(
                    tutorId,
                    sessionManager.getUserId(),
                    date,
                    time,
                    duration,
                    subject);

            if (success) {
                Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
            }
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
            // Already on this activity
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