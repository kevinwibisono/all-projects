package com.example.sellerapp.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class PromoDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<DocumentSnapshot> getPromoDetail(String id){
        return firebaseDb.collection("promo")
                .document(id)
                .get();
    }

    public Task<QuerySnapshot> getShopPromos(String email){
        return firebaseDb.collection("promo")
                .whereEqualTo("email_penjual", email)
                .get();
    }

    public Task<DocumentReference> addPromo(Promo addedPromo, String email){
        Map<String, Object> promo = new HashMap<>();
        promo.put("email_penjual", email);
        promo.put("judul", addedPromo.getJudul());
        promo.put("id_produk", addedPromo.getId_produk());
        promo.put("persentase", addedPromo.getPersentase());
        promo.put("maximum_diskon", addedPromo.getMaximum_diskon());
        promo.put("minimum_pembelian", addedPromo.getMinimum_pembelian());
        promo.put("valid_hingga", addedPromo.getValid_hingga());
        promo.put("valid_mulai", Timestamp.now());

        return firebaseDb.collection("promo").add(promo);
    }

    public Task<Void> updatePromo(Promo addedPromo, String id){
        Map<String, Object> promo = new HashMap<>();
        promo.put("judul", addedPromo.getJudul());
        promo.put("id_produk", addedPromo.getId_produk());
        promo.put("persentase", addedPromo.getPersentase());
        promo.put("maximum_diskon", addedPromo.getMaximum_diskon());
        promo.put("minimum_pembelian", addedPromo.getMinimum_pembelian());
        promo.put("valid_hingga", addedPromo.getValid_hingga());

        return firebaseDb.collection("promo")
                .document(id)
                .set(promo, SetOptions.merge());
    }
}
