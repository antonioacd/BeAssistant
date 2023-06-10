package com.example.beassistant.models;

public class YoutubeData {

    private String videoId = "";
    private String title = "";
    private String description = "";
    private String published = "";
    private String thumbnail = "";

    public YoutubeData(String videoId, String title, String description, String published, String thumbnail) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.published = published;
        this.thumbnail = thumbnail;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
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
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", published='" + published + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
