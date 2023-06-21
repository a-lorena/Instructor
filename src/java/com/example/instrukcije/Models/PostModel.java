package com.example.instrukcije.Models;

public class PostModel {
    private String postID, title, description, username, publisher, city, level;

    public PostModel() {
    }

    public PostModel(String postID, String postTitle, String postDescription, String username,
                     String publisher, String city, String level) {
        this.postID = postID;
        this.title = postTitle;
        this.description = postDescription;
        this.username = username;
        this.publisher = publisher;
        this.city = city;
        this.level = level;
    }

    public String getPostID() { return postID; }

    public void setPostID(String postID) { this.postID = postID; }

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

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPublisher() { return publisher; }

    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
