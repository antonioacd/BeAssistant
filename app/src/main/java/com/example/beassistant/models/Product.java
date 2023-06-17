package com.example.beassistant.models;

public class Product {

    private String productId;
    private String productName;
    private String imgReference;
    private String brand;
    private String category;
    private String type;
    private double mediaRating;
    private String shopUrl;
    private Opinion opinion;

    public Product(String productId, String productName, String imgReference, String brand, String category, String type, double mediaRating, String shopUrl) {
        this.productId = productId;
        this.productName = productName;
        this.imgReference = imgReference;
        this.brand = brand;
        this.category = category;
        this.type = type;
        this.mediaRating = mediaRating;
        this.shopUrl = shopUrl;
    }

    public Product(String productId, String productName, String imgReference, String brand, String category, String type, double mediaRating, Opinion opinion) {
        this.productId = productId;
        this.productName = productName;
        this.imgReference = imgReference;
        this.brand = brand;
        this.category = category;
        this.type = type;
        this.mediaRating = mediaRating;
        this.opinion = opinion;
    }

    public Product(String productId, String productName, String imgReference) {
        this.productId = productId;
        this.productName = productName;
        this.imgReference = imgReference;
    }

    public Product (String productId){
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImgReference() {
        return imgReference;
    }

    public void setImgReference(String imgReference) {
        this.imgReference = imgReference;
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

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "uuID='" + productId + '\'' +
                ", name='" + productName + '\'' +
                ", img_reference='" + imgReference + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", mediaRating='" + mediaRating + '\'' +
                ", opinion=" + opinion +
                '}';
    }
}
