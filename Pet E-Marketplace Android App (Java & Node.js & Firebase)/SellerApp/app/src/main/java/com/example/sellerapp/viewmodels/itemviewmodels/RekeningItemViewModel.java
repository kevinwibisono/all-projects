package com.example.sellerapp.viewmodels.itemviewmodels;

import com.example.sellerapp.models.Rekening;

public class RekeningItemViewModel {
    private Rekening rek;

    public RekeningItemViewModel(Rekening rek) {
        this.rek = rek;
    }

    public String getNumber(){
        return rek.getNo_rek();
    }

    public String getNama(){
        return rek.getNama();
    }

    public int getJenis(){
        return rek.getJenis_rek();
    }

    public int getId(){
        return rek.getId();
    }

    public boolean isMain(){
        return rek.getDipilih();
    }
}
