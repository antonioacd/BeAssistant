package com.example.beassistant.models;

public class YoutubeData {

    private String title = "";
    private String description = "";
    private String published = "";
    private String thumbnail = "";

    public YoutubeData(String title, String description, String published, String thumbnail) {
        this.title = title;
        this.description = description;
        this.published = published;
        this.thumbnail = thumbnail;
    }

    public YoutubeData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "YoutubeData{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", published='" + published + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
