package my.istts.finalproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Artikel {
    private String id_artikel;
    private String judul;
    private String nama_penulis;
    private String link;
    private String gambar;
    private Timestamp tanggal;
    private int like;
    private String topik_hewan;
    private int kategori;
    private int target_pembaca;
    private String isi;

    public Artikel(DocumentSnapshot doc){
        this.id_artikel = doc.getId();
        this.judul = doc.getString("judul");
        this.nama_penulis = doc.getString("nama_penulis");
        this.link = doc.getString("link");
        this.gambar = doc.getString("gambar");
        this.topik_hewan = doc.getString("topik_hewan");
        this.isi = doc.getString("isi");
        this.tanggal = doc.getTimestamp("tanggal");
        this.like = doc.getLong("like").intValue();
        this.kategori = doc.getLong("kategori").intValue();
        this.target_pembaca = doc.getLong("target_pembaca").intValue();
    }

    public String getStringDate(){
        Date date = this.tanggal.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        return date.getDate()+" "+months[date.getMonth()]+" "+(date.getYear()+1900)+
                " "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

    public String getId_artikel() {
        return id_artikel;
    }

    public String getJudul() {
        return judul;
    }

    public String getNama_penulis() {
        return nama_penulis;
    }

    public String getLink() {
        return link;
    }

    public String getGambar() {
        return gambar;
    }

    public int getLike() {
        return like;
    }

    public String getTopik_hewan() {
        return topik_hewan;
    }

    public String[] getTopikHewanList(){
        return topik_hewan.split("\\|");
    }

    public int getKategori() {
        return kategori;
    }

    public int getTarget_pembaca() {
        return target_pembaca;
    }

    public String getIsi() {
        return isi;
    }
}
