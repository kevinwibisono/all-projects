package com.example.sellerapp.viewmodels;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.DetailJanjiTemu;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.backend.BackendRetrofitService;
import com.example.sellerapp.models.backend.BackendRetrofitClient;
import com.example.sellerapp.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class AppointmentDetailViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;
    private PesananJanjitemu pesananJanjitemu;
    private BackendRetrofitService firebaseNotifService;


    public AppointmentDetailViewModel(){
        orderDB = new PesananJanjitemuDBAccess();
        akunDB = new AkunDBAccess();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    public void getAppointmentDetail(String id_pj){
        petLoading.setValue(true);

        orderDB.getPJByIdSL(id_pj).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.getData() != null){
                    PesananJanjitemu pj = new PesananJanjitemu(value);
                    pesananJanjitemu = pj;
                    getSellerDetails(pj.getemail_penjual());

                    status.setValue(pj.getStatusStr());

                    orderDB.getDetailJanjiTemu(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                DetailJanjiTemu detailJanjiTemu = new DetailJanjiTemu(queryDocumentSnapshots.getDocuments().get(0));

                                petsAge.setValue(detailJanjiTemu.getDaftar_usia());
                                petsKind.setValue(detailJanjiTemu.getDaftar_jenis());
                                petsName.setValue(detailJanjiTemu.getDaftar_nama());
                                petLoading.setValue(false);

                                ownerName.setValue(detailJanjiTemu.getNama_pemilik());
                                ownerPhone.setValue(detailJanjiTemu.getHp_pemilik());

                                address.setValue(detailJanjiTemu.getAlamat());
                                koordinat.setValue(detailJanjiTemu.getKoordinat());

                                appoType.setValue(detailJanjiTemu.getJenis_janjitemu());
                                tglJanjiTemu.setValue(detailJanjiTemu.getTglJanjitemu());
                                keluhan.setValue(detailJanjiTemu.getKeluhan());

                            }
                        }
                    });
                }
            }
        });
    }


    private void getSellerDetails(String email){
        akunDB.getAccByEmail(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Storage storage = new Storage();
                    storage.getPictureUrlFromName(email).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sellerPic.setValue(uri.toString());
                            sellerName.setValue(documentSnapshot.getString("nama"));
                            buyerEmail.setValue(email);
                        }
                    });
                }
            }
        });
    }

    public void acceptAppointment(){
        loading.setValue(true);

        String topikEmail = pesananJanjitemu.getemail_pembeli().substring(0, pesananJanjitemu.getemail_pembeli().indexOf('@'));
        firebaseNotifService.sendNotif("Janjitemu Telah Diterima Klinik", "Janjitemu Sudah Diterima Oleh Klinik", topikEmail);
        orderDB.acceptOrderBookingAppo(pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.setValue(false);
            }
        });
    }

    private MutableLiveData<String[]> petsName = new MutableLiveData<>();
    private MutableLiveData<String[]> petsAge = new MutableLiveData<>();
    private MutableLiveData<String[]> petsKind = new MutableLiveData<>();

    private MutableLiveData<String> buyerEmail = new MutableLiveData<>("");
    private MutableLiveData<String> sellerPic = new MutableLiveData<>("");
    private MutableLiveData<String> sellerName = new MutableLiveData<>("");

    private MutableLiveData<String> ownerName = new MutableLiveData<>("");
    private MutableLiveData<String> ownerPhone = new MutableLiveData<>("");

    private MutableLiveData<String> address = new MutableLiveData<>("");
    private MutableLiveData<String> koordinat = new MutableLiveData<>("");

    private MutableLiveData<String> appoType = new MutableLiveData<>("");
    private MutableLiveData<String> status = new MutableLiveData<>("");
    private MutableLiveData<String> tglJanjiTemu = new MutableLiveData<>("");
    private MutableLiveData<String> keluhan = new MutableLiveData<>("");

    private MutableLiveData<Boolean> petLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LiveData<Boolean> isPetLoading() {
        return petLoading;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String[]> getPetsName() {
        return petsName;
    }

    public LiveData<String[]> getPetsAge() {
        return petsAge;
    }

    public LiveData<String[]> getPetsKind() {
        return petsKind;
    }

    public LiveData<String> getBuyerEmail() {
        return buyerEmail;
    }

    public LiveData<String> getSellerPic() {
        return sellerPic;
    }

    public LiveData<String> getSellerName() {
        return sellerName;
    }

    public LiveData<String> getAddress() {
        return address;
    }

    public LiveData<String> getCoordinate() {
        return koordinat;
    }

    public LiveData<String> getOwnerName() {
        return ownerName;
    }

    public LiveData<String> getOwnerPhone() {
        return ownerPhone;
    }

    public LiveData<String> getAppoType() {
        return appoType;
    }

    public LiveData<String> getStatus() {
        return status;
    }

    public LiveData<String> getTglJanjiTemu() {
        return tglJanjiTemu;
    }

    public LiveData<String> getKeluhan() {
        return keluhan;
    }
}
