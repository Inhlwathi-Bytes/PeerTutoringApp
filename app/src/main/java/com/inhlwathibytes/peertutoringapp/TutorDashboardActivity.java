package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;

public class TutorDashboardActivity extends AppCompatActivity {

    CardView studentRequestCard, appointmentsCard, scheduleCard, myStudentsCard, chatCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_dashboard);

        studentRequestCard = findViewById(R.id.cardStudentRequest);
        appointmentsCard = findViewById(R.id.cardAppointments);
        scheduleCard = findViewById(R.id.cardSchedule);
        myStudentsCard = findViewById(R.id.cardMyStudents);
        chatCard = findViewById(R.id.cardChat);

        studentRequestCard.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentRequestActivity.class));
        });

        appointmentsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, AppointmentsActivity.class));
        });

        scheduleCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ScheduleActivity.class));
        });

        myStudentsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, MyStudentsActivity.class));
        });

        chatCard.setOnClickListener(v -> {
            startActivity(new Intent(this, TutorChatActivity.class));
        });
    }
}
