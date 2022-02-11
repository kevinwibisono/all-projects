package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdoptionDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<DocumentReference> addPetAdoption(String email, String gambar, String nama, String umur, int satuan_umur,
                                                  int jenis, String ras, int jenis_kelamin, String deskripsi){
        Map<String, Object> adopt = new HashMap<>();
        adopt.put("email_pemilik", email);
        adopt.put("gambar", gambar);
        adopt.put("nama", nama);
        adopt.put("umur", Integer.parseInt(umur));
        adopt.put("satuan_umur", satuan_umur);
        adopt.put("jenis", jenis);
        adopt.put("ras", ras);
        adopt.put("jenis_kelamin", jenis_kelamin);
        adopt.put("deskripsi", deskripsi);

        return firebaseDb.collection("adopsi")
                .add(adopt);
    }

    public Task<Void> updatePetAdoption(String id_adopt, String email, String gambar, String nama, String umur,
                                                  int satuan_umur, int jenis, String ras, int jenis_kelamin, String deskripsi){
        Map<String, Object> adopt = new HashMap<>();
        adopt.put("email_pemilik", email);
        adopt.put("gambar", gambar);
        adopt.put("nama", nama);
        adopt.put("umur", Integer.parseInt(umur));
        adopt.put("satuan_umur", satuan_umur);
        adopt.put("jenis", jenis);
        adopt.put("ras", ras);
        adopt.put("jenis_kelamin", jenis_kelamin);
        adopt.put("deskripsi", deskripsi);

        return firebaseDb.collection("adopsi")
                .document(id_adopt)
                .set(adopt, SetOptions.merge());
    }

    public Task<QuerySnapshot> getPetAdoptsFiltered(ArrayList<Integer> jenis, int jenis_kelamin, String email, boolean pemilik){
        Query queryTask = firebaseDb.collection("adopsi");

        if(jenis.size() > 0){
            queryTask = queryTask.whereIn("jenis", jenis);
        }

        if(pemilik){
            queryTask = queryTask.whereEqualTo("email_pemilik", email);
        }
        else{
            queryTask = queryTask.whereNotEqualTo("email_pemilik", email)
                    .orderBy("email_pemilik", Query.Direction.ASCENDING);
        }

        if(jenis_kelamin > -1){
            queryTask = queryTask.whereEqualTo("jenis_kelamin", jenis_kelamin);
        }

        return queryTask
                .get();
    }

    public Task<DocumentSnapshot> getPetAdoptById(String id_pet_adopt){
        return firebaseDb.collection("adopsi")
                .document(id_pet_adopt)
                .get();
    }
}
