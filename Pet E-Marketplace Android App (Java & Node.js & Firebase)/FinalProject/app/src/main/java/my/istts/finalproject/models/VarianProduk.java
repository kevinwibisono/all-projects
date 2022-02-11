package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class VarianProduk {
    private String id_varian;
    private String gambar;
    private String nama;
    private int harga;
    private int stok;

    public VarianProduk(DocumentSnapshot doc){
        this.id_varian = doc.getId();
        this.gambar = doc.getString("gambar");
        this.nama = doc.getString("nama");
        this.harga = doc.getLong("harga").intValue();
        this.stok = doc.getLong("stok").intValue();
    }

    public String getId_varian() {
        return id_varian;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public String getTSHarga(){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(harga);
    }
}
