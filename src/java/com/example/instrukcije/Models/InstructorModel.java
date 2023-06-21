package com.example.instrukcije.Models;

import java.util.List;

public class InstructorModel {
    private String id, fullName, education, subjects;
    private long price, comments, stars;
    double rating;
    private boolean available;
    List<String> tags, levels;

    public InstructorModel() {
    }

    public InstructorModel(String id, String fullName, String education, String subjects,
                           long price, double rating, boolean available, List<String> tags, List<String> levels) {
        this.id = id;
        this.fullName = fullName;
        this.education = education;
        this.subjects = subjects;
        this.price = price;
        this.rating = rating;
        this.available = available;
        this.tags = tags;
        this.levels = levels;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public long getPrice() { return price; }

    public void setPrice(long price) { this.price = price; }

    public long getComments() { return comments; }

    public void setComments(long comments) { this.comments = comments; }

    public long getStars() { return stars; }

    public void setStars(long stars) { this.stars = stars; }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) { this.rating = rating; }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }
}
