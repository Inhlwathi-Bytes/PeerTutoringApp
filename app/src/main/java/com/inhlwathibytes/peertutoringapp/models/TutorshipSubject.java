package com.inhlwathibytes.peertutoringapp.models;

import java.util.List;

public class TutorshipSubject {
    private int id;
    private String subjectName;
    private String availability;
    private String outline;
    private double hourlyRate;
    private String level;

    private String deliveryMode;
    private String coverImagePath;
    private String introVideoLink;
    private int tutorshipId;

    private Tutorship tutorship;
//    private List<TutorshipSubjectLanguage> tutorshipSubjectLanguages;

    public TutorshipSubject() {
    }

    public TutorshipSubject(int id, String subjectName, String availability, String outline,
                            double hourlyRate, String level, String deliveryMode, String coverImagePath,
                            String introVideoLink, int tutorshipId,
                            List<TutorshipSubjectLanguage> tutorshipSubjectLanguages) {
        this.id = id;
        this.subjectName = subjectName;
        this.availability = availability;
        this.outline = outline;
        this.hourlyRate = hourlyRate;
        this.level = level;
        this.coverImagePath = coverImagePath;
        this.introVideoLink = introVideoLink;
        this.tutorshipId = tutorshipId;
        this.deliveryMode = deliveryMode;
//        this.tutorshipSubjectLanguages = tutorshipSubjectLanguages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDeliveryMode() {return  deliveryMode; }

    public void setDeliveryMode(String deliveryMode) { this.deliveryMode = deliveryMode; }

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

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public String getIntroVideoLink() {
        return introVideoLink;
    }

    public void setIntroVideoLink(String introVideoLink) {
        this.introVideoLink = introVideoLink;
    }

    public int getTutorshipId() {
        return tutorshipId;
    }

    public void setTutorshipId(int tutorshipId) {
        this.tutorshipId = tutorshipId;
    }

    public Tutorship getTutorship() {
        return tutorship;
    }

    public void setTutorship(Tutorship tutorship) {
        this.tutorship = tutorship;
    }

//    public List<TutorshipSubjectLanguage> getTutorshipSubjectLanguages() {
//        return tutorshipSubjectLanguages;
//    }
//
//    public void setTutorshipSubjectLanguages(List<TutorshipSubjectLanguage> tutorshipSubjectLanguages) {
//        this.tutorshipSubjectLanguages = tutorshipSubjectLanguages;
//    }
}
