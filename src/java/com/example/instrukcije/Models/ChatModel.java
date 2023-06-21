package com.example.instrukcije.Models;

public class ChatModel {
    String studentID, studentName, teacherID, teacherName, groupName, chatID;
    boolean isGroup;

    public ChatModel() {
    }

    public ChatModel(String studentID, String studentName, String teacherID, String teacherName,
                     boolean isGroup, String groupName, String chatID) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.teacherID = teacherID;
        this.teacherName = teacherName;
        this.isGroup = isGroup;
        this.groupName = groupName;
        this.chatID = chatID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}
