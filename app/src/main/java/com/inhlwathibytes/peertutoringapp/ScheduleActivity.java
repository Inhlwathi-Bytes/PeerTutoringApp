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

public class ScheduleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<Appointment> appointmentList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MaterialButton btnSort;
    private boolean sortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewSchedule);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnSort = findViewById(R.id.btnSort);

        // Setup toolbar
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        appointmentList = fetchAppointmentsFromDB();

        // Initialize adapter first
        scheduleAdapter = new ScheduleAdapter(appointmentList);
        recyclerView.setAdapter(scheduleAdapter);

        // Then sort appointments
        sortAppointments();

        // Setup back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());

        // Setup sort button
        btnSort.setOnClickListener(v -> {
            sortAscending = !sortAscending;
            sortAppointments();
            Toast.makeText(this, sortAscending ? "Sorted: Oldest First" : "Sorted: Newest First",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void sortAppointments() {
        if (appointmentList != null) {
            Collections.sort(appointmentList, new Comparator<Appointment>() {
                @Override
                public int compare(Appointment a1, Appointment a2) {
                    // Compare by date and then by time
                    int dateCompare = a1.getDate().compareTo(a2.getDate());
                    if (dateCompare == 0) {
                        return sortAscending ?
                                a1.getTime().compareTo(a2.getTime()) :
                                a2.getTime().compareTo(a1.getTime());
                    }
                    return sortAscending ? dateCompare : -dateCompare;
                }
            });

            if (scheduleAdapter != null) {
                scheduleAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txtName = headerView.findViewById(R.id.txtName);
        TextView txtEmail = headerView.findViewById(R.id.txtEmail);

        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();

        if (userName != null && !userName.isEmpty()) {
            txtName.setText(userName);
        }
        if (userEmail != null && !userEmail.isEmpty()) {
            txtEmail.setText(userEmail);
        }
    }

    private List<Appointment> fetchAppointmentsFromDB() {
        List<Appointment> appointments = new ArrayList<>();
        Cursor cursor;

        if ("tutor".equals(sessionManager.getUserRole())) {
            cursor = dbHelper.getApprovedAppointmentsForTutor(sessionManager.getUserId());
        } else {
            cursor = dbHelper.getApprovedAppointmentsForStudent(sessionManager.getUserId());
        }

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

                String otherUserName = "";
                String otherUserEmail = "";

                if ("tutor".equals(sessionManager.getUserRole())) {
                    Student student = dbHelper.getStudentById(studentId);
                    if (student != null) {
                        otherUserName = student.getName() + " " + student.getSurname();
                        otherUserEmail = student.getEmail();
                    }
                } else {
                    Tutor tutor = dbHelper.getTutorById(tutorId);
                    if (tutor != null) {
                        otherUserName = tutor.getName() + " " + tutor.getSurname();
                        otherUserEmail = tutor.getEmail();
                    }
                }

                Appointment appointment = new Appointment(
                        id, tutorId, studentId,
                        otherUserName,
                        otherUserEmail,
                        "",
                        date, time, duration, subject, status, createdAt
                );
                appointments.add(appointment);
            } while (cursor.moveToNext());
            cursor.close();
        }
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

    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
        private List<Appointment> appointments;

        public ScheduleAdapter(List<Appointment> appointments) {
            this.appointments = appointments;
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule, parent, false);
            return new ScheduleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ScheduleViewHolder holder, int position) {
            Appointment appointment = appointments.get(position);

            holder.textOtherUser.setText(appointment.getStudentName());
            holder.textDate.setText("Date: " + appointment.getDate());
            holder.textTime.setText("Time: " + appointment.getTime());
            holder.textDuration.setText("Duration: " + appointment.getDuration());
            holder.textSubject.setText("Subject: " + appointment.getSubject());

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
        }

        @Override
        public int getItemCount() {
            return appointments != null ? appointments.size() : 0;
        }

        class ScheduleViewHolder extends RecyclerView.ViewHolder {
            TextView textOtherUser, textDate, textTime, textDuration, textSubject;
            ImageView imageTeams;

            public ScheduleViewHolder(View itemView) {
                super(itemView);
                textOtherUser = itemView.findViewById(R.id.textOtherUser);
                textDate = itemView.findViewById(R.id.textDate);
                textTime = itemView.findViewById(R.id.textTime);
                textDuration = itemView.findViewById(R.id.textDuration);
                textSubject = itemView.findViewById(R.id.textSubject);
                imageTeams = itemView.findViewById(R.id.imageTeams);
            }
        }
    }

    private String generateTeamsMeetingLink(String participantName, String date, String time) {
        String subject = "Tutoring Session - " + date + " at " + time;
        String body = "Tutoring session with " + participantName;

        return "https://teams.microsoft.com/l/meeting/new?subject=" +
                Uri.encode(subject) + "&body=" + Uri.encode(body);
    }
}