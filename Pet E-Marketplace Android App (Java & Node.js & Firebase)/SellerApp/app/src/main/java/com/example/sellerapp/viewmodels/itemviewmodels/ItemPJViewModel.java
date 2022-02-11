package com.example.sellerapp.viewmodels.itemviewmodels;

import com.example.sellerapp.models.ItemPesananJanjitemu;

public class ItemPJViewModel {
    private ItemPesananJanjitemu itemPJ;
    private int tipe;

    public ItemPJViewModel(ItemPesananJanjitemu itemPJ, int tipe) {
        this.itemPJ = itemPJ;
        this.tipe = tipe;
    }

    public String getPicture(){
        return itemPJ.getGambar();
    }

    public String getName(){
        return itemPJ.getNama();
    }

    public String getVariant(){
        return itemPJ.getVariasi();
    }

    public int getPrice(){
        return itemPJ.getHarga();
    }

    public int getWeight(){
        return itemPJ.getTotal_berat();
    }

    public int getQty(){
        return itemPJ.getJumlah();
    }

    public int getTipe(){
        return tipe;
    }

    public String getId(){
        return itemPJ.getId_item();
    }

}
