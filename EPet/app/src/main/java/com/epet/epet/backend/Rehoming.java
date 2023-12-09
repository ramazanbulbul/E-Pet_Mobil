package com.epet.epet.backend;

public class Rehoming {
    private int Id;
    private String Username;
    private String Description;
    private String ImageUrl;
    private String PhoneNumber;
    private String Adress;

    public Rehoming(int id, String username, String description, String imageUrl, String phoneNumber, String adress) {
        Id = id;
        Username = username;
        Description = description;
        ImageUrl = imageUrl;
        PhoneNumber = phoneNumber;
        Adress = adress;
    }

    public int getId() {
        return Id;
    }

    public String getUsername() {
        return Username;
    }

    public String getDescription() {
        return Description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getAdress() {
        return Adress;
    }
}
