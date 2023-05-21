package com.example.beassistant.models;

public class UserInAList {

    private String id;
    private String username;
    private String imgRef;

    public UserInAList(String id, String username, String imgRef) {
        this.id = id;
        this.username = username;
        this.imgRef = imgRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgRef() {
        return imgRef;
    }

    public void setImgRef(String imgRef) {
        this.imgRef = imgRef;
    }

    @Override
    public String toString() {
        return "UserInAList{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", imgRef='" + imgRef + '\'' +
                '}';
    }
}
