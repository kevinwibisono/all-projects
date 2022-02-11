package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.RiwayatDBAccess;
import com.example.sellerapp.models.RiwayatSaldo;
import com.example.sellerapp.viewmodels.itemviewmodels.RiwayatItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SaldoViewModel extends AndroidViewModel {
    private AkunDBAccess akunDB;
    private RiwayatDBAccess historyDB;

    public SaldoViewModel(@NonNull Application application) {
        super(application);
        akunDB = new AkunDBAccess(application);
        historyDB = new RiwayatDBAccess(application);
    }

    private String email;
    private MutableLiveData<ArrayList<RiwayatItemViewModel>> historiesVMs;
    private MutableLiveData<Boolean> riwayatLoading = new MutableLiveData<>();
    private MutableLiveData<Integer> saldo = new MutableLiveData<>();

    public LiveData<Integer> getSaldo(){
        return saldo;
    }

    public LiveData<Boolean> isLoading(){
        return riwayatLoading;
    }

    public LiveData<ArrayList<RiwayatItemViewModel>> getHistoriesVMs(){
        if(historiesVMs == null){
            historiesVMs = new MutableLiveData<>();
        }
        return historiesVMs;
    }

    public void getSellerSaldo(){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    getSaldoHistories(-1);

                    akunDB.getAccByEmail(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                saldo.setValue(documentSnapshot.getLong("saldo").intValue());
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void getSaldoHistories(int jenis){
        riwayatLoading.setValue(true);
        historiesVMs.setValue(new ArrayList<>());

        historyDB.getHistories(jenis, email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<RiwayatSaldo> riwayat = new ArrayList<>();
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        riwayat.add(new RiwayatSaldo(doc));
                    }
                    addHistoriesToVM(riwayat);
                }
                else{
                    riwayatLoading.setValue(false);
                }
            }
        });
    }

    private void addHistoriesToVM(ArrayList<RiwayatSaldo> histories){
        ArrayList<RiwayatItemViewModel> currentVMs = historiesVMs.getValue();
        for (RiwayatSaldo history:
             histories) {
            currentVMs.add(new RiwayatItemViewModel(history));
        }
        historiesVMs.setValue(currentVMs);
        riwayatLoading.setValue(false);
    }

}
