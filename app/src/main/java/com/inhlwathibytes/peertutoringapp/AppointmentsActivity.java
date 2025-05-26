package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private List<Appointment> filteredAppointmentList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MaterialButton btnFilter, btnSort;

    private String currentFilter = "All"; // Default filter
    private boolean sortAscending = true; // Default sort order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Initialize views
        toolbar = findViewById(R.id.tutor_toolbar);
        recyclerView = findViewById(R.id.recyclerViewAppointments);
        drawerLayout = findViewById(R.id.tutor_drawer_layout);
        navigationView = findViewById(R.id.tutor_nav_view);
        btnFilter = findViewById(R.id.btnFilter);
        btnSort = findViewById(R.id.btnSort);

        // Setup toolbar
        setSupportActionBar(toolbar);

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

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        appointmentList = fetchAppointmentsFromDB();
        filteredAppointmentList = new ArrayList<>(appointmentList);

        appointmentAdapter = new AppointmentAdapter(filteredAppointmentList);
        recyclerView.setAdapter(appointmentAdapter);

        // Setup back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());

        // Setup filter button
        btnFilter.setOnClickListener(v -> showFilterDialog());

        // Setup sort button
        btnSort.setOnClickListener(v -> {
            sortAppointments();
            appointmentAdapter.notifyDataSetChanged();
        });
    }

    private void showFilterDialog() {
        String[] filterOptions = {"All", "Pending", "Approved", "Rejected"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Filter Appointments");
        builder.setSingleChoiceItems(filterOptions, getFilterIndex(currentFilter), (dialog, which) -> {
            currentFilter = filterOptions[which];
            filterAppointments();
            appointmentAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private int getFilterIndex(String filter) {
        switch (filter) {
            case "All": return 0;
            case "Pending": return 1;
            case "Approved": return 2;
            case "Rejected": return 3;
            default: return 0;
        }
    }

    private void filterAppointments() {
        filteredAppointmentList.clear();

        if (currentFilter.equals("All")) {
            filteredAppointmentList.addAll(appointmentList);
        } else {
            for (Appointment appointment : appointmentList) {
                if (appointment.getStatus().equalsIgnoreCase(currentFilter)) {
                    filteredAppointmentList.add(appointment);
                }
            }
        }

        // Reapply sort after filtering
        sortAppointments();
    }

    private void sortAppointments() {
        Collections.sort(filteredAppointmentList, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment a1, Appointment a2) {
                // Compare dates first
                int dateCompare = a1.getDate().compareTo(a2.getDate());
                if (dateCompare != 0) {
                    return sortAscending ? dateCompare : -dateCompare;
                }

                // If dates are equal, compare times
                int timeCompare = a1.getTime().compareTo(a2.getTime());
                return sortAscending ? timeCompare : -timeCompare;
            }
        });

        // Toggle sort order for next time
        sortAscending = !sortAscending;

        // Update button icon to reflect sort direction
        btnSort.setIconResource(sortAscending ?
                R.drawable.ic_sort : R.drawable.ic_sort);
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.tutor_nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.tutor_txtName);
        TextView navEmail = headerView.findViewById(R.id.tutor_txtEmail);

        navName.setText(sessionManager.getUserName());
        navEmail.setText(sessionManager.getUserEmail());
    }

    private List<Appointment> fetchAppointmentsFromDB() {
        List<Appointment> appointments = new ArrayList<>();
        Cursor cursor = dbHelper.getAppointmentsForTutor(sessionManager.getUserId());

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_ID));
                int tutorId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_TUTOR_ID));
                int studentId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_STUDENT_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_TIME));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_DURATION));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_SUBJECT));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_STATUS));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.APPOINTMENT_CREATED_AT));

                // Get student details
                Student student = dbHelper.getStudentById(studentId);
                if (student != null) {
                    Appointment appointment = new Appointment(
                            id, tutorId, studentId,
                            student.getName() + " " + student.getSurname(),
                            student.getEmail(),
                            student.getPhone(),
                            date, time, duration, subject, status, createdAt
                    );
                    appointments.add(appointment);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return appointments;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tutor_nav_dashboard) {
            startActivity(new Intent(this, TutorDashboardActivity.class));
        } else if (id == R.id.tutor_nav_requests) {
            startActivity(new Intent(this, StudentRequestActivity.class));
        } else if (id == R.id.tutor_nav_appointments) {
            // Already on this activity
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

    private class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
        private List<Appointment> appointments;

        public AppointmentAdapter(List<Appointment> appointments) {
            this.appointments = appointments;
        }

        @Override
        public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_appointment, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AppointmentViewHolder holder, int position) {
            Appointment appointment = appointments.get(position);

            holder.textStudentName.setText(appointment.getStudentName());
            holder.textDate.setText("Date: " + appointment.getDate());
            holder.textTime.setText("Time: " + appointment.getTime());
            holder.textDuration.setText("Duration: " + appointment.getDuration());
            holder.textSubject.setText("Subject: " + appointment.getSubject());
            holder.textStatus.setText("Status: " + appointment.getStatus());

            // Show/hide buttons based on status
            if (appointment.getStatus().equalsIgnoreCase("pending")) {
                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
            } else {
                holder.btnApprove.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            }

            holder.btnApprove.setOnClickListener(v -> {
                boolean success = dbHelper.updateAppointmentStatus(appointment.getId(), "approved");
                if (success) {
                    appointment.setStatus("approved");
                    // Refresh the list
                    refreshAppointments();
                    Toast.makeText(AppointmentsActivity.this, "Appointment approved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppointmentsActivity.this, "Failed to approve appointment", Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnReject.setOnClickListener(v -> {
                boolean success = dbHelper.updateAppointmentStatus(appointment.getId(), "rejected");
                if (success) {
                    appointment.setStatus("rejected");
                    // Refresh the list
                    refreshAppointments();
                    Toast.makeText(AppointmentsActivity.this, "Appointment rejected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppointmentsActivity.this, "Failed to reject appointment", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        class AppointmentViewHolder extends RecyclerView.ViewHolder {
            TextView textStudentName, textDate, textTime, textDuration, textSubject, textStatus;
            Button btnApprove, btnReject;

            public AppointmentViewHolder(View itemView) {
                super(itemView);
                textStudentName = itemView.findViewById(R.id.textStudentName);
                textDate = itemView.findViewById(R.id.textDate);
                textTime = itemView.findViewById(R.id.textTime);
                textDuration = itemView.findViewById(R.id.textDuration);
                textSubject = itemView.findViewById(R.id.textSubject);
                textStatus = itemView.findViewById(R.id.textStatus);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnReject = itemView.findViewById(R.id.btnReject);
            }
        }
    }

    private void refreshAppointments() {
        appointmentList = fetchAppointmentsFromDB();
        filterAppointments();
        appointmentAdapter.notifyDataSetChanged();
    }
}