package my.istts.finalproject.models;

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
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getCommentOfComment(String id_komentar){
        return firebaseDb.collection("komentar")
                .whereEqualTo("id_target", id_komentar)
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .limit(1)
                .get();
    }

    public Task<DocumentSnapshot> getCommentById(String id_komentar){
        return firebaseDb.collection("komentar")
                .document(id_komentar)
                .get();
    }

    public Task<DocumentReference> addKomentar(String id_target, String email_pengomentar, String teks){
        Map<String, Object> komentar = new HashMap<>();
        komentar.put("id_target", id_target);
        komentar.put("email_pengomentar", email_pengomentar);
        komentar.put("teks", teks);
        komentar.put("tanggal", Timestamp.now());
        komentar.put("balasan_penjual", false);

        return firebaseDb.collection("komentar")
                .add(komentar);
    }


}
