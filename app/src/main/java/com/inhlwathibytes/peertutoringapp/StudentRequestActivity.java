package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudentRequestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;
    private List<TutorRequest> requestList;
    private List<TutorRequest> filteredList;
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
        setContentView(R.layout.activity_student_requests);

        // Initialize views
        toolbar = findViewById(R.id.tutor_toolbar);
        recyclerView = findViewById(R.id.recyclerViewRequests);
        drawerLayout = findViewById(R.id.tutor_drawer_layout);
        navigationView = findViewById(R.id.tutor_nav_view);
        btnFilter = findViewById(R.id.btnFilter);
        btnSort = findViewById(R.id.btnSort);

        // Setup toolbar
        setSupportActionBar(toolbar);

        // Initialize SessionManager
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

        // Update navigation header with user info
        updateNavHeader();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database and load requests
        dbHelper = new DatabaseHelper(this);
        requestList = fetchRequestsFromDB();
        filteredList = new ArrayList<>(requestList);

        requestAdapter = new RequestAdapter(filteredList);
        recyclerView.setAdapter(requestAdapter);

        // Setup back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());

        // Setup filter button
        btnFilter.setOnClickListener(v -> showFilterDialog());

        // Setup sort button
        btnSort.setOnClickListener(v -> {
            sortAscending = !sortAscending;
            sortRequests();
            Toast.makeText(this, sortAscending ? "Sorted: Oldest First" : "Sorted: Newest First", Toast.LENGTH_SHORT).show();
        });
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
            for (TutorRequest request : requestList) {
                if (request.getStatus().equalsIgnoreCase(currentFilter)) {
                    filteredList.add(request);
                }
            }
        }

        sortRequests();
        requestAdapter.notifyDataSetChanged();
    }

    private void sortRequests() {
        Collections.sort(filteredList, new Comparator<TutorRequest>() {
            @Override
            public int compare(TutorRequest r1, TutorRequest r2) {
                return sortAscending ?
                        r1.getRequestDate().compareTo(r2.getRequestDate()) :
                        r2.getRequestDate().compareTo(r1.getRequestDate());
            }
        });
        requestAdapter.notifyDataSetChanged();
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

    private List<TutorRequest> fetchRequestsFromDB() {
        List<TutorRequest> requests = new ArrayList<>();
        Cursor cursor = dbHelper.getRequestsForTutor(sessionManager.getUserId());

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_ID));
                int tutorId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_TUTOR_ID));
                int studentId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_ID));
                String studentName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_NAME));
                String studentEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_EMAIL));
                String studentPhone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_PHONE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STATUS));
                String requestDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_DATE));

                TutorRequest request = new TutorRequest(id, tutorId, studentId, studentName,
                        studentEmail, studentPhone, status, requestDate);
                requests.add(request);

                Log.d("RequestDebug", "Loaded request: " + request.getStudentName() +
                        ", Email: " + request.getStudentEmail() +
                        ", Phone: " + request.getStudentPhone());
            } while (cursor.moveToNext());
        }
        cursor.close();
        return requests;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tutor_nav_dashboard) {
            startActivity(new Intent(this, TutorDashboardActivity.class));
        } else if (id == R.id.tutor_nav_requests) {
            // Already on this activity
        } else if (id == R.id.tutor_nav_appointments) {
            startActivity(new Intent(this, AppointmentsActivity.class));
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

    private class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
        private List<TutorRequest> requests;

        public RequestAdapter(List<TutorRequest> requests) {
            this.requests = requests;
        }

        @Override
        public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_request, parent, false);
            return new RequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RequestViewHolder holder, int position) {
            TutorRequest request = requests.get(position);

            holder.textStudentName.setText(request.getStudentName());
            holder.textStudentEmail.setText(request.getStudentEmail());
            holder.textStudentPhone.setText(request.getStudentPhone());
            holder.textStatus.setText("Status: " + request.getStatus());
            holder.textRequestDate.setText("Requested on: " + request.getRequestDate());

            // Set status color
            switch (request.getStatus().toLowerCase()) {
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

            if (!"pending".equals(request.getStatus())) {
                holder.btnApprove.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            } else {
                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
            }

            holder.btnApprove.setOnClickListener(v -> {
                boolean success = dbHelper.updateRequestStatus(request.getId(), "approved");
                if (success) {
                    request.setStatus("approved");
                    filterRequests(); // Refresh the filtered list
                    Toast.makeText(StudentRequestActivity.this, "Request approved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StudentRequestActivity.this, "Failed to approve request", Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnReject.setOnClickListener(v -> {
                boolean success = dbHelper.updateRequestStatus(request.getId(), "rejected");
                if (success) {
                    request.setStatus("rejected");
                    filterRequests(); // Refresh the filtered list
                    Toast.makeText(StudentRequestActivity.this, "Request rejected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StudentRequestActivity.this, "Failed to reject request", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        class RequestViewHolder extends RecyclerView.ViewHolder {
            TextView textStudentName, textStudentEmail, textStudentPhone, textStatus, textRequestDate;
            Button btnApprove, btnReject;

            public RequestViewHolder(View itemView) {
                super(itemView);
                textStudentName = itemView.findViewById(R.id.textStudentName);
                textStudentEmail = itemView.findViewById(R.id.textStudentEmail);
                textStudentPhone = itemView.findViewById(R.id.textStudentPhone);
                textStatus = itemView.findViewById(R.id.textStatus);
                textRequestDate = itemView.findViewById(R.id.textRequestDate);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnReject = itemView.findViewById(R.id.btnReject);
            }
        }
    }
}