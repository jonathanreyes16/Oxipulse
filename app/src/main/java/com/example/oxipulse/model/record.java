package com.example.oxipulse.model;

import java.io.Serializable;

public class record implements Serializable {
    String date,degree_of_urgency,id,hr,oxi,tag;

    public record(String date, String degree_of_urgency, String id, String hr, String oxi, String tag) {
        this.date = date;
        this.degree_of_urgency = degree_of_urgency;
        this.id = id;
        this.hr = hr;
        this.oxi = oxi;
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDegree_of_urgency() {
        return degree_of_urgency;
    }

    public void setDegree_of_urgency(String degree_of_urgency) {
        this.degree_of_urgency = degree_of_urgency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHr() {
        return hr;
    }

    public void setHr(String hr) {
        this.hr = hr;
    }

    public String getOxi() {
        return oxi;
    }

    public void setOxi(String oxi) {
        this.oxi = oxi;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
