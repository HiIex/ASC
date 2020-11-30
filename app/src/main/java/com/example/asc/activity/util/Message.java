package com.example.asc.activity.util;

public class Message {
    private String phone;
    private String content;

    public Message(String phone,String content){
        this.phone=phone;
        this.content=content;
    }

    public String getPhone(){
        return this.phone;
    }

    public String getContent(){
        return this.content;
    }
}
