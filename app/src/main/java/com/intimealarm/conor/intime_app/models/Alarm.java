package com.intimealarm.conor.intime_app.models;

import java.io.Serializable;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 21/11/2016.
 */

public class Alarm implements Serializable{

    // Variables
    int id, to, from, preptime;
    String time, dueTime;
    int[] days;
    String[] tmodes;
    Boolean isActive, isPublic;

    // Constructor - Overloaded
    public Alarm(int id, String time, int[] days, int from, int to, String dueTime, Boolean isActive, Boolean isPublic, String[] tmodes, int preptime) {
        this.id = id;
        this.to = to;
        this.days = days;
        this.time = time;
        this.from = from;
        this.dueTime = dueTime;
        this.isActive = isActive;
        this.isPublic = isPublic;
        this.tmodes = tmodes;
        this.preptime = preptime;
    }

    // Constructor
    public Alarm() {

    }

    /*
        Getters and Setters
     */

    public int getPreptime() {
        return preptime;
    }

    public void setPreptime(int preptime) {
        this.preptime = preptime;
    }

    public String[] getTmodes() {
        return tmodes;
    }

    public void setTmodes(String[] tmodes) {
        this.tmodes = tmodes;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public int getId() {
        return id;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public int[] getDays() {
        return days;
    }

    public void setDays(int[] days) {
        this.days = days;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
