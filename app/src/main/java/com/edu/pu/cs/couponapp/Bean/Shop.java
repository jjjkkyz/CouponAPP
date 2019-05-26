package com.edu.pu.cs.couponapp.Bean;

public class Shop {
    private String abbreviation;
    private String title;
    private String titlelogo;
    private double latitude;
    private double longitude;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitlelogo() {
        return titlelogo;
    }

    public void setTitlelogo(String titlelogo) {
        this.titlelogo = titlelogo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
