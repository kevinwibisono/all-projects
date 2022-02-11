package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PaketGroomingDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getPackages(String email_groomer){
        return firebaseDb.collection("paket_grooming")
                .whereEqualTo("email_groomer", email_groomer)
                .get();
    };

    public Task<DocumentSnapshot> getPackageById(String id_paket){
        return firebaseDb.collection("paket_grooming")
                .document(id_paket)
                .get();
    };
}
