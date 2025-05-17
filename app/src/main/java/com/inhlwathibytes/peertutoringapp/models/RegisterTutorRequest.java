package com.inhlwathibytes.peertutoringapp.models;

import java.util.List;

public class RegisterTutorRequest {
    private String bio;
    private String highestAchievement;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private boolean isAvailable;
    private int yearsOfExperience;
    private int age;
    private List<Integer> languageIds;
    private String profilePhotoPath;

    public RegisterTutorRequest(String bio, String highestAchievement, String street,
                                String city, String province, int age, String postalCode,
                                boolean isAvailable, int yearsOfExperience,
                                List<Integer> languageIds, String profilePhotoPath) {
        this.bio = bio;
        this.highestAchievement = highestAchievement;
        this.street = street;
        this.city = city;
        this.province = province;
        this.age = age;
        this.postalCode = postalCode;
        this.isAvailable = isAvailable;
        this.yearsOfExperience = yearsOfExperience;
        this.languageIds = languageIds;
        this.profilePhotoPath = profilePhotoPath;
    }

    // Getters and setters (optional if using Gson)
}

