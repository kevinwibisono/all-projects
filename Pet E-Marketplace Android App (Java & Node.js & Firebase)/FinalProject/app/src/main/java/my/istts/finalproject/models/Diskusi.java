package my.istts.finalproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Diskusi {
    private String id_diskusi;
    private String email_penanya;
    private String gambar;
    private String judul;
    private String isi;
    private int like;
    private int topik_hewan;
    private Timestamp tanggal;

    public Diskusi(DocumentSnapshot doc) {
        this.id_diskusi = doc.getId();
        this.email_penanya = doc.getString("email_penanya");
        this.gambar = doc.getString("gambar");
        this.judul = doc.getString("judul");
        this.isi = doc.getString("isi");
        this.like = doc.getLong("like").intValue();
        this.topik_hewan = doc.getLong("topik_hewan").intValue();
        this.tanggal = doc.getTimestamp("tanggal");
    }

    public String getTanggal() {
        Date date = this.tanggal.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        return date.getDate()+" "+months[date.getMonth()]+" "+(date.getYear()+1900)+
                " "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

    public String getId_diskusi() {
        return id_diskusi;
    }

    public String getemail_penanya() {
        return email_penanya;
    }

    public String getGambar() {
        return gambar;
    }

    public String getJudul() {
        return judul;
    }

    public String getIsi() {
        return isi;
    }

    public int getLike() {
        return like;
    }

    public int getTopik_hewan() {
        return topik_hewan;
    }
}
