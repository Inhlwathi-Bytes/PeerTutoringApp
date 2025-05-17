package com.inhlwathibytes.peertutoringapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Tutorship {

    @SerializedName("id")
    private int id;

    @SerializedName("systemUserId")
    private String systemUserId;

    @SerializedName("bio")
    private String bio;

    @SerializedName("street")
    private String street;

    @SerializedName("city")
    private String city;

    @SerializedName("province")
    private String province;

    @SerializedName("postalCode")
    private String postalCode;

    @SerializedName("highestAchievement")
    private String highestAchievement;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("yearsOfExperience")
    private int yearsOfExperience;

    @SerializedName("age")
    private int age;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("rating")
    private double rating;

    @SerializedName("profilePhotoPath")
    private String profilePhotoPath;

    @SerializedName("tutorshipSubjects")
    private List<TutorshipSubject> tutorshipSubjects;

    @SerializedName("tutorshipLanguages")
    private List<TutorshipSubjectLanguage> tutorshipLanguages;

    // Getters and setters

    public int getId() {
        return id;
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

    public boolean isAvailable() {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
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

    public List<TutorshipSubject> getTutorshipSubjects() {
        return tutorshipSubjects;
    }

    public void setTutorshipSubjects(List<TutorshipSubject> tutorshipSubjects) {
        this.tutorshipSubjects = tutorshipSubjects;
    }

    public List<TutorshipSubjectLanguage> getTutorshipLanguages() {
        return tutorshipLanguages;
    }

    public void setTutorshipLanguages(List<TutorshipSubjectLanguage> tutorshipLanguages) {
        this.tutorshipLanguages = tutorshipLanguages;
    }
}
