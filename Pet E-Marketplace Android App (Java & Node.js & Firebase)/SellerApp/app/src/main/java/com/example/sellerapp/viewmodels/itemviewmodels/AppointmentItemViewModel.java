package com.example.sellerapp.viewmodels.itemviewmodels;

import com.example.sellerapp.models.DetailJanjiTemu;

public class AppointmentItemViewModel {
    private DetailJanjiTemu janjiTemu;

    public AppointmentItemViewModel(DetailJanjiTemu janjiTemu) {
        this.janjiTemu = janjiTemu;
    }

    public String getIdPJ(){
        return janjiTemu.getId_pj();
    }

    public String getTglJanjiTemu(){
        return janjiTemu.getTglJanjitemu();
    }

    public String getOwnerName(){
        return janjiTemu.getNama_pemilik();
    }

    public String getKeluhan(){
        return janjiTemu.getKeluhan();
    }
}
