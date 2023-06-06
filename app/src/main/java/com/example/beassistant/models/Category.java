package com.example.beassistant.models;

public class Category {

    private String category_name;
    private String number_of_opinions;

    public Category(String category_name, String number_of_opinions) {
        this.category_name = category_name;
        this.number_of_opinions = number_of_opinions;
    }

    public Category() {
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getNumber_of_opinions() {
        return number_of_opinions;
    }

    public void setNumber_of_opinions(String number_of_opinions) {
        this.number_of_opinions = number_of_opinions;
    }

    @Override
    public String toString() {
        return "Category{" +
                "category_name='" + category_name + '\'' +
                ", number_of_opinions='" + number_of_opinions + '\'' +
                '}';
    }
}
