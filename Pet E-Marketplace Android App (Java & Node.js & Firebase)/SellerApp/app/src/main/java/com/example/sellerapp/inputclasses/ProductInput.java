package com.example.sellerapp.inputclasses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Product;

public class ProductInput {
    public MutableLiveData<String> nama = new MutableLiveData<>("");
    public MutableLiveData<String> harga = new MutableLiveData<>("");
    public MutableLiveData<String> berat = new MutableLiveData<>("");
    public MutableLiveData<String> stok = new MutableLiveData<>("");
    public MutableLiveData<String> deskripsi = new MutableLiveData<>("");
    private boolean aktif;
    private MutableLiveData<Integer> kategori = new MutableLiveData<>(0);
    private String emailPenjual;
    private String[] pictureUrl = new String[4];

    public ProductInput() {
        aktif = true;
        for (int i = 0; i < 4; i++) {
            pictureUrl[i] = "";
        }
    }

    public ProductInput(Product p){
        nama.setValue(p.getNama());
        harga.setValue(p.getTSHarga());
        deskripsi.setValue(p.getDeskripsi());
        berat.setValue(String.valueOf(p.getBerat()));
        stok.setValue(String.valueOf(p.getStok()));
    }

    public int emptyField(){
        int index = -1;
        String[] inputs = {nama.getValue(), String.valueOf(kategori), harga.getValue(), berat.getValue(), stok.getValue(), deskripsi.getValue()};
        for (int i=0;i<6;i++){
            if(inputs[i].equals("") && index == -1){
                index = i;
            }
        }
        return index;
    }

    public int zeroField(){
        int index = -1;
        int[] inputs = {1, 1, Integer.parseInt(harga.getValue()), Integer.parseInt(berat.getValue()), Integer.parseInt(stok.getValue()), 1};
        for (int i=0;i<6;i++){
            if(inputs[i] <= 0 && index == -1){
                index = i;
            }
        }
        return index;
    }

    public void setPictureUrl(int index, String url){
        pictureUrl[index] = url;
    }

    public String[] getPictureUrl(){
        return pictureUrl;
    }

    public String getemailPenjual() {
        return emailPenjual;
    }

    public void setemailPenjual(String emailPenjual) {
        this.emailPenjual = emailPenjual;
    }

    public void setKategori(int kategori) {
        this.kategori.setValue(kategori);
    }

    public LiveData<Integer> getKategori() {
        return kategori;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
}
