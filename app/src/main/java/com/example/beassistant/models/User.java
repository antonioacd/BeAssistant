package com.example.beassistant.models;

public class User {

    private String userId;
    private String username;
    private String name;
    private String img_reference;
    private String email;
    private String password;
    private int numOpiniones;
    private int numSeguidores;
    private int numSeguidos;

    public User(String userId, String username, String name, String img_reference, String email, String password, int numOpiniones, int numSeguidores, int numSeguidos) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.img_reference = img_reference;
        this.email = email;
        this.password = password;
        this.numOpiniones = numOpiniones;
        this.numSeguidores = numSeguidores;
        this.numSeguidos = numSeguidos;
    }

    public User(String userId, String username, String img_reference) {
        this.userId = userId;
        this.username = username;
        this.img_reference = img_reference;
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumOpiniones() {
        return numOpiniones;
    }

    public void setNumOpiniones(int numOpiniones) {
        this.numOpiniones = numOpiniones;
    }

    public int getNumSeguidores() {
        return numSeguidores;
    }

    public void setNumSeguidores(int numSeguidores) {
        this.numSeguidores = numSeguidores;
    }

    public int getNumSeguidos() {
        return numSeguidos;
    }

    public void setNumSeguidos(int numSeguidos) {
        this.numSeguidos = numSeguidos;
    }
}
