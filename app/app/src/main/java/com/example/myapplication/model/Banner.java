package com.example.myapplication.model;

import java.io.Serializable;

public class Banner implements Serializable {
    private String id;
    private String title;
    private String imageUrl;
    private String link;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}

