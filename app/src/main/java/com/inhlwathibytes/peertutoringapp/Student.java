package com.inhlwathibytes.peertutoringapp;

public class Student {
    private int id;
    private String name;
    private String surname;
    private String studentNumber;
    private String institution;
    private String email;
    private String phone;

    public Student(int id, String name, String surname, String studentNumber,
                   String institution, String email, String phone) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.studentNumber = studentNumber;
        this.institution = institution;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getStudentNumber() { return studentNumber; }
    public String getInstitution() { return institution; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public String getFullName() { return name + " " + surname; }
}