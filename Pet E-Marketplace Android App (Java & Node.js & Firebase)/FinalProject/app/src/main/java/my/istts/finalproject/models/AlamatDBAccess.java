package my.istts.finalproject.models;

import my.istts.finalproject.models.herejsonaddress.AlamatList;
import my.istts.finalproject.inputclasses.AlamatInput;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class AlamatDBAccess {

    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private HereGetAddressesService service = HereRetrofitClient.getRetrofitInstance().create(HereGetAddressesService.class);

    public Call<AlamatList> getHereAddresses(String q, int limit){
        service = HereRetrofitClient.getRetrofitInstance().create(HereGetAddressesService.class);
        Call<AlamatList> call = service.getAddreses("XkrucEspZdBEHmP7Gq5rLGFC7C2cep5rAYC2zklmfdk", q, limit);
        return call;
    }

    public Call<AlamatList> getHereAddressByCoordinates(double latitude, double longitude){
        service = ReverseGeoRetrofitClient.getRetrofitInstance().create(HereGetAddressesService.class);
        Call<AlamatList> call = service.reverseGeoAddress("XkrucEspZdBEHmP7Gq5rLGFC7C2cep5rAYC2zklmfdk", latitude+","+longitude);
        return call;
    }

    public Task<DocumentSnapshot> getAddressById(String id){
        return firebaseDB.collection("alamat")
                .document(id)
                .get();
    }

    public Task<QuerySnapshot> getAllAddresses(String email){
        return firebaseDB.collection("alamat")
                .whereEqualTo("email_pemilik", email)
                .whereEqualTo("dihapus", false)
                .get();
    }

    public Task<QuerySnapshot> getSelectedAddress(String email){
        return firebaseDB.collection("alamat")
                .whereEqualTo("email_pemilik", email)
                .whereEqualTo("dipilih", true)
                .whereEqualTo("dihapus", false)
                .get();
    }

    public Task<DocumentReference> insertAddress(String email_pemilik, AlamatInput input){
        Map<String, Object> addr = new HashMap<>();
        addr.put("email_pemilik", email_pemilik);
        addr.put("nama", input.nama.getValue());
        addr.put("no_hp", input.hp.getValue());
        addr.put("alamat_lengkap", input.alamat.getValue());
        addr.put("kota", input.kota.getValue());
        addr.put("kecamatan", input.kecamatan.getValue());
        addr.put("kelurahan", input.kelurahan.getValue());
        addr.put("kodepos", input.kodepos.getValue());
        addr.put("koordinat", input.getKoordinat());
        addr.put("catatan", input.catatan.getValue());
        addr.put("dipilih", false);
        addr.put("dihapus", false);

        return firebaseDB.collection("alamat")
                .add(addr);
    }

    public Task<Void> deleteAddress(String id){
        Map<String, Object> addr = new HashMap<>();
        addr.put("dihapus", true);

        return firebaseDB.collection("alamat")
                .document(id)
                .set(addr, SetOptions.merge());
    }

    public Task<Void> updateAddress(String id, AlamatInput input){
        Map<String, Object> addr = new HashMap<>();
        addr.put("nama", input.nama.getValue());
        addr.put("no_hp", input.hp.getValue());
        addr.put("alamat_lengkap", input.alamat.getValue());
        addr.put("kota", input.kota.getValue());
        addr.put("kecamatan", input.kecamatan.getValue());
        addr.put("kelurahan", input.kelurahan.getValue());
        addr.put("kodepos", input.kodepos.getValue());
        addr.put("koordinat", input.getKoordinat());
        addr.put("catatan", input.catatan.getValue());

        return firebaseDB.collection("alamat")
                .document(id)
                .set(addr, SetOptions.merge());
    }

    public Task<Void> selectAddress(String id, boolean selected){
        Map<String, Object> addr = new HashMap<>();
        addr.put("dipilih", selected);

        return firebaseDB.collection("alamat")
                .document(id)
                .set(addr, SetOptions.merge());
    }

}
