package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class PaketGrooming {
    private String nama;
    private String id_paket;
    private String email_groomer;
    private int harga;

    public PaketGrooming(DocumentSnapshot doc){
        this.nama = doc.getString("nama");
        this.id_paket = doc.getId();
        this.harga = doc.getLong("harga").intValue();
        this.email_groomer = doc.getString("email_groomer");
    }

    public String getNama() {
        return nama;
    }

    public String getId_paket() {
        return id_paket;
    }

    public String getemail_groomer() {
        return email_groomer;
    }

    public int getHarga() {
        return harga;
    }
}
