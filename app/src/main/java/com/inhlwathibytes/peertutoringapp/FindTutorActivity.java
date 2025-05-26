package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindTutorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private TutorAdapter tutorAdapter;
    private List<Tutor> tutorList;
    private DatabaseHelper dbHelper;
    private SearchView searchView;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button btnSort;
    private Map<Integer, String> tutorRequestStatusMap; // To track request status for each tutor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_tutor);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewTutors);
        searchView = findViewById(R.id.searchView);
        toolbar = findViewById(R.id.toolbar);
        btnSort = findViewById(R.id.btnSort);

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

        // Initialize the tutor request status map
        tutorRequestStatusMap = new HashMap<>();

        // Load tutors and their request statuses
        loadTutorsAndRequests();

        // Set up sort button
        btnSort.setOnClickListener(v -> showSortMenu());
    }

    private void loadTutorsAndRequests() {
        // First load all tutors
        tutorList = fetchTutorsFromDB();

        // Then load the student's requests to check status
        loadStudentRequests();

        // Initialize adapter with the data
        tutorAdapter = new TutorAdapter(this, tutorList, tutorRequestStatusMap);
        recyclerView.setAdapter(tutorAdapter);

        // Set click listener for Add button
        tutorAdapter.setOnAddButtonClickListener(tutorId -> {
            String currentStatus = tutorRequestStatusMap.get(tutorId);

            if (currentStatus != null &&
                    (currentStatus.equals("pending") || currentStatus.equals("approved"))) {
                Toast.makeText(this,
                        "You already have a " + currentStatus + " request with this tutor",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("RequestDebug", "Sending request with student: " + sessionManager.getUserName() +
                    ", Email: " + sessionManager.getUserEmail() +
                    ", Phone: " + sessionManager.getUserPhone());

            boolean success = dbHelper.insertRequest(
                    tutorId,
                    sessionManager.getUserId(),
                    sessionManager.getUserName(),
                    sessionManager.getUserEmail(),
                    sessionManager.getUserPhone());

            if (success) {
                // Update the status in our map
                tutorRequestStatusMap.put(tutorId, "pending");
                tutorAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Request sent to tutor", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to send request", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle search filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tutorAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tutorAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void loadStudentRequests() {
        Cursor cursor = dbHelper.getRequestsForStudent(sessionManager.getUserId());
        if (cursor.moveToFirst()) {
            do {
                int tutorId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_TUTOR_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STATUS));
                tutorRequestStatusMap.put(tutorId, status);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void showSortMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnSort);
        popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.sort_by_rating_high) {
                sortTutorsByRating(true);
                return true;
            } else if (id == R.id.sort_by_rating_low) {
                sortTutorsByRating(false);
                return true;
            } else if (id == R.id.sort_by_name) {
                sortTutorsByName();
                return true;
            } else if (id == R.id.sort_default) {
                resetSorting();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void sortTutorsByRating(boolean descending) {
        List<Tutor> sortedList = new ArrayList<>(tutorList);
        Collections.sort(sortedList, (t1, t2) -> {
            if (descending) {
                return Float.compare(t2.getRating(), t1.getRating());
            } else {
                return Float.compare(t1.getRating(), t2.getRating());
            }
        });
        tutorAdapter.updateList(sortedList);
        Toast.makeText(this,
                descending ? "Sorted by rating (high to low)" : "Sorted by rating (low to high)",
                Toast.LENGTH_SHORT).show();
    }

    private void sortTutorsByName() {
        List<Tutor> sortedList = new ArrayList<>(tutorList);
        Collections.sort(sortedList, (t1, t2) ->
                t1.getName().compareToIgnoreCase(t2.getName()));
        tutorAdapter.updateList(sortedList);
        Toast.makeText(this, "Sorted by name", Toast.LENGTH_SHORT).show();
    }

    private void resetSorting() {
        tutorList = fetchTutorsFromDB();
        tutorAdapter.updateList(tutorList);
        Toast.makeText(this, "Default sorting restored", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_tutor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            showSortMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private List<Tutor> fetchTutorsFromDB() {
        List<Tutor> tutors = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_TUTORS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_NAME));
                String surname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_SURNAME));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_SUBJECT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_CATEGORY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_DESCRIPTION));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_EMAIL));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_PHONE));
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_RATING));
                int ratingCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_RATING_COUNT));

                Tutor tutor = new Tutor(id, subject, rating, name, surname, category, description, email, phone);
                tutor.setRatingCount(ratingCount);
                tutors.add(tutor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tutors;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            startActivity(new Intent(this, StudentDashboardActivity.class));
        } else if (id == R.id.nav_find_tutor) {
            // Already on this activity
        } else if (id == R.id.nav_my_tutors) {
            startActivity(new Intent(this, MyTutorsActivity.class));
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
}