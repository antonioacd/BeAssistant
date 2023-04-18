package com.example.beassistant.models;

public class Producto {

    private String personal_id;
    private String id;
    private String img_reference;
    private String category;
    private String type;
    private String shopBuy;
    private double price;
    private String toneOrColor;
    private String opinion;

    public Producto(String personal_id, String id, String img_reference, String category, String type, String shopBuy, double price, String toneOrColor, String opinion) {
        this.personal_id = personal_id;
        this.id = id;
        this.img_reference = img_reference;
        this.category = category;
        this.type = type;
        this.shopBuy = shopBuy;
        this.price = price;
        this.toneOrColor = toneOrColor;
        this.opinion = opinion;
    }

    public Producto(String personal_id ,String id, String category, String type, String shopBuy, double price, String toneOrColor, String opinion) {
        this.personal_id = personal_id;
        this.id = id;
        this.category = category;
        this.type = type;
        this.shopBuy = shopBuy;
        this.price = price;
        this.toneOrColor = toneOrColor;
        this.opinion = opinion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_reference() {
        return img_reference;
    }

    public void setImg_reference(String img_reference) {
        this.img_reference = img_reference;
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

    public String getPersonal_id() {
        return personal_id;
    }

    public void setPersonal_id(String personal_id) {
        this.personal_id = personal_id;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "personal_id='" + personal_id + '\'' +
                ", id='" + id + '\'' +
                ", img_reference='" + img_reference + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", shopBuy='" + shopBuy + '\'' +
                ", price=" + price +
                ", toneOrColor='" + toneOrColor + '\'' +
                ", opinion='" + opinion + '\'' +
                '}';
    }
}
