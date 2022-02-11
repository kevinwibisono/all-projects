package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.CommentDBAccess;
import com.example.sellerapp.models.DetailPenjual;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.example.sellerapp.models.Hotel;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.DiscussListItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DiscussListViewModel {
    private CommentDBAccess diskusiDB;
    private DetailPenjualDBAccess detailDB;
    private AkunDBAccess akunDB;

    public DiscussListViewModel(Application app){
        diskusiDB = new CommentDBAccess();
        detailDB = new DetailPenjualDBAccess(app);
        akunDB = new AkunDBAccess(app);
    }

    private MutableLiveData<ArrayList<DiscussListItemViewModel>> discuss = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LiveData<ArrayList<DiscussListItemViewModel>> getDiscuss() {
        return discuss;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public void searchDiscussList(){
        loading.setValue(true);
        discuss.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    detailDB.setGetDetailCompleteListener(new DetailPenjualDBAccess.onCompleteGetDetailListener() {
                        @Override
                        public void onDetailComplete(List<DetailPenjual> detailsGot) {
                            if(detailsGot.size() > 0){
                                int tipe = detailsGot.get(0).getRole();

                                if(tipe == 0){
                                    ProductDBAccess productDB = new ProductDBAccess();
                                    productDB.getAllProducts(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot productQuerySnapshot) {
                                            ArrayList<Integer> counter = new ArrayList<>();
                                            for (DocumentSnapshot productDoc:
                                                 productQuerySnapshot.getDocuments()) {
                                                Product product = new Product(productDoc);

                                                diskusiDB.getUnreadDiscussions(product.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        if(queryDocumentSnapshots.getDocuments().size() > 0){
                                                            addDiscussCommentsToVM(queryDocumentSnapshots.getDocuments(), product.getId_produk(), tipe);
                                                        }
                                                        counter.add(0);
                                                        if(counter.size() >= productQuerySnapshot.getDocuments().size()){
                                                            loading.setValue(false);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else{
                                    HotelDBAccess hotelDB = new HotelDBAccess();
                                    hotelDB.getHotelRooms(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot hotelQuerySnapshot) {
                                            ArrayList<Integer> counter = new ArrayList<>();
                                            for (DocumentSnapshot productDoc:
                                                    hotelQuerySnapshot.getDocuments()) {
                                                Hotel hotel = new Hotel(productDoc);

                                                diskusiDB.getUnreadDiscussions(hotel.getId_kamar()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        if(queryDocumentSnapshots.getDocuments().size() > 0){
                                                            addDiscussCommentsToVM(queryDocumentSnapshots.getDocuments(), hotel.getId_kamar(), tipe);
                                                        }
                                                        counter.add(0);
                                                        if(counter.size() >= hotelQuerySnapshot.getDocuments().size()){
                                                            loading.setValue(false);
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });
                                }

                            }
                        }
                    });

                    detailDB.getLocalDetail();
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void addDiscussCommentsToVM(List<DocumentSnapshot> discusses, String id_item, int jenis){
        ArrayList<DiscussListItemViewModel> currentDiscuss = discuss.getValue();
        currentDiscuss.add(new DiscussListItemViewModel(discusses.size(), id_item, jenis));
        discuss.setValue(currentDiscuss);
    }

}
