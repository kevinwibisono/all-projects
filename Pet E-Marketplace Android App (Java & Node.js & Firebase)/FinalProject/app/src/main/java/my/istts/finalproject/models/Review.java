package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Review {
    private int nilai;
    private String email_pemberi, ulasan, balasan_penjual;
    private String id_item;
    private String id_review;

    public Review(DocumentSnapshot doc) {
        id_review = doc.getId();
        id_item = doc.getString("id_item");
        nilai = doc.getLong("nilai").intValue();
        email_pemberi = doc.getString("email_pemberi");
        ulasan = doc.getString("ulasan");
        balasan_penjual = doc.getString("balasan_penjual");
    }

    public String getId_review() {
        return id_review;
    }

    public String getId_item() {
        return id_item;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }

    public int getNilai() {
        return nilai;
    }

    public void setNilai(int nilai) {
        this.nilai = nilai;
    }

    public String getemail_pemberi() {
        return email_pemberi;
    }

    public void setemail_pemberi(String email_pemberi) {
        this.email_pemberi = email_pemberi;
    }

    public String getUlasan() {
        return ulasan;
    }

    public void setUlasan(String ulasan) {
        this.ulasan = ulasan;
    }

    public String getBalasan_penjual() {
        return balasan_penjual;
    }

    public void setBalasan_penjual(String balasan_penjual) {
        this.balasan_penjual = balasan_penjual;
    }
}
