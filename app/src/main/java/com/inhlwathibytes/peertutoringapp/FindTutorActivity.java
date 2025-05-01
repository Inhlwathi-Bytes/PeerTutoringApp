package com.inhlwathibytes.peertutoringapp;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FindTutorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TutorAdapter tutorAdapter;
    private List<Tutor> tutorList;
    private DatabaseHelper dbHelper;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_tutor);

        recyclerView = findViewById(R.id.recyclerViewTutors);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Switch to list view

        dbHelper = new DatabaseHelper(this);
        tutorList = fetchTutorsFromDB();

        tutorAdapter = new TutorAdapter(this, tutorList);
        recyclerView.setAdapter(tutorAdapter);

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

    private List<Tutor> fetchTutorsFromDB() {
        List<Tutor> tutors = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_TUTORS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_NAME));
                String surname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_SURNAME));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_SUBJECT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_CATEGORY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_DESCRIPTION));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TUTOR_EMAIL));

                // Example rating, update later if needed
                float rating = 4.0f;

                tutors.add(new Tutor(id, subject, rating, name, surname, category, description, email));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tutors;
    }
}
