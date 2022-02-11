package com.example.sellerapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class RiwayatSaldo {
    private String id_riwayat;
    private String bukti_transfer;
    private String no_rek;
    private String nama_rek;
    private int jenis_rek;
    private int jenis;
    private int jumlah;
    private String keterangan;
    private Timestamp tanggal;

    public RiwayatSaldo(DocumentSnapshot doc){
        id_riwayat = doc.getId();
        bukti_transfer = doc.getString("bukti_transfer");
        no_rek = doc.getString("no_rek");
        nama_rek = doc.getString("nama_rek");
        jenis_rek = doc.getLong("jenis_rek").intValue();
        keterangan = doc.getString("keterangan");
        jenis = doc.getLong("jenis").intValue();
        jumlah = doc.getLong("jumlah").intValue();
        tanggal = doc.getTimestamp("tanggal");
    }

    public String getStringDate(){
        Date date = this.tanggal.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        return date.getDate()+" "+months[date.getMonth()]+" "+(date.getYear()+1900)+
                " "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

    public String getId_riwayat() {
        return id_riwayat;
    }

    public String getBukti_transfer() {
        return bukti_transfer;
    }

    public String getNo_rek() {
        return no_rek;
    }

    public String getNama_rek() {
        return nama_rek;
    }

    public int getJenis_rek() {
        return jenis_rek;
    }

    public int getJenis() {
        return jenis;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }
}
