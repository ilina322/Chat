package com.example.android.chat;

import java.util.Date;

/**
 * Created by Me on 13.12.2017 г..
 */

public class Message {

    private String content;
    private String username;
    private String imagePath;
    private long time;


    public Message(String content, String username) {
        this.content = content;
        this.username = username;

        time = new Date().getTime();
    }

    public Message(){

    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public long getTime() {
        return time;
    }
}
