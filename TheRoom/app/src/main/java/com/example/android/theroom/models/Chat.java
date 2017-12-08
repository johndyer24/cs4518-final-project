package com.example.android.theroom.models;

/**
 * Created by johndyer on 12/8/17.
 */

/**
 * Class that represents a Chat object
 */
public class Chat {

    private Long startTime; // The time the chat was created

    // Empty constructor required by firebase
    public Chat() {}

    public Chat(Long startTime) {
        this.startTime = startTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
