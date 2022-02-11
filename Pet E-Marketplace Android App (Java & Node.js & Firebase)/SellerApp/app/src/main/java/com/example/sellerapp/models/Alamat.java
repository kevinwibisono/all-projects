package com.example.sellerapp.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Alamat {
    private String id;
    private String nama;
    private String no_hp;
    private String alamat_lengkap;
    private String kelurahan;
    private String kecamatan;
    private String kodepos;
    private String kota;
    private String provinsi;
    private String catatan;
    private String koordinat;

    public Alamat(DocumentSnapshot doc){
        this.id = doc.getId();
        this.nama = doc.getString("nama");
        this.no_hp = doc.getString("no_hp");
        this.alamat_lengkap = doc.getString("alamat_lengkap");
        this.kelurahan = doc.getString("kelurahan");
        this.kecamatan = doc.getString("kecamatan");
        this.kota = doc.getString("kota");
        this.provinsi = doc.getString("provinsi");
        this.kodepos = doc.getString("kodepos");
        this.catatan = doc.getString("catatan");
        this.koordinat = doc.getString("koordinat");
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public String getAlamat_lengkap() {
        return alamat_lengkap;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public String getKodepos() {
        return kodepos;
    }

    public String getKota() {
        return kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getKoordinat() {
        return koordinat;
    }

    @Override
    public String toString() {
        return nama+"\n"+
                no_hp+"\n"+
                alamat_lengkap+"\n"+
                kelurahan+", "+kecamatan+", "+kota+" "+kodepos+"\n"+
                catatan;
    }
}
