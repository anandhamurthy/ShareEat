package com.shareeat.Models;

public class Donated {

    String user_id, sender_user_id, food_type, food_name, best_before, quantity;

    public Donated() {
    }

    public Donated(String user_id, String sender_user_id, String food_type, String food_name, String best_before, String quantity) {
        this.user_id = user_id;
        this.sender_user_id = sender_user_id;
        this.food_type = food_type;
        this.food_name = food_name;
        this.best_before = best_before;
        this.quantity = quantity;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
