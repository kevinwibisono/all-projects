package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Rekening;
import com.example.sellerapp.models.RekeningDBAccess;

import java.util.List;

public class AddRekeningViewModel {
    private RekeningDBAccess rekeningDB;
    private AkunDBAccess akunDB;

    public AddRekeningViewModel(Application app){
        rekeningDB = new RekeningDBAccess(app);
        akunDB = new AkunDBAccess(app);
    }

    private int jenis = 0;
    public MutableLiveData<String> atasNamaRek = new MutableLiveData<>("");
    public MutableLiveData<String> noRek = new MutableLiveData<>("");
    private MutableLiveData<String[]> errors = new MutableLiveData<>();

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> doneAdd = new MutableLiveData<>();

    public LiveData<String[]> getErrors(){
        return errors;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<Boolean> isDoneAdd(){
        return doneAdd;
    }

    public void setJenis(int jenis) {
        this.jenis = jenis;
    }

    public void checkAdd(){
        if(atasNamaRek.getValue().equals("")){
            errors.setValue(new String[]{"", "Nama Tidak Dapat Kosong"});
        }
        else if(noRek.getValue().equals("")){
            errors.setValue(new String[]{"No Rekening Tidak Dapat Kosong", ""});
        }
        else{
            errors.setValue(new String[]{"", ""});
            addRekening();
        }
    }

    private void addRekening(){
        loading.setValue(true);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    Rekening rekening = new Rekening(jenis, atasNamaRek.getValue(), noRek.getValue(), accountsGot.get(0).getEmail(), false);
                    rekeningDB.setAddRekeningListener(new RekeningDBAccess.rekeningAddedCallback() {
                        @Override
                        public void onRekeningAdded() {
                            loading.setValue(false);
                            doneAdd.setValue(true);
                        }
                    });

                    rekeningDB.insertRekening(rekening);
                }
            }
        });

        akunDB.getSavedAccounts();
    }

}
