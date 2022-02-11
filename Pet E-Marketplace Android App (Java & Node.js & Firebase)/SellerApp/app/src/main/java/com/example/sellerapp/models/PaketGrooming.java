package com.example.sellerapp.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class PaketGrooming {
    private String nama;
    private String id_paket;
    private int harga;

    public PaketGrooming(DocumentSnapshot doc){
        this.nama = doc.getString("nama");
        this.id_paket = doc.getId();
        this.harga = doc.getLong("harga").intValue();
    }

    public PaketGrooming(String nama, int harga, String id_paket) {
        this.nama = nama;
        this.id_paket = id_paket;
        this.harga = harga;
    }

    public String getNama() {
        return nama;
    }

    public String getId_paket() {
        return id_paket;
    }

    public int getHarga() {
        return harga;
    }
}
