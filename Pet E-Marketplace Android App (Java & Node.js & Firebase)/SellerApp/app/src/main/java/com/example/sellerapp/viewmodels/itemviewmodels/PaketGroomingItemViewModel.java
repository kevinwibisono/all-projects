package com.example.sellerapp.viewmodels.itemviewmodels;

import com.example.sellerapp.models.PaketGrooming;

public class PaketGroomingItemViewModel {
    private PaketGrooming paketGrooming;

    public PaketGroomingItemViewModel(PaketGrooming paketGrooming) {
        this.paketGrooming = paketGrooming;
    }

    public void setPaketGrooming(PaketGrooming paketGrooming) {
        this.paketGrooming = paketGrooming;
    }

    public String getName(){
        return paketGrooming.getNama();
    }

    public String getId(){
        return paketGrooming.getId_paket();
    }

    public int getHarga(){
        return paketGrooming.getHarga();
    }
}
