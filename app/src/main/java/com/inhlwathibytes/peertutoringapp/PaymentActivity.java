package com.inhlwathibytes.peertutoringapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textViewBalance;
    private EditText editTextAmount, editTextReference;
    private Button buttonPay;
    private RecyclerView recyclerViewPayments;
    private PaymentAdapter adapter;
    private List<Payment> paymentList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        textViewBalance = findViewById(R.id.textViewBalance);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextReference = findViewById(R.id.editTextReference);
        buttonPay = findViewById(R.id.buttonPay);
        recyclerViewPayments = findViewById(R.id.recyclerViewPayments);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Setup toolbar
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn() || !"student".equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Please login as a student", Toast.LENGTH_SHORT).show();
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

        dbHelper = new DatabaseHelper(this);
        paymentList = new ArrayList<>();

        // Setup RecyclerView
        recyclerViewPayments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPayments.setHasFixedSize(true);
        adapter = new PaymentAdapter(paymentList);
        recyclerViewPayments.setAdapter(adapter);

        // Load balance and payment history
        updateBalance();
        loadPaymentHistory();

        // Setup pay button
        buttonPay.setOnClickListener(v -> processPayment());

        // Setup back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> onBackPressed());
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

    private void updateBalance() {
        double balance = dbHelper.getOutstandingBalance(sessionManager.getUserId());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
        textViewBalance.setText(format.format(balance));
    }

    private void loadPaymentHistory() {
        paymentList.clear();

        Cursor cursor = dbHelper.getPaymentHistory(sessionManager.getUserId());

        if (cursor == null) {
            // Table doesn't exist yet
            Toast.makeText(this, "Payment history not available yet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.moveToFirst()) {
            do {
                // Process payment records
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PAYMENT_ID));
                // ... rest of your code ...
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No payment history found", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    private void processPayment() {
        String amountStr = editTextAmount.getText().toString().trim();
        String reference = editTextReference.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter payment amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reference.isEmpty()) {
            Toast.makeText(this, "Please enter payment reference", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            double balance = dbHelper.getOutstandingBalance(sessionManager.getUserId());

            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }

            // For simplicity, we're just recording the payment
            // In a real app, you would integrate with a payment gateway here
            boolean success = dbHelper.recordPayment(
                    sessionManager.getUserId(),
                    amount,
                    reference,
                    0); // 0 means general payment, not tied to specific appointment

            if (success) {
                Toast.makeText(this, "Payment recorded successfully", Toast.LENGTH_SHORT).show();
                editTextAmount.setText("");
                editTextReference.setText("");
                updateBalance();
                loadPaymentHistory();
            } else {
                Toast.makeText(this, "Failed to record payment", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            startActivity(new Intent(this, StudentDashboardActivity.class));
        } else if (id == R.id.nav_find_tutor) {
            startActivity(new Intent(this, FindTutorActivity.class));
        } else if (id == R.id.nav_my_tutors) {
            startActivity(new Intent(this, MyTutorsActivity.class));
        } else if (id == R.id.nav_appointments) {
            startActivity(new Intent(this, BookAppointmentActivity.class));
        } else if (id == R.id.nav_status) {
            startActivity(new Intent(this, AppointmentStatusActivity.class));
        }  else if (id == R.id.nav_profile) {
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

    private class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {
        private List<Payment> payments;

        public PaymentAdapter(List<Payment> payments) {
            this.payments = payments;
        }

        @Override
        public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_payment, parent, false);
            return new PaymentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PaymentViewHolder holder, int position) {
            Payment payment = payments.get(position);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));

            holder.textAmount.setText(format.format(payment.getAmount()));
            holder.textDate.setText(payment.getDate());
            holder.textReference.setText(payment.getReference());
            holder.textStatus.setText(payment.getStatus());
        }

        @Override
        public int getItemCount() {
            return payments.size();
        }

        class PaymentViewHolder extends RecyclerView.ViewHolder {
            TextView textAmount, textDate, textReference, textStatus;

            public PaymentViewHolder(View itemView) {
                super(itemView);
                textAmount = itemView.findViewById(R.id.textAmount);
                textDate = itemView.findViewById(R.id.textDate);
                textReference = itemView.findViewById(R.id.textReference);
                textStatus = itemView.findViewById(R.id.textStatus);
            }
        }
    }
}