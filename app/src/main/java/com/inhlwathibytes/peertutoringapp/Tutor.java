package com.inhlwathibytes.peertutoringapp;

public class Tutor {
    private int id;
    private String subject;
    private float rating;  // assume you’ll store this later
    private String name;
    private String surname;
    private String category;
    private String description;
    private String email;

    public Tutor(int id, String subject, float rating, String name, String surname, String category, String description, String email) {
        this.id = id;
        this.subject = subject;
        this.rating = rating;
        this.name = name;
        this.surname = surname;
        this.category = category;
        this.description = description;
        this.email = email;
    }

    // Getters
    public int getId() { return id; }
    public String getSubject() { return subject; }
    public float getRating() { return rating; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getEmail() { return email; }
}
