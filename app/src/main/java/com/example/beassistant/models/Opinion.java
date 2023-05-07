package com.example.beassistant.models;

public class Opinion {

    private String shopBuy;
    private double price;
    private String toneOrColor;
    private String opinion;
    private String rating;

    public Opinion(String shopBuy, double price, String toneOrColor, String opinion, String rating) {
        this.shopBuy = shopBuy;
        this.price = price;
        this.toneOrColor = toneOrColor;
        this.opinion = opinion;
        this.rating = rating;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Opinion{" +
                "shopBuy='" + shopBuy + '\'' +
                ", price=" + price +
                ", toneOrColor='" + toneOrColor + '\'' +
                ", opinion='" + opinion + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}
