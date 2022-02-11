package my.istts.finalproject.viewmodels.itemviewmodels;

import my.istts.finalproject.models.Promo;
import my.istts.finalproject.inputclasses.PJInput;

import java.util.ArrayList;

public class PromoItemViewModel {
    private Promo promo;
    private boolean valid;

    public PromoItemViewModel(Promo promo, PJInput pj, ArrayList<ProductCheckoutItemVM> checkoutItemVMS) {
        this.promo = promo;

        boolean productsFound = false;
        if(promo.getId_produk().equals("")){
            productsFound = true;
        }
        else{
            //dari produk-produk yang dibawa checkout, apakah ada yang termasuk di jajaran produk yang sedang promo
            for (ProductCheckoutItemVM checkout:
                    checkoutItemVMS) {
                //jika chekout adalah dari seller yang sama dan termasuk dalam produk yang dipromokan
                //maka ditemukan produk yang dapat diberlakukan promo
                if(checkout.getSeller().equals(promo.getemail_penjual()) && promo.getId_produk().contains(checkout.getIdProduct())){
                    productsFound = true;
                }
            }
        }

        if(productsFound && pj.getSubtotal().getValue() >= promo.getMinimum_pembelian()){
            valid = true;
        }
        else{
            valid = false;
        }
    }

    public PromoItemViewModel(Promo promo){
        this.promo = promo;
        valid = false;
    }

    public String getPromoId(){
        return promo.getId_promo();
    }

    public String getPromoName(){
        return promo.getJudul();
    }

    public String getMin(){
        return promo.getTSMin();
    }

    public int getMax(){
        return promo.getMaximum_diskon();
    }

    public int getPercent(){
        return promo.getPersentase();
    }

    public String getIdProduk(){
        return promo.getId_produk();
    }

    public String getValidHingga(){
        return promo.getValidString();
    }

    public boolean isValid(){
        return valid;
    }

}
