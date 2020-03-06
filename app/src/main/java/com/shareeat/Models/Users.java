package com.shareeat.Models;

public class Users {

    String address, email_id, user_id, device_token, source, phone_number, name;
    double lon,lat;

    public Users() {
    }

    public Users(String address, String email_id, String user_id, String device_token, String source, String phone_number, String name, double lon, double lat) {
        this.address = address;
        this.email_id = email_id;
        this.user_id = user_id;
        this.device_token = device_token;
        this.source = source;
        this.phone_number = phone_number;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
