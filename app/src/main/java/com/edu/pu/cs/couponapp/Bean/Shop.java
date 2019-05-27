package com.edu.pu.cs.couponapp.Bean;

public class Shop {
    private String abbreviation;
    private String title;
    private String titlelogo;
    private String address;
    private Double latitude;
    private Double longitude;

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

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
