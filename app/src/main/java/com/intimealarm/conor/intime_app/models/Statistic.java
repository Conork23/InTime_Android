package com.intimealarm.conor.intime_app.models;

import java.io.Serializable;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 26/04/2017.
 */

public class Statistic implements Serializable {

    // Variables
    String from, to;
    Boolean isPublic, arrivedOnTime;

    // Constuctor - Overloaded
    public Statistic(String from, String to,Boolean isPublic, Boolean arrivedOnTime) {
        this.from = from;
        this.to = to;
        this.isPublic = isPublic;
        this.arrivedOnTime = arrivedOnTime;
    }

    /*
        Getters and Setters
     */

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getArrivedOnTime() {
        return arrivedOnTime;
    }

    public void setArrivedOnTime(Boolean arrivedOnTime) {
        this.arrivedOnTime = arrivedOnTime;
    }
}
