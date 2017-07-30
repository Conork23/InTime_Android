package com.intimealarm.conor.intime_app.models;

import java.io.Serializable;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 06/03/2017.
 */

public class Evaluation implements Serializable {

    // Variables
    private String time, estimated, actual, from, to;
    boolean isPublic;

    // Constructor - Overloaded
    public Evaluation(String from, String time, String estimated, String to, boolean isPublic) {
        this.from = from;
        this.time = time;
        this.estimated = estimated;
        this.to = to;
        this.isPublic = isPublic;
    }

    // Constructor
    public Evaluation() {
    }

    /*
        Getters and Setters
     */

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEstimated() {
        return estimated;
    }

    public void setEstimated(String estimated) {
        this.estimated = estimated;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

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

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
