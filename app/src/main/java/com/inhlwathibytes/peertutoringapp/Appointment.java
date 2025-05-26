package com.inhlwathibytes.peertutoringapp;

public class Appointment {
    private int id;
    private int tutorId;
    private int studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String date;
    private String time;
    private String duration;
    private String subject;
    private String status;
    private String createdAt;

    public Appointment(int id, int tutorId, int studentId, String studentName,
                       String studentEmail, String studentPhone, String date,
                       String time, String duration, String subject,
                       String status, String createdAt) {
        this.id = id;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.subject = subject;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getTutorId() { return tutorId; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getStudentPhone() { return studentPhone; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDuration() { return duration; }
    public String getSubject() { return subject; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public void setStatus(String status) { this.status = status; }
}