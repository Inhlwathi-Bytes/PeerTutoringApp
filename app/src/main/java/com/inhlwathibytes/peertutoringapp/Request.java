package com.inhlwathibytes.peertutoringapp;

public class Request {
    private int id;
    private String studentEmail;
    private String tutorEmail;
    private String status;

    public Request(int id, String studentEmail, String tutorEmail, String status) {
        this.id = id;
        this.studentEmail = studentEmail;
        this.tutorEmail = tutorEmail;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getStudentEmail() { return studentEmail; }
    public String getTutorEmail() { return tutorEmail; }
    public String getStatus() { return status; }
}
