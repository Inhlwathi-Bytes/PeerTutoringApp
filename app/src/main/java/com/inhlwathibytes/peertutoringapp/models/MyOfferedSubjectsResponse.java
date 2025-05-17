package com.inhlwathibytes.peertutoringapp.models;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyOfferedSubjectsResponse {
    @SerializedName("subjects")
    private List<TutorshipSubject> subjects;

    public List<TutorshipSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<TutorshipSubject> subjects) {
        this.subjects = subjects;
    }
}
