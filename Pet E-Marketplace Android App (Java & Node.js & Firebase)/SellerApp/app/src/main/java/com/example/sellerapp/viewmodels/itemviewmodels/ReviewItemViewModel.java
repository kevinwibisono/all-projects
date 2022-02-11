package com.example.sellerapp.viewmodels.itemviewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.Review;
import com.example.sellerapp.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ReviewItemViewModel extends ViewModel {
    private Review review;

    public ReviewItemViewModel(Review review, Application app) {
        this.review = review;
        AkunDBAccess akunDB = new AkunDBAccess(app);
        ProductDBAccess productDB = new ProductDBAccess();
        HotelDBAccess hotelDB = new HotelDBAccess();
        Storage storage = new Storage();

        storage.getPictureUrlFromName(review.getemail_pemberi()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                personPic.setValue(uri.toString());
            }
        });

        akunDB.getAccByEmail(review.getemail_pemberi()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    personName.setValue(documentSnapshot.getString("nama"));
                }
            }
        });

        //terdapat produk/hotel yang dibicarakan
        productDB.getProductById(review.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if(doc.getData() != null){
                    //ditemukan product dengan id tsb, yang direview adalah produk
                    itemName.setValue(doc.getString("nama"));
                }
                else{
                    hotelDB.getRoomById(review.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            //ditemukan kamar dengan id tsb, yang direview adalah kamar pet hotel
                            if(documentSnapshot.getData() != null){
                                itemName.setValue(documentSnapshot.getString("nama"));
                            }
                            else{
                                //yang direview adalah groomer/klinik itu sendiri
                                akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
                                    @Override
                                    public void onComplete(List<Akun> accountsGot) {
                                        if(accountsGot.size() > 0){
                                            itemName.setValue(accountsGot.get(0).getNama());
                                        }
                                    }
                                });

                                akunDB.getSavedAccounts();
                            }
                        }
                    });
                }
            }
        });
    }

    private MutableLiveData<String> personName = new MutableLiveData<>();
    private MutableLiveData<String> personPic = new MutableLiveData<>();
    private MutableLiveData<String> itemName = new MutableLiveData<>();

    public String getText(){
        return review.getUlasan();
    }

    public String getId(){
        return review.getId_review();
    }

    public int getSkor(){
        return review.getNilai();
    }

    public LiveData<String> getPersonName(){
        return personName;
    }

    public LiveData<String> getPicture(){
        return personPic;
    }

    public LiveData<String> getItemName(){
        return itemName;
    }

    public String getBalasan(){
        return review.getBalasan_penjual();
    }
}
