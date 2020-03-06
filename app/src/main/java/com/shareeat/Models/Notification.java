package com.shareeat.Models;

public class Notification {
    String message, user_id, sender_user_id, address, food_type, food_name, best_before, distance, quantity, noti_key;
    boolean accepted, sent;

    public Notification() {
    }

    public Notification(String message, String user_id, String sender_user_id, String address, String food_type, String food_name, String best_before, String distance, String quantity, String noti_key, boolean accepted, boolean sent) {
        this.message = message;
        this.user_id = user_id;
        this.sender_user_id = sender_user_id;
        this.address = address;
        this.food_type = food_type;
        this.food_name = food_name;
        this.best_before = best_before;
        this.distance = distance;
        this.quantity = quantity;
        this.noti_key = noti_key;
        this.accepted = accepted;
        this.sent = sent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSender_user_id() {
        return sender_user_id;
    }

    public void setSender_user_id(String sender_user_id) {
        this.sender_user_id = sender_user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getBest_before() {
        return best_before;
    }

    public void setBest_before(String best_before) {
        this.best_before = best_before;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getNoti_key() {
        return noti_key;
    }

    public void setNoti_key(String noti_key) {
        this.noti_key = noti_key;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
