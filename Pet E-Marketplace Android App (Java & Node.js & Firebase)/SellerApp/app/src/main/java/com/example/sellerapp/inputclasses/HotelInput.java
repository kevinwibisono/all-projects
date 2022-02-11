package com.example.sellerapp.inputclasses;

import androidx.lifecycle.MutableLiveData;

public class HotelInput {
    public MutableLiveData<String> nama = new MutableLiveData<>("");
    public MutableLiveData<String> harga = new MutableLiveData<>("");
    public MutableLiveData<String> jumlah = new MutableLiveData<>("");
    public MutableLiveData<String> panjang = new MutableLiveData<>("");
    public MutableLiveData<String> lebar = new MutableLiveData<>("");
    public MutableLiveData<String> deskripsi = new MutableLiveData<>("");
    public MutableLiveData<boolean[]> fasilitas = new MutableLiveData<>(new boolean[9]);
    private boolean aktif;
    private String daftar_fasilitas;
    private String emailPemilik;
    private String[] pictureId = new String[4];

    public HotelInput() {
        aktif = true;
        daftar_fasilitas = "";
        boolean[] initFacs = new boolean[9];
        for (int i = 0; i < 9; i++) {
            if(i < 4){
                pictureId[i] = "";
            }
            initFacs[i] = false;
        }
        fasilitas.setValue(initFacs);
    }

    public int emptyField(){
        int index = -1;
        String[] inputs = {nama.getValue(), harga.getValue(), jumlah.getValue(), panjang.getValue(), lebar.getValue(), deskripsi.getValue()};
        for (int i=0;i<6;i++){
            if(inputs[i].equals("") && index == -1){
                index = i;
            }
        }
        return index;
    }

    public int zeroField(){
        int index = -1;
        int[] inputs = {1, Integer.parseInt(harga.getValue()), Integer.parseInt(jumlah.getValue()), Integer.parseInt(panjang.getValue()), Integer.parseInt(lebar.getValue()), 1};
        for (int i=0;i<6;i++){
            if(inputs[i] <= 0 && index == -1){
                index = i;
            }
        }
        return index;
    }

    public void setPictureId(int index, String url){
        pictureId[index] = url;
    }

    public String[] getPictureId(){
        return pictureId;
    }

    public String getemailPemilik() {
        return emailPemilik;
    }

    public void setemailPemilik(String emailPemilik) {
        this.emailPemilik = emailPemilik;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
}
