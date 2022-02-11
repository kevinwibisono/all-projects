package com.example.sellerapp.models;

public class OptionPengiriman {
    private int maxDurasi; //untuk perhitungan selesai otomatis

    private String durasi;

    private String judul;

    private int idPerusahaan; //0 -> JNE, 1 -> POS, 2 -> TIKI

    public OptionPengiriman(String durasi, String judul, int idPerusahaan) {
        this.durasi = durasi;
        this.judul = judul;
        this.idPerusahaan = idPerusahaan;
    }

    public int getMaxDurasi() {
        return maxDurasi;
    }

    public void setMaxDurasi(int maxDurasi) {
        this.maxDurasi = maxDurasi;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public int getIdPerusahaan() {
        return idPerusahaan;
    }

    public void setIdPerusahaan(int idPerusahaan) {
        this.idPerusahaan = idPerusahaan;
    }
}
