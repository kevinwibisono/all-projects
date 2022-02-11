package com.example.sellerapp.viewmodels.itemviewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Alamat;
import com.example.sellerapp.models.AlamatDBAccess;
import com.example.sellerapp.models.DetailGrooming;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class GroomingItemViewModel {
    private DetailGrooming detailGrooming;
    private PesananJanjitemu pj;

    public GroomingItemViewModel(DetailGrooming detailGrooming, PesananJanjitemu pj){
        this.detailGrooming = detailGrooming;
        this.pj = pj;

        AlamatDBAccess alamatDB = new AlamatDBAccess();
        alamatDB.getAddressById(detailGrooming.getId_alamat()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Alamat alamat = new Alamat(documentSnapshot);
                alamatLive.setValue(alamat.getAlamat_lengkap());
            }
        });

        PesananJanjitemuDBAccess orderDB = new PesananJanjitemuDBAccess();
        orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                jumItem.setValue(queryDocumentSnapshots.getDocuments().size());
            }
        });
    }

    public String getTglBooking(){
        return detailGrooming.getTglBooking();
    }

    public String getIdPJ(){
        return pj.getId_pj();
    }

    public int getTotal(){
        return pj.getTotal();
    }

    public MutableLiveData<Integer> jumItem = new MutableLiveData<>();
    public MutableLiveData<String> alamatLive = new MutableLiveData<>("");

    public LiveData<String> getAlamat(){
        return alamatLive;
    }

    public LiveData<Integer> getJumItem(){
        return jumItem;
    }
}
