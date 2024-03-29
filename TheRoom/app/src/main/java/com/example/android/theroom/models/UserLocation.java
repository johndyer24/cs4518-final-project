package com.example.android.theroom.models;

/**
 * Created by johndyer on 12/14/17.
 */

public class UserLocation {

    private double latitude;
    private double longitude;

    public UserLocation(){}

    public UserLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
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
