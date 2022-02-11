package com.example.sellerapp.viewmodels.itemviewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Complain;
import com.example.sellerapp.models.ItemPesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class ComplainItemViewModel {
    private Complain complain;

    public ComplainItemViewModel(Complain c){
        this.complain = c;

        String[] id_item = c.getComplainedItems();

        PesananJanjitemuDBAccess orderDB = new PesananJanjitemuDBAccess();
        orderDB.getItemPJById(id_item[0]).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(documentSnapshot);
                    picture.setValue(itemPJ.getGambar());
                }
            }
        });
    }

    private MutableLiveData<String> picture = new MutableLiveData<>();

    public LiveData<String> getPicture() {
        return picture;
    }

    public int getStatus(){
        return complain.getStatus();
    }

    public String getId(){
        return complain.getId_complain();
    }

    public String getKeluhan(){
        return complain.getKeluhan();
    }

    public int getJumlah(){
        return complain.getJumlah_kembali();
    }

    public int getItemsQty(){
        return complain.getComplainedItems().length;
    }

    public String getIdPJ(){
        return complain.getId_pj();
    }
}
