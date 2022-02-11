package com.example.sellerapp.models;

import com.example.sellerapp.models.herejsonaddress.AlamatList;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import retrofit2.Call;

public class AlamatDBAccess {

    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private GetAddressesService service = CariAlamatRetrofitClient.getRetrofitInstance().create(GetAddressesService.class);
    private ReverseGeoAddressService reverseService = ReverseGeoRetrofitClient.getRetrofitInstance().create(ReverseGeoAddressService.class);

    public Call<AlamatList> getHereAddresses(String q, int limit){
        Call<AlamatList> call = service.getAddreses("XkrucEspZdBEHmP7Gq5rLGFC7C2cep5rAYC2zklmfdk", q, limit);
        return call;
    }

    public Call<AlamatList> getHereAddressByCoordinates(double latitude, double longitude){
        Call<AlamatList> call = reverseService.reverseGeoAddress("XkrucEspZdBEHmP7Gq5rLGFC7C2cep5rAYC2zklmfdk", latitude+","+longitude);
        return call;
    }

    public Task<DocumentSnapshot> getAddressById(String id){
        return firebaseDB.collection("alamat")
                .document(id)
                .get();
    }
}
