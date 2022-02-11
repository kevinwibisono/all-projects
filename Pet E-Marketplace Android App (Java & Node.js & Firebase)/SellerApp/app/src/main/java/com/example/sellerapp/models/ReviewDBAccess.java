package com.example.sellerapp.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ReviewDBAccess {

    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getUnreturnedReviews(String id_produk){
        return firebaseDb.collection("ulasan")
                .whereEqualTo("id_item", id_produk)
                .whereEqualTo("balasan_penjual", "")
                .get();
    }

    public Task<QuerySnapshot> getReturnedReviews(String id_produk){
        return firebaseDb.collection("ulasan")
                .whereEqualTo("id_item", id_produk)
                .whereNotEqualTo("balasan_penjual", "")
                .get();
    }

    public Task<DocumentSnapshot> getReviewById(String id_ulasan){
        return firebaseDb.collection("ulasan")
                .document(id_ulasan)
                .get();
    }

    public Task<QuerySnapshot> getItemReviews(String id_item){
        return firebaseDb.collection("ulasan")
                .whereEqualTo("id_item", id_item)
                .get();
    }

    public Task<QuerySnapshot> getItemReviewsFiltered(String id_item, int skor, int limit){
        Query query = firebaseDb.collection("ulasan")
                .whereEqualTo("id_item", id_item);
        if(skor > 0){
            query = query.whereEqualTo("nilai", skor);
        }
        if(limit > 0){
            query = query.limit(limit);
        }
        return query
                .get();
    }

    public Task<Void> returnReview(String id, String balasan){
        Map<String, Object> review = new HashMap<>();
        review.put("balasan_penjual", balasan);

        return firebaseDb.collection("ulasan")
                .document(id)
                .set(review, SetOptions.merge());
    }
}
