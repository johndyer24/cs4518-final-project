package com.example.android.theroom.models;

/**
 * Created by johndyer on 12/8/17.
 */

/**
 * Class that represents a Chat object
 */
public class Chat {

    private Long startTime; // The time the chat was created
    private String user1;
    private String user2;
    private String chatID; // key of the object in firebase

    // Empty constructor required by firebase
    public Chat() {}

    public Chat(Long startTime, String user1, String user2) {
        this.startTime = startTime;
        this.user1 = user1;
        this.user2 = user2;

    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}
