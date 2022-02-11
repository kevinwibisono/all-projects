package com.example.sellerapp.viewmodels.itemviewmodels;

import androidx.lifecycle.ViewModel;
import com.example.sellerapp.models.Promo;

public class PromoItemViewModel extends ViewModel {
    private Promo promo;

    public PromoItemViewModel(Promo promo) {
        this.promo = promo;
    }

    public String getTitle(){
        return promo.getJudul();
    }

    public String getId(){
        return promo.getId_promo();
    }

    public String getValidDate(){
        return promo.getValidString();
    }

    public String getMinimum(){
        return promo.getTSMin();
    }
}
