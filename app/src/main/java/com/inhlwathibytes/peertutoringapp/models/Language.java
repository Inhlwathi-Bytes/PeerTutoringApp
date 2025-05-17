package com.inhlwathibytes.peertutoringapp.models;

import androidx.annotation.NonNull;

public class Language {
    private int Id;
    private String Name;

    public Language(){}
    public Language (int id, String name){
        this.Id = id;
        this.Name = name;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    @NonNull
    @Override
    public String toString() {
        return Name; // used by ArrayAdapter & filtering
    }
}
