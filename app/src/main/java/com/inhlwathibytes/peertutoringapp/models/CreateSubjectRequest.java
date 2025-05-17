package com.inhlwathibytes.peertutoringapp.models;

public class CreateSubjectRequest {

    private String subjectName;
    private String level;
    private double hourlyRate;
    private String availability;
    private String outline;
    private String deliveryMode;
    private String coverImage; // Optional – base64 or URL
    private String introVideo; // Optional – base64 or URL

    public CreateSubjectRequest() {
    }

    public CreateSubjectRequest(String subjectName, String level, double hourlyRate,
                                String availability, String outline, String deliveryMode,
                                String coverImage, String introVideo) {
        this.subjectName = subjectName;
        this.level = level;
        this.hourlyRate = hourlyRate;
        this.availability = availability;
        this.outline = outline;
        this.deliveryMode = deliveryMode;
        this.coverImage = coverImage;
        this.introVideo = introVideo;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDeliveryMode(){ return  deliveryMode; }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getIntroVideo() {
        return introVideo;
    }

    public void setIntroVideo(String introVideo) {
        this.introVideo = introVideo;
    }
}

