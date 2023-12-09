package com.epet.epet.backend;

import android.icu.lang.UProperty;

import java.util.Date;

public class Pet {
    private int Id;
    private String Name;
    private String Birthdate;
    private String Type;
    private String Breed;
    private String ImageUrl;

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public String getType() {
        return Type;
    }

    public String getBreed() {
        return Breed;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public Pet(int id, String name, String birthdate, String type, String breed, String imageUrl) {
        Id = id;
        Name = name;
        Birthdate = birthdate;
        Type = type;
        Breed = breed;
        ImageUrl = imageUrl;
    }
}
