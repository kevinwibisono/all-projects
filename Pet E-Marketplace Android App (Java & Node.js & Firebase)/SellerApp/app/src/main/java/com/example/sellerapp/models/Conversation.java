package com.example.sellerapp.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Conversation {
    private String id_chatroom;
    private String email_penerima, email_pengirim;

    public Conversation(DocumentSnapshot doc) {
        this.id_chatroom = doc.getId();
        this.email_penerima = doc.getString("email_penerima");
        this.email_pengirim = doc.getString("email_pengirim");
    }

    public String getId_chatroom() {
        return id_chatroom;
    }

    public String getemail_penerima() {
        return email_penerima;
    }

    public void setemail_penerima(String email_penerima) {
        this.email_penerima = email_penerima;
    }

    public String getemail_pengirim() {
        return email_pengirim;
    }

    public void setemail_pengirim(String email_pengirim) {
        this.email_pengirim = email_pengirim;
    }
}
