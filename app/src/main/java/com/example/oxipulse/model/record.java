package com.example.oxipulse.model;

import java.io.Serializable;

public class record  {
    String date,degree_of_urgency,hr,oxi,tag;

    public record() {
    }

    public record(String date, String degree_of_urgency, String id, String hr, String oxi, String tag) {
        this.date = date;
        this.degree_of_urgency = degree_of_urgency;

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
