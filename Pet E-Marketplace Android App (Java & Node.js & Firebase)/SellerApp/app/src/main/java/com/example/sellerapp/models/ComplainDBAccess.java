package com.example.sellerapp.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ComplainDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getWaitingComplains(String id_pj){
        return firebaseDb.collection("komplain")
                .whereEqualTo("id_pj", id_pj)
                .whereEqualTo("status", 0)
                .orderBy("tanggal", Query.Direction.ASCENDING)
                .get();
    }

    public Task<QuerySnapshot> getPJComplains(String id_pj){
        return firebaseDb.collection("komplain")
                .whereEqualTo("id_pj", id_pj)
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .get();
    }

    public Task<DocumentSnapshot> getComplainById(String id_complain){
        return firebaseDb.collection("komplain")
                .document(id_complain)
                .get();
    }

    public Task<Void> changeComplainStatus(String id_komplain, int status){
        Map<String, Object> complainInput = new HashMap<>();
        complainInput.put("status", status);

        return firebaseDb.collection("komplain")
                .document(id_komplain)
                .set(complainInput, SetOptions.merge());
    }

}
