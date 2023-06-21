package com.example.instrukcije.Models;


import com.google.firebase.Timestamp;

import java.sql.Time;

public class MessageModel {
    String message, messageID, ownerID, ownerName, chatID;
    Timestamp date;
    boolean isGroup;

    public MessageModel() {
    }

    public MessageModel(String message, String messageID, String ownerName, String ownerID, String chatID, Timestamp date, boolean isGroup) {
        this.message = message;
        this.messageID = messageID;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.chatID = chatID;
        this.date = date;
        this.isGroup = isGroup;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }
}
