package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.DetailJanjiTemu;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.AppointmentItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClinicHomeViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;

    public ClinicHomeViewModel(Application application) {
        orderDB = new PesananJanjitemuDBAccess();
        akunDB = new AkunDBAccess(application);
    }

    private MutableLiveData<String> sellerName = new MutableLiveData<>("");
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<AppointmentItemViewModel>> activeAppos = new MutableLiveData<>();

    public LiveData<String> getSellerName(){
        return sellerName;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<ArrayList<AppointmentItemViewModel>> getActiveAppos(){
        return activeAppos;
    }

    public void getClinicDetails(){
        loading.setValue(true);
        activeAppos.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    sellerName.setValue(accountsGot.get(0).getNama());
                    getClinicActiveAppos(accountsGot.get(0).getEmail());
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void getClinicActiveAppos(String no_hp){
        orderDB.getActiveAppos(no_hp).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot apposQuery) {
                if(apposQuery.getDocuments().size() > 0){
                    ArrayList<Integer> counter = new ArrayList<>();
                    for (DocumentSnapshot pjDoc:
                            apposQuery.getDocuments()) {
                        PesananJanjitemu pj = new PesananJanjitemu(pjDoc);

                        orderDB.getDetailJanjiTemu(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    DetailJanjiTemu detailJanjiTemu = new DetailJanjiTemu(queryDocumentSnapshots.getDocuments().get(0));
                                    addApposToVM(detailJanjiTemu);
                                }
                                counter.add(0);
                                if(counter.size() >= apposQuery.getDocuments().size()){
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

    private void addApposToVM(DetailJanjiTemu detailJanjiTemu){
        ArrayList<AppointmentItemViewModel> currentAppos = activeAppos.getValue();
        currentAppos.add(new AppointmentItemViewModel(detailJanjiTemu));
        activeAppos.setValue(currentAppos);
    }
}
