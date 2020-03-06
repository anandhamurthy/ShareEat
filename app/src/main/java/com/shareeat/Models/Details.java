package com.shareeat.Models;

public class Details {

    String tp,kg, ft,de,online, user_id;
    double lon,lat;

    public Details() {
    }

    public Details(String tp, String kg, String ft, String de, String online, String user_id, double lon, double lat) {
        this.tp = tp;
        this.kg = kg;
        this.ft = ft;
        this.de = de;
        this.online = online;
        this.user_id = user_id;
        this.lon = lon;
        this.lat = lat;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getKg() {
        return kg;
    }

    public void setKg(String kg) {
        this.kg = kg;
    }

    public String getFt() {
        return ft;
    }

    public void setFt(String ft) {
        this.ft = ft;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
