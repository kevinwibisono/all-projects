package com.example.sellerapp.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Hotel;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class DiscussListItemViewModel {
    private int jumlahDiskusi;
    private int tipe;
    private String id_item;

    public DiscussListItemViewModel(int jumlahDiskusi, String id_item, int tipe){
        this.jumlahDiskusi = jumlahDiskusi;
        this.tipe = tipe;
        this.id_item = id_item;

        Storage storage = new Storage();
        if(tipe == 0){
            ProductDBAccess productDB = new ProductDBAccess();
            productDB.getProductById(id_item).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        Product product = new Product(documentSnapshot);
                        itemName.setValue(product.getNama());

                        storage.getPictureUrlFromName(product.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                itemPic.setValue(uri.toString());
                            }
                        });
                    }
                }
            });
        }
        else{
            HotelDBAccess hotelDB = new HotelDBAccess();
            hotelDB.getRoomById(id_item).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        Hotel hotel = new Hotel(documentSnapshot);
                        itemName.setValue(hotel.getNama());

                        storage.getPictureUrlFromName(hotel.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                itemPic.setValue(uri.toString());
                            }
                        });
                    }
                }
            });
        }

    }


    private MutableLiveData<String> itemName = new MutableLiveData<>("");
    private MutableLiveData<String> itemPic = new MutableLiveData<>("");

    public LiveData<String> getItemPic() {
        return itemPic;
    }

    public LiveData<String> getItemName() {
        return itemName;
    }

    public int getJumlahDiskusi() {
        return jumlahDiskusi;
    }

    public int getTipe() {
        return tipe;
    }

    public String getId_item() {
        return id_item;
    }
}
