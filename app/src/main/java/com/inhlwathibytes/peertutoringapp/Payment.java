package com.inhlwathibytes.peertutoringapp;

public class Payment {
    private int id;
    private double amount;
    private String date;
    private String status;
    private String reference;
    private int appointmentId;

    public Payment(int id, double amount, String date, String status, String reference, int appointmentId) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.reference = reference;
        this.appointmentId = appointmentId;
    }

    // Getters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public String getReference() { return reference; }
    public int getAppointmentId() { return appointmentId; }
}