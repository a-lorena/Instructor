package com.example.instrukcije.Models;

import com.google.firebase.Timestamp;

public class RatingModel {
    String studentFullName, comment;
    double stars;
    Timestamp date;

    public RatingModel() {
    }

    public RatingModel(String studentFullName, String comment, double stars, Timestamp date) {
        this.studentFullName = studentFullName;
        this.comment = comment;
        this.stars = stars;
        this.date = date;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
