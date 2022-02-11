package com.example.sellerapp.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Komplain {
    private String id_komplain;
    private String id_pj;
    private int status;
    private int jenis_keluhan;
    private String detail_keluhan;
    private String daftar_gambar;
    private String video_bukti;
    private int jumlah_pengembalian;

    public Komplain(DocumentSnapshot doc){
        id_komplain = doc.getId();
        id_pj = doc.getString("id_pj");
        detail_keluhan = doc.getString("detail_keluhan");
        daftar_gambar = doc.getString("daftar_gambar");
        video_bukti = doc.getString("video_bukti");
        status = doc.getLong("status").intValue();
        jenis_keluhan = doc.getLong("jenis_keluhan").intValue();
        jumlah_pengembalian = doc.getLong("jumlah_pengembalian").intValue();
    }

    public String[] getGambarBukti(){
        return daftar_gambar.split("\\|", -1);
    }

    public String getStatusStr(){
        String[] statuses = {"Menunggu Tanggapan Penjual", "Diterima", "Ditolak"};
        return statuses[status];
    }

    public String getJenisKeluhanStr(){
        String[] jenis = {"Pesanan Belum Sampai", "Produk Tidak Sesuai", "Produk Cacat/Rusak", "Jumlah Produk Kurang"};
        return jenis[jenis_keluhan];
    }

    public String getId_komplain() {
        return id_komplain;
    }

    public String getId_pj() {
        return id_pj;
    }

    public String getDetail_keluhan() {
        return detail_keluhan;
    }

    public String getVideo_bukti() {
        return video_bukti;
    }

    public int getJumlah_pengembalian() {
        return jumlah_pengembalian;
    }
}
