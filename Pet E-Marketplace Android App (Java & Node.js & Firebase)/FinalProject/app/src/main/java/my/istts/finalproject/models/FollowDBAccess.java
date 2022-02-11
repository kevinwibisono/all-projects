package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FollowDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getFollowers(String email_penjual){
        return firebaseDb.collection("pengikut")
                .whereEqualTo("email_penjual", email_penjual)
                .get();
    };

    //tipe  0->shop  1->groomer  2->hotel  3->clinic
    public Task<QuerySnapshot> getMyFollowing(String email, int tipe){
        return firebaseDb.collection("pengikut")
                .whereEqualTo("email_pembeli", email)
                .whereEqualTo("tipe", tipe)
                .get();
    };

    public Task<QuerySnapshot> getIsFollowing(String email_pembeli, String email_penjual){
        return firebaseDb.collection("pengikut")
                .whereEqualTo("email_pembeli", email_pembeli)
                .whereEqualTo("email_penjual", email_penjual)
                .get();
    };

    public Task<DocumentReference> followSeller(String email_pembeli, String email_penjual, int tipe){
        Map<String, Object> favorit = new HashMap<>();
        favorit.put("email_pembeli", email_pembeli);
        favorit.put("email_penjual", email_penjual);
        favorit.put("tipe", tipe);

        return firebaseDb.collection("pengikut").add(favorit);
    };

    public Task<Void> unfollowSeller(String id_follow){
        return firebaseDb.collection("pengikut")
                .document(id_follow)
                .delete();
    };
}
