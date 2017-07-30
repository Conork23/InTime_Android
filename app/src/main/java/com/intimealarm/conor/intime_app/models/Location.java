package com.intimealarm.conor.intime_app.models;

import java.io.Serializable;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 25/01/2017.
 */

public class Location implements Serializable{

    // Variables
    String lable, address;
    int id;
    double lat, lng;

    // Constructor - Overloaded
    public Location(int id, String lable, String address, double lat, double lng) {
        this.lng = lng;
        this.lable = lable;
        this.address = address;
        this.lat = lat;
        this.id = id;
    }

    // Constructor
    public Location() {

    }

    /*
        Getters and Setters
     */

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
