package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class DetailPenjualDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public DetailPenjualDBAccess(){}

    public Task<DocumentSnapshot> getDBAkunDetail(String email){
        return firebaseDb.collection("detail_penjual")
                .document(email)
                .get();
    };

    public Task<QuerySnapshot> getAllWithRole(int role){
        return firebaseDb.collection("detail_penjual")
                .whereEqualTo("tipe", role)
                .get();
    };
}
