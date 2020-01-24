package com.chats.message.model;

public class Message {

    String sender;
    String textMessage;
    String photoUrl;

    public Message() {
    }

    public Message(String sender, String textMessage, String photoUrl) {
        this.sender = sender;
        this.textMessage = textMessage;
        this.photoUrl = photoUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
