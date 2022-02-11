package com.example.sellerapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.PaketGrooming;
import com.example.sellerapp.models.PaketGroomingDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class AddGroomingPackViewModel {
    private PaketGroomingDBAccess groomingDB;

    public AddGroomingPackViewModel() {
        this.groomingDB = new PaketGroomingDBAccess();
    }

    public MutableLiveData<String> packName = new MutableLiveData<>("");
    public MutableLiveData<String> packPrice = new MutableLiveData<>("");

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);

    private MutableLiveData<String[]> errors = new MutableLiveData<>();

    public LiveData<String[]> getErrors(){
        return errors;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<Boolean> isUpdating(){
        return updating;
    }

    public boolean groomingPackageValid(){
        if(packName.getValue().equals("")){
            errors.setValue(new String[]{"Nama Tidak Boleh Kosong", ""});
            return false;
        }
        else if(packPrice.getValue().equals("")){
            errors.setValue(new String[]{"", "Harga Tidak Boleh Kosong"});
            return false;
        }
        else{
            errors.setValue(new String[]{"", ""});
            return true;
        }
    }

    public void getPackageDetails(String id_pack){
        loading.setValue(true);
        updating.setValue(true);

        groomingDB.getGroomingPackageById(id_pack).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    PaketGrooming paketGrooming = new PaketGrooming(documentSnapshot);
                    packName.setValue(paketGrooming.getNama());
                    packPrice.setValue(String.valueOf(paketGrooming.getHarga()));
                    loading.setValue(false);
                }
            }
        });
    }

}
