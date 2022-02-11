package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class DetailPesanan {
    private String id_pj;
    private String no_resi;
    private String kurir;
    private String paket_kurir;
    private String alamat;
    private String koordinat;
    private String catatan;
    private int ongkir;
    private String metode_bayar;

    public DetailPesanan(DocumentSnapshot doc) {
        this.id_pj = doc.getString("id_pj");
        this.no_resi = doc.getString("no_resi");
        this.kurir = doc.getString("kurir");
        this.paket_kurir = doc.getString("paket_kurir");
        this.alamat = doc.getString("alamat");
        this.koordinat = doc.getString("koordinat");
        this.catatan = doc.getString("catatan");
        this.ongkir = doc.getLong("ongkir").intValue();
        this.metode_bayar = doc.getString("metode_bayar");
    }

    public String getId_pj() {
        return id_pj;
    }

    public String getKurir() {
        return kurir;
    }

    public String getPaket_kurir() {
        return paket_kurir;
    }

    public String getNo_resi() {
        return no_resi;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public String getCatatan() {
        return catatan;
    }

    public int getOngkir() {
        return ongkir;
    }

    public String getMetode_bayar() {
        return metode_bayar;
    }
}
