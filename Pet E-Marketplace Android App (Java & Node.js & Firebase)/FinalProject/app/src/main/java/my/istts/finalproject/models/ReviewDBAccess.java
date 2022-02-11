package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ReviewDBAccess {

    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<DocumentSnapshot> getReviewById(String id_rev){
        return firebaseDb.collection("ulasan")
                .document(id_rev)
                .get();
    }

    public Task<QuerySnapshot> getAllReviewsOfItem(String id_item){
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

    public Task<DocumentReference> addReview(int nilai, String reviewStr, String id_item, String email_pemberi){
        Map<String, Object> review = new HashMap<>();
        review.put("nilai", nilai);
        review.put("email_pemberi", email_pemberi);
        review.put("ulasan", reviewStr);
        review.put("id_item", id_item);
        review.put("balasan_penjual", "");

        return firebaseDb.collection("ulasan")
                .add(review);

    }
}
