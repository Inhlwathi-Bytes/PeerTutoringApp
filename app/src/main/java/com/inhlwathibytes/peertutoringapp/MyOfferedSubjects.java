package com.inhlwathibytes.peertutoringapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.inhlwathibytes.peertutoringapp.adapters.SubjectsAdapter;
import com.inhlwathibytes.peertutoringapp.network.MyOfferedSubjectsApi;
import com.inhlwathibytes.peertutoringapp.models.MyOfferedSubjectsResponse;
import com.inhlwathibytes.peertutoringapp.models.TutorshipSubject;
import com.inhlwathibytes.peertutoringapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOfferedSubjects extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView noSubjectsText;
    private Button addSubjectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_my_offered_subjects);

        recyclerView = findViewById(R.id.subjectsRecyclerView);
        noSubjectsText = findViewById(R.id.noSubjectsText);
        addSubjectButton = findViewById(R.id.addSubjectButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addSubjectButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyOfferedSubjects.this, CreateNewSubject.class);
            startActivity(intent);
        });

        loadSubjects();
    }

    private void loadSubjects() {
        // Get JWT token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        MyOfferedSubjectsApi api = RetrofitClient.getRetrofitInstance().create(MyOfferedSubjectsApi.class);
        Call<MyOfferedSubjectsResponse> call = api.getMySubjects("Bearer " + token);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MyOfferedSubjectsResponse> call, Response<MyOfferedSubjectsResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<TutorshipSubject> subjects = response.body().getSubjects();
//                    Log.d("response body", subjects.get(0).getSubjectName());
                    if (subjects == null || subjects.isEmpty()) {
                        noSubjectsText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noSubjectsText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        try {
                            recyclerView.setAdapter(new SubjectsAdapter(subjects, MyOfferedSubjects.this));
                        } catch (Exception e) {
                            Toast.makeText(MyOfferedSubjects.this, "Adapter error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
//                        recyclerView.setAdapter(new SubjectsAdapter(subjects, MyOfferedSubjects.this));
                    }
                } else {
                    Toast.makeText(MyOfferedSubjects.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyOfferedSubjectsResponse> call, Throwable t) {
                Toast.makeText(MyOfferedSubjects.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
