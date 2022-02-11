package com.example.sellerapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Chat {
    private String teks;
    private String gambar;
    private String id_pj, id_item, id_chat, id_chatroom;
    private Product produk;
    private String email_penerima, email_pengirim;
    private String tanggal, jam;
    private boolean showDate;
    private Timestamp time;

    public Chat(DocumentSnapshot doc) {
        this.id_chat = doc.getId();
        this.teks = doc.getString("teks");
        this.gambar = doc.getString("gambar");
        this.id_chatroom = doc.getString("id_obrolan");
        this.id_item = doc.getString("id_item");
        this.id_pj = doc.getString("id_pj");
        this.email_penerima = doc.getString("email_penerima");
        this.email_pengirim = doc.getString("email_pengirim");
        this.time = doc.getTimestamp("waktu");
        Date chatDate = doc.getTimestamp("waktu").toDate();
        setStringDate(chatDate);
    }

    public Chat(String teks, String gambar, String id_pj, String id_item, String email_penerima) {
        this.teks = teks;
        this.gambar = gambar;
        this.id_pj = id_pj;
        this.id_item = id_item;
        this.email_penerima = email_penerima;
    }

    private void setStringDate(Date chatDate){
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        this.tanggal = chatDate.getDate()+" "+months[chatDate.getMonth()]+" "+(chatDate.getYear()+1900);

        //jika sama dengan tanggal hari ini, maka  tulis sebagai "Hari Ini", tdk usah dalam tanggal
        Date today = new Date();
        String todayString = today.getDate()+" "+months[today.getMonth()]+" "+(today.getYear()+1900);
        if(this.tanggal.equals(todayString)){
            this.tanggal = "Hari Ini";
        }

        this.jam = addZero(chatDate.getHours())+":"+addZero(chatDate.getMinutes());
    }

    private String addZero(int hourMin){
        String hourMinStr = String.valueOf(hourMin);
        if(hourMinStr.length() < 2){
            hourMinStr = "0"+hourMin;
        }
        return hourMinStr;
    }

    public String getTeks() {
        return teks;
    }

    public void setTeks(String teks) {
        this.teks = teks;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getId_pj() {
        return id_pj;
    }

    public void setId_pj(String id_pj) {
        this.id_pj = id_pj;
    }

    public String getId_item() {
        return id_item;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }

    public Product getProduk() {
        return produk;
    }

    public void setProduk(Product produk) {
        this.produk = produk;
    }

    public String getemail_penerima() {
        return email_penerima;
    }

    public void setemail_penerima(String email_penerima) {
        this.email_penerima = email_penerima;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public String getId_chat() {
        return id_chat;
    }

    public String getId_chatroom() {
        return id_chatroom;
    }

    public String getemail_pengirim() {
        return email_pengirim;
    }

    public Timestamp getTime() {
        return time;
    }
}
