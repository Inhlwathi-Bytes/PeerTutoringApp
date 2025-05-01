package com.inhlwathibytes.peertutoringapp;

import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyTutorsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyTutorsAdapter adapter;
    private List<Request> myTutorList;
    private DatabaseHelper dbHelper;
    private String studentEmail = "logged_in_student_email"; // replace with real value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tutors);

        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        myTutorList = new ArrayList<>();

        loadMyTutors();
    }

    private void loadMyTutors() {
        Cursor cursor = dbHelper.getRequestsForStudent(studentEmail);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_ID));
                String tutorEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_TUTOR_EMAIL));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STATUS));

                myTutorList.add(new Request(id, studentEmail, tutorEmail, status));
            } while (cursor.moveToNext());
        }

        adapter = new MyTutorsAdapter(this, myTutorList);
        recyclerView.setAdapter(adapter);
    }
}
