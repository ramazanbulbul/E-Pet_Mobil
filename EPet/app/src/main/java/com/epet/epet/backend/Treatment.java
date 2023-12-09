package com.epet.epet.backend;

public class Treatment {
    private String ClinicName;
    private String Date;
    private String Title;
    private String Description;

    public Treatment(String clinicName, String date, String title, String description) {
        ClinicName = clinicName;
        Date = date;
        Title = title;
        Description = description;
    }

    public String getClinicName() {
        return ClinicName;
    }

    public String getDate() {
        return Date;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }
}
