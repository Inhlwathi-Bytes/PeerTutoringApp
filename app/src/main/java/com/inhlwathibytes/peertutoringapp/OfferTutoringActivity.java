package com.inhlwathibytes.peertutoringapp;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.inhlwathibytes.peertutoringapp.databinding.ActivityOfferTutoringBinding;
import android.content.Intent;


public class OfferTutoringActivity extends BaseActivity {

    private Button btnRegisterTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_offer_tutoring);

        btnRegisterTutor = findViewById(R.id.btnRegisterTutor);

        btnRegisterTutor.setOnClickListener(v -> {
            Intent intent = new Intent(OfferTutoringActivity.this, RegisterAsTutor.class);
            startActivity(intent);
        });

    }
}
