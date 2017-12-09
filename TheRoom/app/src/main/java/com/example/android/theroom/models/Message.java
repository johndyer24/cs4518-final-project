package com.example.android.theroom.models;

/**
 * Created by johndyer on 12/9/17.
 */

/**
 * Class that represents a Message object
 */
public class Message {

    private String userID;
    private String text;
    private Long time;

    // Empty constructor required by firebase
    public Message() {}

    public Message(String userID, String text, Long time) {
        this.userID = userID;
        this.text = text;
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
