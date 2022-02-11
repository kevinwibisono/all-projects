package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.DetailGrooming;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.GroomingItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroomingHomeViewModel {

    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;

    public GroomingHomeViewModel(Application application) {
        orderDB = new PesananJanjitemuDBAccess();
        akunDB = new AkunDBAccess(application);
    }

    private MutableLiveData<String> sellerName = new MutableLiveData<>("");
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<GroomingItemViewModel>> activeGroomings = new MutableLiveData<>();

    public LiveData<String> getSellerName(){
        return sellerName;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<ArrayList<GroomingItemViewModel>> getActiveGroomings(){
        return activeGroomings;
    }

    public void getGroomerDetails(){
        loading.setValue(true);
        activeGroomings.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    sellerName.setValue(accountsGot.get(0).getNama());
                    getGroomerActiveGroomings(accountsGot.get(0).getEmail());

                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void getGroomerActiveGroomings(String email){
        orderDB.getActiveGrooming(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Integer> counter = new ArrayList<>();
                    for (DocumentSnapshot pjDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        PesananJanjitemu pj = new PesananJanjitemu(pjDoc);

                        orderDB.getDetailGrooming(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    DetailGrooming detailGrooming = new DetailGrooming(queryDocumentSnapshots.getDocuments().get(0));
                                    if(pj.getStatus() > 0){
                                        addGroomingsToVM(detailGrooming, pj);
                                    }
                                }
                                counter.add(0);
                                if(counter.size() >= queryDocumentSnapshots.getDocuments().size()){
                                    loading.setValue(false);
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

    private void addGroomingsToVM(DetailGrooming detailGrooming, PesananJanjitemu pj){
        ArrayList<GroomingItemViewModel> currentGroomings = activeGroomings.getValue();
        currentGroomings.add(new GroomingItemViewModel(detailGrooming, pj));
        activeGroomings.setValue(currentGroomings);
    }
}
