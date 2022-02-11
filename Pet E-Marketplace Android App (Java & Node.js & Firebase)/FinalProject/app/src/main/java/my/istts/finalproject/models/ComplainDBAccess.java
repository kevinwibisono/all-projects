package my.istts.finalproject.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ComplainDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getPJComplains(String id_pj){
        return firebaseDb.collection("komplain")
                .whereEqualTo("id_pj", id_pj)
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .get();
    }

    public DocumentReference getComplainById(String id_complain){
        return firebaseDb.collection("komplain")
                .document(id_complain);
    }

    public Task<DocumentReference> addNewComplain(String id_pj, String keluhan, String id_item, int jumlah, String[] bukti, String video){
        Map<String, Object> complainInput = new HashMap<>();
        complainInput.put("id_pj", id_pj);
        complainInput.put("keluhan", keluhan);
        complainInput.put("id_item", id_item);
        complainInput.put("jumlah_kembali", jumlah);
        complainInput.put("tanggal", Timestamp.now());
        complainInput.put("status", 0);
        complainInput.put("bukti_gambar", combineArrayIntoString(bukti));
        complainInput.put("link_video", video);

        return firebaseDb.collection("komplain")
                .add(complainInput);
    }

    public void deleteAllComplainOnPJ(String id_pj){
        firebaseDb.collection("komplain")
                .whereEqualTo("id_pj", id_pj)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot komplainDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    firebaseDb.collection("komplain")
                            .document(komplainDoc.getId())
                            .delete();
                }
            }
        });
    }

    private String combineArrayIntoString(String[] array){
        String combined = "";
        for (String arrItem:
             array) {
            if(arrItem != null){
                if(combined.equals("")){
                    combined += arrItem;
                }
                else{
                    combined += "|"+arrItem;
                }
            }
            else{
                combined += "|";
            }
        }
        return combined;
    }
}
