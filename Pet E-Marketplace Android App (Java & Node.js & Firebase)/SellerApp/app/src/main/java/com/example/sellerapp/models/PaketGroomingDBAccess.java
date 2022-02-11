package com.example.sellerapp.models;

import com.example.sellerapp.viewmodels.itemviewmodels.PaketGroomingItemViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaketGroomingDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getGroomingPackages(String email_penjual){
        return firebaseDb.collection("paket_grooming")
                .whereEqualTo("email_groomer", email_penjual)
                .get();
    };

    public Task<DocumentSnapshot> getGroomingPackageById(String id_paket){
        return firebaseDb.collection("paket_grooming")
                .document(id_paket)
                .get();
    };

    public void addGroomingPackages(ArrayList<PaketGroomingItemViewModel> paketVMs, String email){
        for (PaketGroomingItemViewModel paketVM:
             paketVMs) {
            Map<String, Object> paket = new HashMap<>();
            paket.put("email_groomer", email);
            paket.put("nama", paketVM.getName());
            paket.put("harga", paketVM.getHarga());

            firebaseDb.collection("paket_grooming").add(paket);
        }
    }

    public void updateGroomingPackages(ArrayList<PaketGroomingItemViewModel> paketVMs, String email){
        for (PaketGroomingItemViewModel paketVM:
                paketVMs) {
            Map<String, Object> paket = new HashMap<>();
            paket.put("email_groomer", email);
            paket.put("nama", paketVM.getName());
            paket.put("harga", paketVM.getHarga());
            if(paketVM.getId().equals("")){
                firebaseDb.collection("paket_grooming").add(paket);
            }
            else{
                firebaseDb.collection("paket_grooming")
                        .document(paketVM.getId())
                        .set(paket);
            }
        }
    }

    public void deleteGroomingPackages(String id_paket){
        firebaseDb.collection("paket_grooming")
                .document(id_paket)
                .delete();
    }
}
