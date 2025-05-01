package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDashboardActivity extends AppCompatActivity {

    CardView findTutorCard, myTutorsCard, appointmentStatusCard, chatCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        findTutorCard = findViewById(R.id.cardFindTutor);
        myTutorsCard = findViewById(R.id.cardMyTutors);
        appointmentStatusCard = findViewById(R.id.cardAppointmentStatus);
        chatCard = findViewById(R.id.cardChat);

        findTutorCard.setOnClickListener(v -> {
            startActivity(new Intent(this, FindTutorActivity.class));
        });

        myTutorsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, MyTutorsActivity.class));
        });

        appointmentStatusCard.setOnClickListener(v -> {
            startActivity(new Intent(this, AppointmentStatusActivity.class));
        });

        chatCard.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentChatActivity.class));
        });
    }
}
