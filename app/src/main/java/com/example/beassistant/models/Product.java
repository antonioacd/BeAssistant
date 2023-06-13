package com.example.beassistant.models;

public class Product {

    private String uuID;
    private String name;
    private String img_reference;
    private String brand;
    private String category;
    private String type;
    private double mediaRating;
    private String url;
    private Opinion opinion;

    public Product(String uuID, String name, String img_reference, String brand, String category, String type, double mediaRating, String url) {
        this.uuID = uuID;
        this.name = name;
        this.img_reference = img_reference;
        this.brand = brand;
        this.category = category;
        this.type = type;
        this.mediaRating = mediaRating;
        this.url = url;
    }

    public Product(String uuID, String name, String img_reference, String brand, String category, String type, double mediaRating, Opinion opinion) {
        this.uuID = uuID;
        this.name = name;
        this.img_reference = img_reference;
        this.brand = brand;
        this.category = category;
        this.type = type;
        this.mediaRating = mediaRating;
        this.opinion = opinion;
    }

    public Product(String uuID, String name, String img_reference) {
        this.uuID = uuID;
        this.name = name;
        this.img_reference = img_reference;
    }

    public Product (String uuID){
        this.uuID = uuID;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
