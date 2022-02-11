package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Complain;
import com.example.sellerapp.models.ComplainDBAccess;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.ComplainItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class KomplainListViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private ComplainDBAccess complainDB;
    private AkunDBAccess akunDB;

    public KomplainListViewModel(Application app){
        orderDB = new PesananJanjitemuDBAccess();
        complainDB = new ComplainDBAccess();
        akunDB = new AkunDBAccess(app);
    }

    private MutableLiveData<ArrayList<ComplainItemViewModel>> pjComplains = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public void getSellerPJsComplains(){
        loading.setValue(true);
        pjComplains.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    orderDB.getComplainedOrders(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot complainedOrders) {
                            if(complainedOrders.getDocuments().size() > 0){
                                for (int i = 0; i < complainedOrders.getDocuments().size(); i++) {
                                    final int index = i;
                                    DocumentSnapshot pjDoc = complainedOrders.getDocuments().get(i);
                                    PesananJanjitemu pj = new PesananJanjitemu(pjDoc);

                                    complainDB.getWaitingComplains(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            ArrayList<Complain> complains = new ArrayList<>();
                                            for (DocumentSnapshot complainDoc:
                                                 queryDocumentSnapshots.getDocuments()) {
                                                complains.add(new Complain(complainDoc));
                                            }
                                            if(index == complainedOrders.getDocuments().size()-1){
                                                addComplainsToVM(complains, true);
                                            }
                                            else{
                                                addComplainsToVM(complains, false);
                                            }
                                        }
                                    });
                                }
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

    private void addComplainsToVM(ArrayList<Complain> complains, boolean last){
        ArrayList<ComplainItemViewModel> currentVMs = pjComplains.getValue();
        for (Complain complain:
             complains) {
            currentVMs.add(new ComplainItemViewModel(complain));
        }
        pjComplains.setValue(currentVMs);
        if(last){
            loading.setValue(false);
        }
    }

    public LiveData<ArrayList<ComplainItemViewModel>> getPjComplains() {
        return pjComplains;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }
}
