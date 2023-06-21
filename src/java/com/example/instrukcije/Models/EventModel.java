package com.example.instrukcije.Models;

public class EventModel {
    String eventID, subject, teacherID, teacherIme, studentReceiver, studentName, date, time;

    public EventModel() {
    }

    public EventModel(String eventID, String subject, String teacherID, String teacherIme,
                      String studentReceiver, String studentName, String date, String time) {
        this.eventID = eventID;
        this.subject = subject;
        this.teacherID = teacherID;
        this.teacherIme = teacherIme;
        this.studentReceiver = studentReceiver;
        this.studentName = studentName;
        this.date = date;
        this.time = time;
    }

    public String getEventID() { return eventID; }

    public void setEventID(String eventID) { this.eventID = eventID; }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherIme() {
        return teacherIme;
    }

    public void setTeacherIme(String teacherIme) {
        this.teacherIme = teacherIme;
    }

    public String getStudentReceiver() { return studentReceiver; }

    public void setStudentReceiver(String studentReceiver) { this.studentReceiver = studentReceiver; }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
