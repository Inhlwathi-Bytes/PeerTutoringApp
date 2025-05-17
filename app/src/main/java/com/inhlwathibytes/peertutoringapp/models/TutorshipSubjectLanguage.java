package com.inhlwathibytes.peertutoringapp.models;

public class TutorshipSubjectLanguage {
    private int id;
    private String languageName;

    public TutorshipSubjectLanguage() {
    }

    public TutorshipSubjectLanguage(int id, String languageName) {
        this.id = id;
        this.languageName = languageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}

