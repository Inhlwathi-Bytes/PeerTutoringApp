package com.inhlwathibytes.peertutoringapp;

import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;


public class StudentRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;
    private List<Request> requestList;
    private DatabaseHelper dbHelper;
    private String tutorEmail = "logged_in_tutor_email"; // replace with actual logged-in tutor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_request);

        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        requestList = new ArrayList<>();

        loadRequests();
    }

    private void loadRequests() {
        Cursor cursor = dbHelper.getRequestsForTutor(tutorEmail);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_ID));
                String studentEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STUDENT_EMAIL));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REQUEST_STATUS));

                requestList.add(new Request(id, studentEmail, tutorEmail, status));
            } while (cursor.moveToNext());
        }

        requestAdapter = new RequestAdapter(this, requestList, dbHelper);
        recyclerView.setAdapter(requestAdapter);
    }
}
