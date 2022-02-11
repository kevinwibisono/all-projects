package com.example.sellerapp.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Hotel {
    private String id_kamar, email_pemilik;
    private int panjang, lebar;
    private int harga, total, sedang_disewa;
    private int dilihat, tersewa, ulasan;
    private String nama, deskripsi, fasilitas;
    private String[] fasilitasArr;
    private String daftar_gambar, gambar;
    private boolean aktif;

    public Hotel(DocumentSnapshot doc) {
        this.id_kamar = doc.getId();
        this.email_pemilik = doc.getString("email_pemilik");
        this.panjang = doc.getLong("panjang").intValue();
        this.lebar = doc.getLong("lebar").intValue();
        this.harga = doc.getLong("harga").intValue();
        this.total = doc.getLong("total").intValue();
        this.sedang_disewa = doc.getLong("sedang_disewa").intValue();
        this.dilihat = doc.getLong("dilihat").intValue();
        this.tersewa = doc.getLong("tersewa").intValue();
        this.ulasan = doc.getLong("ulasan").intValue();
        this.nama = doc.getString("nama");
        this.deskripsi = doc.getString("deskripsi");
        this.fasilitas = doc.getString("fasilitas");
        this.fasilitasArr = doc.getString("fasilitas").split("\\|");
        this.daftar_gambar = doc.getString("daftar_gambar");
        this.gambar = this.daftar_gambar.split("\\|")[0];
        this.aktif = doc.getBoolean("aktif");
    }

    public String getId_kamar() {
        return id_kamar;
    }

    public int getPanjang() {
        return panjang;
    }

    public int getLebar() {
        return lebar;
    }

    public int getHarga() {
        return harga;
    }

    public int getUlasan() {
        return ulasan;
    }

    public String getTSHarga(){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(harga);
    }

    public int getTotal() {
        return total;
    }

    public int getSedang_disewa() {
        return sedang_disewa;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public String getDaftar_gambar() {
        return daftar_gambar;
    }

    public String getGambar() {
        return gambar;
    }

    public boolean isAktif() {
        return aktif;
    }

    public int getDilihat() {
        return dilihat;
    }

    public int getTersewa() {
        return tersewa;
    }

    public String[] getFasilitasArr() {
        return fasilitasArr;
    }

    public String getemail_pemilik() {
        return email_pemilik;
    }
}
