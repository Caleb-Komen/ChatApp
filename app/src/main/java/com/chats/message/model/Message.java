package com.chats.message.model;

public class Message {

    private String senderId;
    private String receiverId;
    private String textMessage;
    private String photoUrl;

    public Message() {
    }

    public Message(String senderId, String receiverId, String textMessage, String photoUrl) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.textMessage = textMessage;
        this.photoUrl = photoUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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
