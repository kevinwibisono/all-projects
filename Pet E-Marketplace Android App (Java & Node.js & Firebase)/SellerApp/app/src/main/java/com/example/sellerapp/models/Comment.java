package com.example.sellerapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Comment {
    private String id_komentar;
    private String id_target;
    private String email_pengomentar;
    private String teks;
    private Timestamp tanggal;
    private boolean balasan_penjual;

    public Comment(DocumentSnapshot doc){
        this.id_komentar = doc.getId();
        this.id_target = doc.getString("id_target");
        this.email_pengomentar = doc.getString("email_pengomentar");
        this.teks = doc.getString("teks");
        this.tanggal = doc.getTimestamp("tanggal");
        this.balasan_penjual = doc.getBoolean("balasan_penjual");
    }

    public String getId_komentar() {
        return id_komentar;
    }

    public String getId_target() {
        return id_target;
    }

    public String getemail_pengomentar() {
        return email_pengomentar;
    }

    public String getTeks() {
        return teks;
    }

    public String getTanggal() {
        Date date = this.tanggal.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        return date.getDate()+" "+months[date.getMonth()]+" "+(date.getYear()+1900)+
                " "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

    public boolean isReplied() {
        return balasan_penjual;
    }
}
