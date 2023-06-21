package com.example.instrukcije.Models;

import java.util.List;

public class UserModel {
    String userID, name;
    List<String> chats;

    public UserModel() {
    }

    public UserModel(String userID, String name, List<String> chats) {
        this.userID = userID;
        this.name = name;
        this.chats = chats;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getChats() {
        return chats;
    }

    public void setChats(List<String> chats) {
        this.chats = chats;
    }
}
