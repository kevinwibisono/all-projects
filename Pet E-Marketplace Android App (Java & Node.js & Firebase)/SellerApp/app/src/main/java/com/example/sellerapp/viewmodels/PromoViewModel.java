package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Promo;
import com.example.sellerapp.models.PromoDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.PromoItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class PromoViewModel {
    private PromoDBAccess promoDB;
    private AkunDBAccess akunDB;

    public PromoViewModel(Application application) {
        akunDB = new AkunDBAccess(application);
        promoDB = new PromoDBAccess();
    }

    public MutableLiveData<String> judul = new MutableLiveData<>("");
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<PromoItemViewModel>> promosVM = new MutableLiveData<>();

    public LiveData<ArrayList<PromoItemViewModel>> getPromoVMs(){
        return promosVM;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public void getShopPromos(){
        loading.setValue(true);
        promosVM.setValue(new ArrayList<>());
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    promoDB.getShopPromos(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            if(results.size() > 0){
                                ArrayList<Promo> promos = new ArrayList<>();
                                for (DocumentSnapshot doc:
                                        results) {
                                    if(doc.getString("judul").contains(judul.getValue())){
                                        promos.add(new Promo(doc));
                                    }
                                }
                                addNewPromoVMs(promos);
                            }
                            else{
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void addNewPromoVMs(ArrayList<Promo> promos){
        ArrayList<PromoItemViewModel> currentVMs = promosVM.getValue();
        for (Promo v:
                promos) {
            currentVMs.add(new PromoItemViewModel(v));
        }
        promosVM.setValue(currentVMs);
        loading.setValue(false);
    }
}
