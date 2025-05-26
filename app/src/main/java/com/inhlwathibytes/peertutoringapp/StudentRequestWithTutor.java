package com.inhlwathibytes.peertutoringapp;

public class StudentRequestWithTutor {
    private TutorRequest request;
    private Tutor tutor;

    public StudentRequestWithTutor(TutorRequest request, Tutor tutor) {
        this.request = request;
        this.tutor = tutor;
    }

    public TutorRequest getRequest() {
        return request;
    }

    public Tutor getTutor() {
        return tutor;
    }
}