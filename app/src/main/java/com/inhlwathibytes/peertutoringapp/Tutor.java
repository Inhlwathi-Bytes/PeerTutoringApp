package com.inhlwathibytes.peertutoringapp;

public class Tutor {
    private int id;
    private String subject;
    private float rating;
    private String name;
    private String surname;
    private String category;
    private String description;
    private String email;
    private String phone;
    private int ratingCount;

    public Tutor(int id, String subject, float rating, String name, String surname,
                 String category, String description, String email, String phone) {
        this.id = id;
        this.subject = subject;
        this.rating = rating;
        this.name = name;
        this.surname = surname;
        this.category = category;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.ratingCount = 0; // Initialize to 0, will be set from database
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public float getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    // Setters (optional, add if needed)
    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    // Helper method to get full name
    public String getFullName() {
        return name + " " + surname;
    }

    // Helper method to get formatted rating
    public String getFormattedRating() {
        return String.format("%.1f", rating);
    }
}