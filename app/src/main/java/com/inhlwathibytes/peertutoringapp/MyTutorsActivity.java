package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import java.util.List;

public class MyTutorsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private MyTutorsAdapter adapter;
    private List<StudentRequestWithTutor> requestList;
    private List<StudentRequestWithTutor> filteredList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MaterialButton btnFilter;
    private String currentFilter = "all"; // "all", "approved", "pending", "rejected"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tutors);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewMyTutors);
        toolbar = findViewById(R.id.toolbar);
        btnFilter = findViewById(R.id.btnFilter);

        // Setup toolbar
        setSupportActionBar(toolbar);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Check if user is logged in as student
        if (!sessionManager.isLoggedIn() || !"student".equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Please login as a student", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        requestList = fetchMyRequests();
        filteredList = new ArrayList<>(requestList);

        adapter = new MyTutorsAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        // Initialize and set up the back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());

        // Setup filter button
        btnFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void showFilterDialog() {
        String[] statuses = {"All Requests", "Approved", "Pending", "Rejected"};
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
                    filterRequests();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void filterRequests() {
        filteredList.clear();

        if (currentFilter.equals("all")) {
            filteredList.addAll(requestList);
        } else {
            for (StudentRequestWithTutor request : requestList) {
                if (request.getRequest().getStatus().equalsIgnoreCase(currentFilter)) {
                    filteredList.add(request);
                }
            }
        }

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

    private List<StudentRequestWithTutor> fetchMyRequests() {
        List<StudentRequestWithTutor> requestsWithTutors = new ArrayList<>();
        Cursor cursor = dbHelper.getRequestsForStudent(sessionManager.getUserId());

        if (cursor.moveToFirst()) {
            do {
                TutorRequest request = new TutorRequest(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_TUTOR_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_DATE))
                );

                Tutor tutor = dbHelper.getTutorById(request.getTutorId());
                if (tutor != null) {
                    requestsWithTutors.add(new StudentRequestWithTutor(request, tutor));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return requestsWithTutors;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            startActivity(new Intent(this, StudentDashboardActivity.class));
        } else if (id == R.id.nav_find_tutor) {
            startActivity(new Intent(this, FindTutorActivity.class));
        } else if (id == R.id.nav_my_tutors) {
            // Already on this activity
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

    private void showRatingDialog(int tutorId, String tutorName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_rate_tutor, null);
        builder.setView(view);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        Button btnSubmit = view.findViewById(R.id.btnSubmitRating);
        TextView txtRatingNotice = view.findViewById(R.id.txtRatingNotice);

        // Check if student has already rated this tutor
        int studentId = sessionManager.getUserId();
        if (dbHelper.hasStudentRatedTutor(tutorId, studentId)) {
            txtRatingNotice.setVisibility(View.VISIBLE);
            txtRatingNotice.setText("You have already rated this tutor");
            btnSubmit.setEnabled(false);
            ratingBar.setIsIndicator(true);
        } else {
            txtRatingNotice.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
            ratingBar.setIsIndicator(false);
        }

        AlertDialog dialog = builder.create();
        dialog.setTitle("Rate " + tutorName);

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            boolean success = dbHelper.updateTutorRating(tutorId, studentId, rating);

            if (success) {
                Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                // Refresh the list to show updated rating
                requestList = fetchMyRequests();
                filterRequests(); // Reapply the current filter
            } else {
                if (dbHelper.hasStudentRatedTutor(tutorId, studentId)) {
                    Toast.makeText(this, "You have already rated this tutor", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                }
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private class MyTutorsAdapter extends RecyclerView.Adapter<MyTutorsAdapter.MyTutorViewHolder> {
        private List<StudentRequestWithTutor> requestsWithTutors;

        public MyTutorsAdapter(List<StudentRequestWithTutor> requestsWithTutors) {
            this.requestsWithTutors = requestsWithTutors;
        }

        @Override
        public MyTutorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_tutor, parent, false);
            return new MyTutorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyTutorViewHolder holder, int position) {
            StudentRequestWithTutor item = requestsWithTutors.get(position);
            Tutor tutor = item.getTutor();
            TutorRequest request = item.getRequest();

            holder.textTutorName.setText(tutor.getName() + " " + tutor.getSurname());
            holder.textTutorSubject.setText(tutor.getSubject());
            holder.textTutorEmail.setText(tutor.getEmail());
            holder.textRequestStatus.setText("Status: " + request.getStatus());
            holder.textRequestDate.setText("Requested on: " + request.getRequestDate());

            // Set status color
            switch (request.getStatus().toLowerCase()) {
                case "approved":
                    holder.textRequestStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case "pending":
                    holder.textRequestStatus.setTextColor(getResources().getColor(R.color.orange));
                    break;
                case "rejected":
                    holder.textRequestStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                default:
                    holder.textRequestStatus.setTextColor(getResources().getColor(R.color.black));
            }

            // Enable rate button only for approved tutors and if not already rated
            int studentId = sessionManager.getUserId();
            boolean isApproved = "approved".equalsIgnoreCase(request.getStatus());
            boolean alreadyRated = dbHelper.hasStudentRatedTutor(tutor.getId(), studentId);

            if (isApproved && !alreadyRated) {
                holder.textRate.setText("Rate");
                holder.textRate.setEnabled(true);
                holder.textRate.setAlpha(1f);
                holder.textRate.setTextColor(getResources().getColor(R.color.primary_color));
            } else if (alreadyRated) {
                holder.textRate.setText("Rated");
                holder.textRate.setEnabled(false);
                holder.textRate.setAlpha(0.5f);
                holder.textRate.setTextColor(getResources().getColor(R.color.gray_dark));
            } else {
                holder.textRate.setText("Rate");
                holder.textRate.setEnabled(false);
                holder.textRate.setAlpha(0.5f);
                holder.textRate.setTextColor(getResources().getColor(R.color.gray_dark));
            }

            // Set click listener for rate button
            holder.textRate.setOnClickListener(v -> {
                if (isApproved && !alreadyRated) {
                    showRatingDialog(tutor.getId(), tutor.getName());
                }
            });
        }

        @Override
        public int getItemCount() {
            return requestsWithTutors.size();
        }

        class MyTutorViewHolder extends RecyclerView.ViewHolder {
            TextView textTutorName, textTutorSubject, textTutorEmail,
                    textRequestStatus, textRequestDate, textRate;

            public MyTutorViewHolder(View itemView) {
                super(itemView);
                textTutorName = itemView.findViewById(R.id.textTutorName);
                textTutorSubject = itemView.findViewById(R.id.textTutorSubject);
                textTutorEmail = itemView.findViewById(R.id.textTutorEmail);
                textRequestStatus = itemView.findViewById(R.id.textRequestStatus);
                textRequestDate = itemView.findViewById(R.id.textRequestDate);
                textRate = itemView.findViewById(R.id.textRate);
            }
        }
    }
}