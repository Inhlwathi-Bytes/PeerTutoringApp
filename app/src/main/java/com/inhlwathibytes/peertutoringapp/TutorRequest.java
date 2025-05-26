package com.inhlwathibytes.peertutoringapp;

public class TutorRequest {
    private int id;
    private int tutorId;
    private int studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String status;
    private String requestDate;

    public TutorRequest(int id, int tutorId, int studentId, String studentName,
                        String studentEmail, String studentPhone, String status, String requestDate) {
        this.id = id;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.status = status;
        this.requestDate = requestDate;
    }

    // Getters
    public int getId() { return id; }
    public int getTutorId() { return tutorId; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getStudentPhone() { return studentPhone; }
    public String getStatus() { return status; }
    public String getRequestDate() { return requestDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }
    public void setStatus(String status) { this.status = status; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
}