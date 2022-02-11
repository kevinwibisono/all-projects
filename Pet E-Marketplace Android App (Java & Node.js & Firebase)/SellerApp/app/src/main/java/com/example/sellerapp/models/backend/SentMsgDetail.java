package com.example.sellerapp.models.backend;

public class SentMsgDetail {
    private String Email;
    private String MessageID;
    private String MessageUUID;

    public SentMsgDetail(String email, String messageID, String messageUUID) {
        Email = email;
        MessageID = messageID;
        MessageUUID = messageUUID;
    }

    public String getEmail() {
        return Email;
    }

    public String getMessageID() {
        return MessageID;
    }

    public String getMessageUUID() {
        return MessageUUID;
    }
}
