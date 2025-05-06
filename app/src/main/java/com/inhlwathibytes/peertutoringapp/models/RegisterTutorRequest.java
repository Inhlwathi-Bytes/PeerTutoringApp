package com.inhlwathibytes.peertutoringapp.models;

import java.util.List;

public class RegisterTutorRequest {
    private String bio;
    private String qualifications;
    private String achievements;
    private boolean isAvailable;
    private int yearsOfExperience;
    private List<Integer> languageIds;
    private String profilePhotoBase64;

    public RegisterTutorRequest(String bio, String qualifications, String achievements,
                                boolean isAvailable, int yearsOfExperience,
                                List<Integer> languageIds, String profilePhotoBase64) {
        this.bio = bio;
        this.qualifications = qualifications;
        this.achievements = achievements;
        this.isAvailable = isAvailable;
        this.yearsOfExperience = yearsOfExperience;
        this.languageIds = languageIds;
        this.profilePhotoBase64 = profilePhotoBase64;
    }

    // Getters and setters (optional if using Gson)
}

