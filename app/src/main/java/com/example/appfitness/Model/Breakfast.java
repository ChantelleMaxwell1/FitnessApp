package com.example.appfitness.Model;

public class Breakfast {

    public String image;
    public String itemName;
    public String calories;
    public String protein;
    public String fat;
    public String carbs;
    public String timestamp;
    public String userID;

    public Breakfast(){

    }

    public Breakfast(String image, String itemName, String calories, String protein, String fat, String carbs, String timestamp, String userID) {
        this.image = image;
        this.itemName = itemName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.timestamp = timestamp;
        this.userID = userID;
    }

    /*public Breakfast(String imageUrl, String itemName, String calories, String protein, String fat, String carbs, String timestamp, String userID) {
    }*/

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
