package com.example.sellerapp.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CommentDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getAllCommments(String id_item){
        return firebaseDb.collection("komentar")
                .whereEqualTo("id_target", id_item)
                .get();
    }

    public Task<QuerySnapshot> getUnreadDiscussions(String id_produk){
        return firebaseDb.collection("komentar")
                .whereEqualTo("id_target", id_produk)
                .whereEqualTo("balasan_penjual", false)
                .get();
    }

    public Task<QuerySnapshot> getCommentsExceptMine(String id_item, String email){
        return firebaseDb.collection("komentar")
                .whereEqualTo("id_target", id_item)
                .whereNotEqualTo("email_pengomentar", email)
                .get();
    }

    public Task<QuerySnapshot> getCommentsFromMe(String id_item, String email){
        return firebaseDb.collection("komentar")
                .whereEqualTo("id_target", id_item)
                .whereEqualTo("email_pengomentar", email)
                .get();
    }

    public Task<QuerySnapshot> getCommentOfComment(String id_komentar){
        return firebaseDb.collection("komentar")
                .whereEqualTo("id_target", id_komentar)
                .orderBy("tanggal", Query.Direction.ASCENDING)
                .limit(1)
                .get();
    }

    public Task<DocumentSnapshot> getCommentById(String id_komentar){
        return firebaseDb.collection("komentar")
                .document(id_komentar)
                .get();
    }

    public Task<DocumentReference> addKomentar(String id_target, String email_pengomentar, String teks){
        firebaseDb.collection("komentar")
                .document(id_target).update("balasan_penjual", true);

        Map<String, Object> komentar = new HashMap<>();
        komentar.put("id_target", id_target);
        komentar.put("email_pengomentar", email_pengomentar);
        komentar.put("teks", teks);
        komentar.put("tanggal", Timestamp.now());

        return firebaseDb.collection("komentar")
                .add(komentar);
    }


}
