package com.example.sellerapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class DetailJanjiTemu {
    private String id_detail;
    private String id_pj;
    private Timestamp tgl_janjitemu;
    private String jenis_janjitemu;
    private String alamat;
    private String koordinat;
    private String nama_pemilik;
    private String hp_pemilik;
    private String daftar_nama;
    private String daftar_usia;
    private String daftar_jenis;
    private String keluhan;

    public DetailJanjiTemu(DocumentSnapshot doc) {
        id_detail = doc.getId();
        id_pj = doc.getString("id_pj");
        tgl_janjitemu = doc.getTimestamp("tgl_janjitemu");
        jenis_janjitemu = doc.getString("jenis_janjitemu");
        alamat = doc.getString("alamat");
        koordinat = doc.getString("koordinat");
        nama_pemilik = doc.getString("nama_pemilik");
        hp_pemilik = doc.getString("hp_pemilik");
        daftar_nama = doc.getString("daftar_nama");
        daftar_usia = doc.getString("daftar_usia");
        daftar_jenis = doc.getString("daftar_jenis");
        keluhan = doc.getString("keluhan");
    }

    public String getId_detail() {
        return id_detail;
    }

    public String getId_pj() {
        return id_pj;
    }

    public String getJenis_janjitemu() {
        return jenis_janjitemu;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public String[] getDaftar_nama() {
        return daftar_nama.split("\\|");
    }

    public String[] getDaftar_usia() {
        return daftar_usia.split("\\|");
    }

    public String[] getDaftar_jenis() {
        return daftar_jenis.split("\\|");
    }

    public String getKeluhan() {
        return keluhan;
    }

    public String getNama_pemilik() {
        return nama_pemilik;
    }

    public String getHp_pemilik() {
        return hp_pemilik;
    }

    public String getTglJanjitemu(){
        Date endDate = this.tgl_janjitemu.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        String tanggal = endDate.getDate()+" "+months[endDate.getMonth()]+" "+(endDate.getYear()+1900);
        String waktu = addZero(endDate.getHours())+":"+addZero(endDate.getMinutes());
        return tanggal+" "+waktu;
    }

    private String addZero(int hourMin){
        String hourMinStr = String.valueOf(hourMin);
        if(hourMinStr.length() < 2){
            hourMinStr = "0"+hourMin;
        }
        return hourMinStr;
    }

    public Timestamp getTgl_janjitemu() {
        return tgl_janjitemu;
    }
}
