package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class ItemPesananJanjitemu {
    private String id;
    private String id_pj;
    private String id_item;
    private String nama;
    private String gambar;
    private String id_variasi, variasi;
    private int harga;
    private int jumlah;
    private int total_berat;

    public ItemPesananJanjitemu(DocumentSnapshot doc) {
        this.id = doc.getId();
        this.id_pj = doc.getString("id_pj");
        this.id_item = doc.getString("id_item");
        this.nama = doc.getString("nama");
        this.gambar = doc.getString("gambar");
        this.id_variasi = doc.getString("id_variasi");
        this.variasi = doc.getString("variasi");
        this.harga = doc.getLong("harga").intValue();
        this.jumlah = doc.getLong("jumlah").intValue();
        this.total_berat = doc.getLong("total_berat").intValue();
    }

    public String getId_pj() {
        return id_pj;
    }

    public String getId_item() {
        return id_item;
    }

    public String getNama() {
        return nama;
    }

    public String getGambar() {
        return gambar;
    }

    public String getId_variasi() {
        return id_variasi;
    }

    public String getVariasi() {
        return variasi;
    }

    public int getHarga() {
        return harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public int getTotal_berat() {
        return total_berat;
    }

    public String getId() {
        return id;
    }
}
