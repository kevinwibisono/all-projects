package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Product {
    private int kategori, stok, berat;
    private int dilihat, terjual;
    private int harga, ulasan;
    private String id_produk, nama, deskripsi, email_penjual;
    private String gambar, daftar_gambar;
    private boolean aktif;

    public Product(DocumentSnapshot doc){
        this.id_produk = doc.getId();
        this.nama = doc.getString("nama");
        this.kategori = doc.getLong("kategori").intValue();
        this.harga = doc.getLong("harga").intValue();
        this.ulasan = doc.getLong("ulasan").intValue();
        this.stok = doc.getLong("stok").intValue();
        this.berat = doc.getLong("berat").intValue();
        this.deskripsi = doc.getString("deskripsi");
        this.email_penjual = doc.getString("email_penjual");
        this.daftar_gambar = doc.getString("daftar_gambar");
        String[] picsUrl = this.daftar_gambar.split("\\|", -1);
        this.gambar = picsUrl[0];
        this.aktif = doc.getBoolean("aktif");
        this.dilihat = doc.getLong("dilihat").intValue();
        this.terjual = doc.getLong("terjual").intValue();
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public String getId_produk() {
        return id_produk;
    }

    public void setId_produk(String id_produk) {
        this.id_produk = id_produk;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getKategori() {
        return kategori;
    }

    public int getUlasan() {
        return ulasan;
    }

    public String getKategoriString(){
        String[] kategoriList = {"Makanan Anjing", "Makanan Kucing", "Makanan Kelinci", "Makanan Burung", "Makanan Ikan", "Makanan Hamster", "Makanan Reptil", "Pakan Ternak", "Peralatan Grooming", "Leash dan Handler", "Treats/Snack (Kudapan)", "Peralatan Kesehatan", "Mainan/Alat Ketangkasan", "Peralatan Kebersihan", "Kandang/Tempat Tidur"};
        return kategoriList[kategori];
    }

    public void setKategori(int kategori) {
        this.kategori = kategori;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getBerat() {
        return berat;
    }

    public void setBerat(int berat) {
        this.berat = berat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getDaftar_gambar() {
        return daftar_gambar;
    }

    public void setDaftar_gambar(String daftar_gambar) {
        this.daftar_gambar = daftar_gambar;
    }

    public int getDilihat() {
        return dilihat;
    }

    public void setDilihat(int dilihat) {
        this.dilihat = dilihat;
    }

    public int getTerjual() {
        return terjual;
    }

    public void setTerjual(int terjual) {
        this.terjual = terjual;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public boolean isAktif() {
        return aktif;
    }

    public String getemail_penjual() {
        return email_penjual;
    }

    public void setemail_penjual(String email_penjual) {
        this.email_penjual = email_penjual;
    }

    public String getTSHarga(){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(harga);
    }
}
