package com.app.iriding.model;

import java.io.Serializable;

/**
 * Created by Íõº£ on 2015/5/30.
 */
public class CyclingPoint implements Serializable{
    private double latitude;
    private double longitude;

    public CyclingPoint(double latitude, double longitude){
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
