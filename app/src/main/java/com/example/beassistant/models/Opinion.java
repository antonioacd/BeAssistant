package com.example.beassistant.models;

public class Opinion {

    private String opinionId;
    private String shopBuy;
    private double price;
    private String toneOrColor;
    private String opinion;
    private int rating;
    private Boolean visible;
    private String userId;
    private String productId;
    private String productCategory;
    private String productBrand;

    public Opinion(String opinionId, String shopBuy, double price, String toneOrColor, String opinion, int rating, Boolean visible, String userId, String productId, String productCategory, String productBrand) {
        this.opinionId = opinionId;
        this.shopBuy = shopBuy;
        this.price = price;
        this.toneOrColor = toneOrColor;
        this.opinion = opinion;
        this.rating = rating;
        this.visible = visible;
        this.userId = userId;
        this.productId = productId;
        this.productCategory = productCategory;
        this.productBrand = productBrand;
    }

    public Opinion(String opinionId) {
        this.opinionId = opinionId;
    }

    public String getOpinionId() {
        return opinionId;
    }

    public void setOpinionId(String opinionId) {
        this.opinionId = opinionId;
    }

    public String getShopBuy() {
        return shopBuy;
    }

    public void setShopBuy(String shopBuy) {
        this.shopBuy = shopBuy;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
                ", shopBuy='" + shopBuy + '\'' +
                ", price=" + price +
                ", toneOrColor='" + toneOrColor + '\'' +
                ", opinion='" + opinion + '\'' +
                ", rating=" + rating +
                ", visible=" + visible +
                ", userId='" + userId + '\'' +
                ", productId='" + productId + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", productBrand='" + productBrand + '\'' +
                '}';
    }
}
