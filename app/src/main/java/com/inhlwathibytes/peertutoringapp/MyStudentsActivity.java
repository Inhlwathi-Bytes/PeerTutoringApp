package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MyStudentsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private MyStudentsAdapter adapter;
    private List<Student> studentList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_students);

        // Initialize views
        toolbar = findViewById(R.id.tutor_toolbar);
        recyclerView = findViewById(R.id.recyclerViewMyStudents);
        drawerLayout = findViewById(R.id.tutor_drawer_layout);
        navigationView = findViewById(R.id.tutor_nav_view);

        // Setup toolbar
        setSupportActionBar(toolbar);

        // Initialize session manager
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

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Load students
        studentList = fetchMyStudents();
        adapter = new MyStudentsAdapter(studentList);
        recyclerView.setAdapter(adapter);

        // Setup back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.tutor_nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txtName = headerView.findViewById(R.id.tutor_txtName);
        TextView txtEmail = headerView.findViewById(R.id.tutor_txtEmail);

        String tutorName = sessionManager.getUserName();
        String tutorEmail = sessionManager.getUserEmail();

        if (tutorName != null && !tutorName.isEmpty()) {
            txtName.setText(tutorName);
        }
        if (tutorEmail != null && !tutorEmail.isEmpty()) {
            txtEmail.setText(tutorEmail);
        }
    }

    private List<Student> fetchMyStudents() {
        List<Student> students = new ArrayList<>();
        Cursor cursor = dbHelper.getApprovedStudentsForTutor(sessionManager.getUserId());

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int studentId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_ID));
                Student student = dbHelper.getStudentById(studentId);
                if (student != null) {
                    students.add(student);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return students;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tutor_nav_dashboard) {
            startActivity(new Intent(this, TutorDashboardActivity.class));
        } else if (id == R.id.tutor_nav_requests) {
            startActivity(new Intent(this, StudentRequestActivity.class));
        } else if (id == R.id.tutor_nav_appointments) {
            startActivity(new Intent(this, AppointmentsActivity.class));
        } else if (id == R.id.tutor_nav_schedule) {
            startActivity(new Intent(this, ScheduleActivity.class));
        } else if (id == R.id.tutor_nav_students) {
            // Already on this activity
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

    private class MyStudentsAdapter extends RecyclerView.Adapter<MyStudentsAdapter.MyStudentViewHolder> {
        private List<Student> students;

        public MyStudentsAdapter(List<Student> students) {
            this.students = students;
        }

        @Override
        public MyStudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_student, parent, false);
            return new MyStudentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyStudentViewHolder holder, int position) {
            Student student = students.get(position);
            holder.textStudentName.setText(student.getFullName());
            holder.textStudentNumber.setText(student.getStudentNumber());
            holder.textStudentInstitution.setText(student.getInstitution());
            holder.textStudentEmail.setText(student.getEmail());
            holder.textStudentPhone.setText(student.getPhone());
        }

        @Override
        public int getItemCount() {
            return students.size();
        }

        class MyStudentViewHolder extends RecyclerView.ViewHolder {
            TextView textStudentName, textStudentNumber, textStudentInstitution,
                    textStudentEmail, textStudentPhone;

            public MyStudentViewHolder(View itemView) {
                super(itemView);
                textStudentName = itemView.findViewById(R.id.textStudentName);
                textStudentNumber = itemView.findViewById(R.id.textStudentNumber);
                textStudentInstitution = itemView.findViewById(R.id.textStudentInstitution);
                textStudentEmail = itemView.findViewById(R.id.textStudentEmail);
                textStudentPhone = itemView.findViewById(R.id.textStudentPhone);
            }
        }
    }
}