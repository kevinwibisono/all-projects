package com.example.sellerapp.viewmodels.itemviewmodels;

import com.example.sellerapp.models.RiwayatSaldo;

public class RiwayatItemViewModel {
    private RiwayatSaldo riwayat;

    public RiwayatItemViewModel(RiwayatSaldo riwayat) {
        this.riwayat = riwayat;
    }

    public String getNoRek(){
        return riwayat.getNo_rek();
    }

    public String gambar_bukti(){
        return riwayat.getBukti_transfer();
    }

    public String getNamaRek(){
        return riwayat.getNama_rek();
    }

    public Integer getJenisRek(){
        return riwayat.getJenis_rek();
    }

    public int getJumlah(){
        return riwayat.getJumlah();
    }

    public int getJenis(){
        return riwayat.getJenis();
    }

    public String getTanggal(){
        return riwayat.getStringDate();
    }

    public String getKeterangan(){
        return riwayat.getKeterangan();
    }
}
