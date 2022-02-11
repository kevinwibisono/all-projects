package com.example.sellerapp.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Hotel;
import com.example.sellerapp.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;

public class HotelItemViewModel {
    private Hotel hotel;
    private MutableLiveData<String> hotelPic = new MutableLiveData<>();

    public HotelItemViewModel(Hotel hotel) {
        this.hotel = hotel;

        Storage storage = new Storage();
        storage.getPictureUrlFromName(hotel.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hotelPic.setValue(uri.toString());
            }
        });
    }

    public int getTotalKamar(){
        return hotel.getTotal();
    }

    public int getSedangDisewa(){
        return hotel.getSedang_disewa();
    }

    public String getNama(){
        return hotel.getNama();
    }

    public int getFasilitas(){
        return hotel.getFasilitasArr().length;
    }

    public String getHarga(){
        return hotel.getTSHarga();
    }

    public int getDilihat(){
        return hotel.getDilihat();
    }

    public int getTersewa(){
        return hotel.getTersewa();
    }

    public boolean isAktif(){
        return hotel.isAktif();
    }

    public String getId(){
        return hotel.getId_kamar();
    }

    public LiveData<String> getPicture(){
        return hotelPic;
    }
}
