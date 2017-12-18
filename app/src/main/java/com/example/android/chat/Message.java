package com.example.android.chat;

import java.util.Date;

/**
 * Created by Me on 13.12.2017 г..
 */

public class Message {

    private String content;
    private String user;
    private long time;

    public Message(String content) {
        this.content = content;

        time = new Date().getTime();
    }

    public Message(){

    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public String getUser() {
        return user;
    }

    public long getTime() {
        return time;
    }
}
