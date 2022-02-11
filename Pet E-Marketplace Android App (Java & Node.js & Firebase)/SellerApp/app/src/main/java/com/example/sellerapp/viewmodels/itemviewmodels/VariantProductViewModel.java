package com.example.sellerapp.viewmodels.itemviewmodels;

import android.graphics.Bitmap;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class VariantProductViewModel {
    private String nama;
    private String harga;
    private String stok;
    private Bitmap gambar;
    private String idProduk;
    private String idVariant;

    public VariantProductViewModel(String nama, String harga, String stok, Bitmap gambar) {
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.gambar = gambar;
    }

    public VariantProductViewModel(String nama, String harga, String stok, Bitmap gambar, String idVariant) {
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.gambar = gambar;
        this.idVariant = idVariant;
    }

    public String getNama() {
        return nama;
    }

    public String getHarga() {
        return harga;
    }

    public String getStok() {
        return stok;
    }

    public void setGambar(Bitmap gambar){
        this.gambar = gambar;
    }

    public Bitmap getGambar(){
        return gambar;
    }

    public String getIdProduk() {
        return idProduk;
    }

    public String getIdVariant() {
        return idVariant;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public void setIdVariant(String idVariant) {
        this.idVariant = idVariant;
    }

    public String getTSHarga(){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(Integer.parseInt(harga));
    }

}
