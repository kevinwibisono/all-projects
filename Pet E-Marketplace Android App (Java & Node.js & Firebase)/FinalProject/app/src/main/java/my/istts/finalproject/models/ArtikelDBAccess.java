package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ArtikelDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getArticles(String sortField, Query.Direction sortDirection, int kategori, int target_pembaca){
        Query queryTask = firebaseDb.collection("artikel");

        if(kategori > -1){
            queryTask = queryTask.whereEqualTo("kategori", kategori);
        }

        if(target_pembaca > -1){
            queryTask = queryTask.whereEqualTo("target_pembaca", target_pembaca);
        }

        if(!sortField.equals("")){
            queryTask = queryTask.orderBy(sortField, sortDirection);
        }

        return queryTask
                .get();
    }

    public Task<QuerySnapshot> getLimitedArticles(){
        return firebaseDb.collection("artikel")
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .limit(5)
                .get();
    }

    public Task<DocumentSnapshot> getArticleById(String id_artikel){
        return firebaseDb.collection("artikel")
                .document(id_artikel)
                .get();
    }

    public Task<Void> incArticleLike(String id_artikel, int jumlah){
        return firebaseDb.collection("artikel")
                .document(id_artikel)
                .update("like", FieldValue.increment(jumlah));
    }
}
