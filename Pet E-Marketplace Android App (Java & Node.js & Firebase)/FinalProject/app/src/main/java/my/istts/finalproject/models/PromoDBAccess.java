package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
                .whereGreaterThan("valid_hingga", Timestamp.now())
                .get();
    }
}
