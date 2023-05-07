package com.example.beassistant.models;

import android.graphics.Path;

public class Producto {

    private String uuID;
    private String name;
    private String img_reference;
    private String brand;
    private String category;
    private String type;
    private double mediaRating;
    private Opinion opinion;

    public Producto(String uuID, String name, String img_reference, String brand, String category, String type, double mediaRating) {
        this.uuID = uuID;
        this.name = name;
        this.img_reference = img_reference;
        this.brand = brand;
        this.category = category;
        this.type = type;
        this.mediaRating = mediaRating;
    }

    public Producto(String uuID, String name, String img_reference, String brand, String category, String type, double mediaRating, Opinion opinion) {
        this.uuID = uuID;
        this.name = name;
        this.img_reference = img_reference;
        this.brand = brand;
        this.category = category;
        this.type = type;
        this.mediaRating = mediaRating;
        this.opinion = opinion;
    }

    public String getUuID() {
        return uuID;
    }

    public void setUuID(String uuID) {
        this.uuID = uuID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_reference() {
        return img_reference;
    }

    public void setImg_reference(String img_reference) {
        this.img_reference = img_reference;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMediaRating() {
        return mediaRating;
    }

    public void setMediaRating(double mediaRating) {
        this.mediaRating = mediaRating;
    }

    public Opinion getOpinion() {
        return opinion;
    }

    public void setOpinion(Opinion opinion) {
        this.opinion = opinion;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "uuID='" + uuID + '\'' +
                ", name='" + name + '\'' +
                ", img_reference='" + img_reference + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", mediaRating='" + mediaRating + '\'' +
                ", opinion=" + opinion +
                '}';
    }
}
