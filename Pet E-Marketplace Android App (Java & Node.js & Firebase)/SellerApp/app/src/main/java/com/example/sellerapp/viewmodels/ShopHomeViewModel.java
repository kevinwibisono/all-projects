package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.ChatConvDBAccess;
import com.example.sellerapp.models.CommentDBAccess;
import com.example.sellerapp.models.ComplainDBAccess;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.ReviewDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopHomeViewModel extends AndroidViewModel {

    private AkunDBAccess akunDB;
    private PesananJanjitemuDBAccess orderDB;
    private ChatConvDBAccess chatDB;
    private ReviewDBAccess revDB;
    private ComplainDBAccess compDB;
    private ProductDBAccess prodDB;
    private CommentDBAccess discDB;

    public ShopHomeViewModel(@NonNull Application application) {
        super(application);
        akunDB = new AkunDBAccess(application);
        orderDB = new PesananJanjitemuDBAccess();
        chatDB = new ChatConvDBAccess();
        revDB = new ReviewDBAccess();
        compDB = new ComplainDBAccess();
        prodDB = new ProductDBAccess();
        discDB = new CommentDBAccess();
    }

    private MutableLiveData<Boolean[]> doneCounting = new MutableLiveData<>();
    private MutableLiveData<Boolean> productLoading = new MutableLiveData<>(true);
    private MutableLiveData<String> shopName = new MutableLiveData<>("");
    private MutableLiveData<Integer[]> importantThingsCount = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProductItemViewModel>> productVMs;

    public void arrangeImportantThings(){
        Boolean[] done = new Boolean[7];
        Integer[] count = new Integer[7];
        for (int i = 0; i < 7; i++) {
            done[i] = false;
            count[i] = 0;
        }
        doneCounting.setValue(done);
        importantThingsCount.setValue(count);
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    shopName.setValue(accountsGot.get(0).getNama());

                    orderDB.getOrdersHomePage(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Integer[] thingsCount = {0, 0, 0};
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot doc: results) {
                                thingsCount[doc.getLong("status").intValue()-1]++;
                            }
                            for (int i=0;i<3;i++){
                                //0-2
                                addImportantThing(i, thingsCount[i]);
                                setDoneCounting(i);
                            }
                        }
                    });

                    orderDB.getComplainedOrders(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> complainedOrders = queryDocumentSnapshots.getDocuments();
                            if(complainedOrders.size() <= 0){
                                setDoneCounting(3);
                            }
                            else{
                                for (int i = 0; i < complainedOrders.size(); i++) {
                                    final int index = i;
                                    compDB.getWaitingComplains(complainedOrders.get(i).getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                            addImportantThing(3, results.size());
                                            if(index == complainedOrders.size() - 1){
                                                setDoneCounting(3);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                    chatDB.getUnreadChats(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            addImportantThing(4, results.size());
                            setDoneCounting(4);
                        }
                    });

                    prodDB.getAllProducts(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> productResults = queryDocumentSnapshots.getDocuments();
                            if(productResults.size() <= 0){
                                productLoading.setValue(false);
                                setDoneCounting(5);
                                setDoneCounting(6);
                            }
                            else{
                                for (int i = 0; i < productResults.size(); i++) {
                                    final int index = i;
                                    //untuk hitung DISKUSI BELUM DIBACA
                                    discDB.getUnreadDiscussions(productResults.get(i).getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                            addImportantThing(5, results.size());
                                            if(index == productResults.size() - 1){
                                                setDoneCounting(5);
                                            }
                                        }
                                    });

                                    //untuk hitung ULASAN BELUM DIBALAS
                                    revDB.getUnreturnedReviews(productResults.get(i).getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                            addImportantThing(6, results.size());
                                            if(index == productResults.size() - 1){
                                                setDoneCounting(6);
                                            }
                                        }
                                    });

                                    //untuk hitung PRODUK BERMASALAH (stok/variasi habis, nonaktif)
                                    Product produk = new Product(productResults.get(i));
                                    if(produk.getStok() <= 0){
                                        addProductToLiveData(produk);
                                    }
                                    else if(!produk.isAktif()){
                                        addProductToLiveData(produk);
                                    }
                                    else{
                                        prodDB.getVariants(produk.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                                for (int j = 0; j < results.size(); j++) {
                                                    if(results.get(j).getLong("stok").intValue() <= 0){
                                                        addProductToLiveData(produk);
                                                        break;
                                                    }
                                                }
                                            }
                                        });
                                    }

                                    if(i == productResults.size() - 1){
                                        productLoading.setValue(false);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        akunDB.getSavedAccounts();
    }

    private void addImportantThing(int index, int value){
        Integer[] container = importantThingsCount.getValue();
        container[index] += value;
        importantThingsCount.setValue(container);
    }

    public LiveData<Boolean[]> isDoneCounting(){
        return doneCounting;
    }

    public LiveData<Boolean> isProductLoading(){
        return productLoading;
    }

    private void setDoneCounting(int index){
        Boolean[] done = doneCounting.getValue();
        done[index] = true;
        doneCounting.setValue(done);
    }

    public LiveData<String> getShopName(){
        return shopName;
    }

    public LiveData<Integer[]> getImportantThings(){
        if(importantThingsCount == null){
            importantThingsCount = new MutableLiveData<>();
            Integer[] initThingsCount = {0, 0, 0, 0, 0, 0, 0};
            importantThingsCount.setValue(initThingsCount);
        }

        return importantThingsCount;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getProductVMs(){
        if(productVMs == null){
            productVMs = new MutableLiveData<>(new ArrayList<>());
        }

        return productVMs;
    }

    private void addProductToLiveData(Product problemProduct){
        ArrayList<ProductItemViewModel> currentVMs = productVMs.getValue();
        currentVMs.add(new ProductItemViewModel(problemProduct));
        productVMs.setValue(currentVMs);
    }
}
