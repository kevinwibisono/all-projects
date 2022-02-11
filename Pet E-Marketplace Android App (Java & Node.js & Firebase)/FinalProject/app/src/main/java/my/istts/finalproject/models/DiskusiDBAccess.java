package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DiskusiDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getDiscussion(String sortField, Query.Direction sortDirection, String email, boolean penanya){
        Query queryTask = firebaseDb.collection("diskusi");

        if(penanya){
            queryTask = queryTask.whereEqualTo("email_penanya", email);
        }
        else{
            queryTask = queryTask.whereNotEqualTo("email_penanya", email)
                        .orderBy("email_penanya", Query.Direction.ASCENDING);
        }

        if(!sortField.equals("")){
            queryTask = queryTask.orderBy(sortField, sortDirection);
        }

        return queryTask
                .get();
    }

    public Task<DocumentSnapshot> getDiscussionById(String id_diskusi){
        return firebaseDb.collection("diskusi")
                .document(id_diskusi)
                .get();
    }

    public Task<DocumentReference> addDiscussion(String email, String gambar, String judul, String isi, int jenisHewan){
        Map<String, Object> discuss = new HashMap<>();
        discuss.put("email_penanya", email);
        discuss.put("topik_hewan", jenisHewan);
        discuss.put("gambar", gambar);
        discuss.put("judul", judul);
        discuss.put("isi", isi);
        discuss.put("like", 0);
        discuss.put("tanggal", Timestamp.now());

        return firebaseDb.collection("diskusi")
                .add(discuss);
    }

    public Task<Void> incDiscussLike(String id_diskusi, int jumlah){
        return firebaseDb.collection("diskusi")
                .document(id_diskusi)
                .update("like", FieldValue.increment(jumlah));
    }
}
