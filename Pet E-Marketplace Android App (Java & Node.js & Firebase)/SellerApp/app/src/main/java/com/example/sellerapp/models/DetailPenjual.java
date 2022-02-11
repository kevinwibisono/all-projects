package com.example.sellerapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentSnapshot;

@Entity(tableName = "detail_penjual")
public class DetailPenjual {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "role")
    private int role;

    @ColumnInfo(name = "deskripsi")
    private String deskripsi;

    @ColumnInfo(name = "poster")
    private String poster;

    @ColumnInfo(name = "kurir")
    private String kurir;

    @ColumnInfo(name = "janji_temu")
    private String janji_temu;

    @ColumnInfo(name = "jam_buka")
    private String jam_buka;

    @ColumnInfo(name = "jam_tutup")
    private String jam_tutup;

    @ColumnInfo(name = "alamat")
    private String alamat;

    @ColumnInfo(name = "koordinat")
    private String koordinat;

    public DetailPenjual(DocumentSnapshot dataFromDB){
        this.role = dataFromDB.getLong("tipe").intValue();
        this.poster = dataFromDB.getString("poster");
        this.deskripsi = dataFromDB.getString("deskripsi");
        this.kurir = dataFromDB.getString("kurir");
        this.janji_temu = dataFromDB.getString("janji_temu");
        this.jam_buka = dataFromDB.getString("jam_buka");
        this.jam_tutup = dataFromDB.getString("jam_tutup");
        this.alamat = dataFromDB.getString("alamat");
        this.koordinat = dataFromDB.getString("koordinatAlamat");
    }

    public DetailPenjual(int role, String deskripsi, String kurir, String janji_temu, String jam_buka, String jam_tutup, String alamat, String koordinat, String poster) {
        this.role = role;
        this.deskripsi = deskripsi;
        this.poster = poster;
        this.kurir = kurir;
        this.janji_temu = janji_temu;
        this.jam_buka = jam_buka;
        this.jam_tutup = jam_tutup;
        this.alamat = alamat;
        this.koordinat = koordinat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getKurir() {
        return kurir;
    }

    public void setKurir(String kurir) {
        this.kurir = kurir;
    }

    public String getJanji_temu() {
        return janji_temu;
    }

    public void setJanji_temu(String janji_temu) {
        this.janji_temu = janji_temu;
    }

    public String getJam_buka() {
        return jam_buka;
    }

    public void setJam_buka(String jam_buka) {
        this.jam_buka = jam_buka;
    }

    public String getJam_tutup() {
        return jam_tutup;
    }

    public void setJam_tutup(String jam_tutup) {
        this.jam_tutup = jam_tutup;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

}
