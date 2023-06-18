package com.example.beassistant.models;

public class Opinion {

    private String opinionId;
    private String username;
    private String imgUser;
    private String productId;
    private String productCategory;
    private String productBrand;
    private int rating;
    private double price;
    private String shopBuy;
    private String toneOrColor;
    private String opinion;

    public Opinion(String opinionId, String username, String imgUser, String productId, String productCategory, String productBrand, int rating, double price, String shopBuy, String toneOrColor, String opinion) {
        this.opinionId = opinionId;
        this.username = username;
        this.imgUser = imgUser;
        this.productId = productId;
        this.productCategory = productCategory;
        this.productBrand = productBrand;
        this.rating = rating;
        this.price = price;
        this.shopBuy = shopBuy;
        this.toneOrColor = toneOrColor;
        this.opinion = opinion;
    }

    public Opinion() {
    }

    public String getOpinionId() {
        return opinionId;
    }

    public void setOpinionId(String opinionId) {
        this.opinionId = opinionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgUser() {
        return imgUser;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getShopBuy() {
        return shopBuy;
    }

    public void setShopBuy(String shopBuy) {
        this.shopBuy = shopBuy;
    }

    public String getToneOrColor() {
        return toneOrColor;
    }

    public void setToneOrColor(String toneOrColor) {
        this.toneOrColor = toneOrColor;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    @Override
    public String toString() {
        return "Opinion{" +
                "opinionId='" + opinionId + '\'' +
                ", username='" + username + '\'' +
                ", imgUser='" + imgUser + '\'' +
                ", productId='" + productId + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", productBrand='" + productBrand + '\'' +
                ", rating=" + rating +
                ", price=" + price +
                ", shopBuy='" + shopBuy + '\'' +
                ", toneOrColor='" + toneOrColor + '\'' +
                ", opinion='" + opinion + '\'' +
                '}';
    }
}