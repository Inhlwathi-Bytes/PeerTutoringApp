package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppointmentStatusActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerViewAppointments;
    private AppointmentStatusAdapter adapter;
    private List<Appointment> appointmentList;
    private List<Appointment> filteredList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MaterialButton btnFilter, btnSort;

    // Filter and sort variables
    private String currentFilter = "all"; // "all", "approved", "pending", "rejected"
    private boolean sortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_status);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnFilter = findViewById(R.id.btnFilter);
        btnSort = findViewById(R.id.btnSort);

        // Setup toolbar
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
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

        // Setup RecyclerView
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAppointments.setHasFixedSize(true);

        dbHelper = new DatabaseHelper(this);
        appointmentList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new AppointmentStatusAdapter(filteredList);
        recyclerViewAppointments.setAdapter(adapter);

        // Setup back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());

        // Setup filter button
        btnFilter.setOnClickListener(v -> showFilterDialog());

        // Setup sort button
        btnSort.setOnClickListener(v -> {
            sortAscending = !sortAscending;
            sortAppointments();
            Toast.makeText(this, sortAscending ? "Sorted: Oldest First" : "Sorted: Newest First", Toast.LENGTH_SHORT).show();
        });

        loadAppointments();
    }

    private void showFilterDialog() {
        String[] statuses = {"All Appointments", "Approved", "Pending", "Rejected"};
        int checkedItem = 0; // Default to "All"

        switch (currentFilter) {
            case "approved":
                checkedItem = 1;
                break;
            case "pending":
                checkedItem = 2;
                break;
            case "rejected":
                checkedItem = 3;
                break;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Filter by Status")
                .setSingleChoiceItems(statuses, checkedItem, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            currentFilter = "all";
                            break;
                        case 1:
                            currentFilter = "approved";
                            break;
                        case 2:
                            currentFilter = "pending";
                            break;
                        case 3:
                            currentFilter = "rejected";
                            break;
                    }
                    filterAppointments();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void filterAppointments() {
        filteredList.clear();

        if (currentFilter.equals("all")) {
            filteredList.addAll(appointmentList);
        } else {
            for (Appointment appointment : appointmentList) {
                if (appointment.getStatus().equalsIgnoreCase(currentFilter)) {
                    filteredList.add(appointment);
                }
            }
        }

        sortAppointments();
        adapter.notifyDataSetChanged();
    }

    private void sortAppointments() {
        Collections.sort(filteredList, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment a1, Appointment a2) {
                // Compare by date and then by time
                int dateCompare = a1.getDate().compareTo(a2.getDate());
                if (dateCompare == 0) {
                    return sortAscending ? a1.getTime().compareTo(a2.getTime()) :
                            a2.getTime().compareTo(a1.getTime());
                }
                return sortAscending ? dateCompare : -dateCompare;
            }
        });
        adapter.notifyDataSetChanged();
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

    private void loadAppointments() {
        appointmentList.clear();

        Cursor cursor = dbHelper.getAllAppointmentsForStudent(sessionManager.getUserId());

        if (cursor != null && cursor.moveToFirst()) {
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

                Tutor tutor = dbHelper.getTutorById(tutorId);
                if (tutor != null) {
                    Appointment appointment = new Appointment(
                            id, tutorId, studentId,
                            tutor.getName() + " " + tutor.getSurname(),
                            tutor.getEmail(),
                            tutor.getPhone(),
                            date, time, duration, subject, status, createdAt
                    );
                    appointmentList.add(appointment);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "No appointments found", Toast.LENGTH_SHORT).show();
        }

        // Apply default filter and sort
        filterAppointments();
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
            // Already on this activity
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

    private class AppointmentStatusAdapter extends RecyclerView.Adapter<AppointmentStatusAdapter.AppointmentStatusViewHolder> {
        private List<Appointment> appointments;

        public AppointmentStatusAdapter(List<Appointment> appointments) {
            this.appointments = appointments;
        }

        @Override
        public AppointmentStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_appointment_status, parent, false);
            return new AppointmentStatusViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AppointmentStatusViewHolder holder, int position) {
            Appointment appointment = appointments.get(position);

            holder.textTutorName.setText(appointment.getStudentName());
            holder.textDate.setText("Date: " + appointment.getDate());
            holder.textTime.setText("Time: " + appointment.getTime());
            holder.textDuration.setText("Duration: " + appointment.getDuration() + " minutes");
            holder.textSubject.setText("Subject: " + appointment.getSubject());
            holder.textStatus.setText("Status: " + appointment.getStatus());

            // Set status color
            switch (appointment.getStatus().toLowerCase()) {
                case "approved":
                    holder.textStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case "pending":
                    holder.textStatus.setTextColor(getResources().getColor(R.color.orange));
                    break;
                case "rejected":
                    holder.textStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                default:
                    holder.textStatus.setTextColor(getResources().getColor(R.color.black));
            }

            // Calculate cost (R0.5 per minute)
            if ("approved".equalsIgnoreCase(appointment.getStatus())) {
                try {
                    int duration = Integer.parseInt(appointment.getDuration());
                    double cost = duration * 50;
                    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
                    holder.textCost.setText("Cost: " + format.format(cost));
                    holder.textCost.setVisibility(View.VISIBLE);
                } catch (NumberFormatException e) {
                    holder.textCost.setVisibility(View.GONE);
                }
            } else {
                holder.textCost.setVisibility(View.GONE);
            }

            if ("approved".equalsIgnoreCase(appointment.getStatus())) {
                holder.imageTeams.setVisibility(View.VISIBLE);
                holder.imageTeams.setOnClickListener(v -> {
                    String meetingUrl = generateTeamsMeetingLink(
                            appointment.getStudentName(),
                            appointment.getDate(),
                            appointment.getTime()
                    );
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(meetingUrl));
                    startActivity(intent);
                });
            } else {
                holder.imageTeams.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        class AppointmentStatusViewHolder extends RecyclerView.ViewHolder {
            TextView textTutorName, textDate, textTime, textDuration, textSubject, textStatus, textCost;
            ImageView imageTeams;

            public AppointmentStatusViewHolder(View itemView) {
                super(itemView);
                textTutorName = itemView.findViewById(R.id.textTutorName);
                textDate = itemView.findViewById(R.id.textDate);
                textTime = itemView.findViewById(R.id.textTime);
                textDuration = itemView.findViewById(R.id.textDuration);
                textSubject = itemView.findViewById(R.id.textSubject);
                textStatus = itemView.findViewById(R.id.textStatus);
                textCost = itemView.findViewById(R.id.textCost);
                imageTeams = itemView.findViewById(R.id.imageTeams);
            }
        }
    }

    private String generateTeamsMeetingLink(String tutorName, String date, String time) {
        String subject = "Tutoring Session - " + date + " at " + time;
        String body = "Tutoring session with " + tutorName;

        return "https://teams.microsoft.com/l/meeting/new?subject=" +
                Uri.encode(subject) + "&body=" + Uri.encode(body);
    }
}