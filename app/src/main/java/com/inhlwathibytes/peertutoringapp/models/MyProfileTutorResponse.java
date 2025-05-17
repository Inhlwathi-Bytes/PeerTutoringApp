package com.inhlwathibytes.peertutoringapp.models;

import java.util.List;

public class MyProfileTutorResponse {

    private int id;
    private String systemUserId;
    private String bio;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String highestAchievement;
    private boolean isAvailable;
    private int yearsOfExperience;
    private int age;
    private String createdAt; // or use Date if you're parsing dates
    private double rating;
    private String profilePhotoPath;
    private String name;
    private String surname;
    private String email;
    private List<TutorshipSubjectDto> tutorshipSubjects;
    private List<TutorshipLanguageDto> tutorshipLanguages;

    // Getters and Setters for all fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(String systemUserId) {
        this.systemUserId = systemUserId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getHighestAchievement() {
        return highestAchievement;
    }

    public void setHighestAchievement(String highestAchievement) {
        this.highestAchievement = highestAchievement;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<TutorshipSubjectDto> getTutorshipSubjects() {
        return tutorshipSubjects;
    }

    public void setTutorshipSubjects(List<TutorshipSubjectDto> tutorshipSubjects) {
        this.tutorshipSubjects = tutorshipSubjects;
    }

    public List<TutorshipLanguageDto> getTutorshipLanguages() {
        return tutorshipLanguages;
    }

    public void setTutorshipLanguages(List<TutorshipLanguageDto> tutorshipLanguages) {
        this.tutorshipLanguages = tutorshipLanguages;
    }

    // Inner class: TutorshipSubjectDto
    public static class TutorshipSubjectDto {
        private String name;
        private String availability;
        private String outline;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }

        public String getOutline() {
            return outline;
        }

        public void setOutline(String outline) {
            this.outline = outline;
        }
    }

    // Inner class: TutorshipLanguageDto
    public static class TutorshipLanguageDto {
        private String languageName;

        public String getLanguageName() {
            return languageName;
        }

        public void setLanguageName(String languageName) {
            this.languageName = languageName;
        }
    }
}
