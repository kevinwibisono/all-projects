package my.istts.finalproject.viewmodels;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.DetailJanjiTemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.SendNotifResponse;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    getSellerDetail(pj.getemail_penjual());

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

                                alamat.setValue(detailJanjiTemu.getAlamat());
                                koordinat.setValue(detailJanjiTemu.getKoordinat());

                                appoType.setValue(detailJanjiTemu.getJenis_janjitemu());
                                tglJanjiTemu.setValue(detailJanjiTemu.getTglJanjitemu());
                                keluhan.setValue(detailJanjiTemu.getKeluhan());

                                reviewAdded.setValue(pj.isReviewGiven());

                            }
                        }
                    });
                }
            }
        });
    }

    private void getSellerDetail(String email){
        akunDB.getAccByEmail(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Storage storage = new Storage();
                    storage.getPictureUrlFromName(email).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            clinicPic.setValue(uri.toString());
                            clinicName.setValue(documentSnapshot.getString("nama"));
                            clinicEmail.setValue(email);
                        }
                    });
                }
            }
        });
    }

    public void cancelAppointment(String alasan){
        loading.setValue(true);

        String topikEmail = pesananJanjitemu.getemail_penjual().substring(0, pesananJanjitemu.getemail_penjual().indexOf('@'));
        firebaseNotifService.sendNotif("Janjitemu Dibatalkan", "Janjitemu Telah Dibatalkan Oleh Pembeli", topikEmail);
        orderDB.cancelOrder(5, pesananJanjitemu.getId_pj(), alasan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.setValue(false);
            }
        });
    }

    public void finishAppointment(){
        loading.setValue(true);

        String topikEmail = pesananJanjitemu.getemail_penjual().substring(0, pesananJanjitemu.getemail_penjual().indexOf('@'));
        firebaseNotifService.sendNotif("Janjitemu Telah Selesai", "Janjitemu Telah Diselesaikan Oleh Pembeli", topikEmail);
        orderDB.finishOrder(4, pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.setValue(false);
            }
        });
    }

    private MutableLiveData<String[]> petsName = new MutableLiveData<>();
    private MutableLiveData<String[]> petsAge = new MutableLiveData<>();
    private MutableLiveData<String[]> petsKind = new MutableLiveData<>();

    private MutableLiveData<String> clinicEmail = new MutableLiveData<>("");
    private MutableLiveData<String> clinicPic = new MutableLiveData<>("");
    private MutableLiveData<String> clinicName = new MutableLiveData<>("");

    private MutableLiveData<String> ownerName = new MutableLiveData<>("");
    private MutableLiveData<String> ownerPhone = new MutableLiveData<>("");

    private MutableLiveData<String> alamat = new MutableLiveData<>("");
    private MutableLiveData<String> koordinat = new MutableLiveData<>("");

    private MutableLiveData<String> appoType = new MutableLiveData<>("");
    private MutableLiveData<String> status = new MutableLiveData<>("");
    private MutableLiveData<String> tglJanjiTemu = new MutableLiveData<>("");
    private MutableLiveData<String> keluhan = new MutableLiveData<>("");

    private MutableLiveData<Boolean> petLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> reviewAdded = new MutableLiveData<>();

    public LiveData<Boolean> isPetLoading() {
        return petLoading;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> isReviewAdded() {
        return reviewAdded;
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

    public LiveData<String> getClinicEmail() {
        return clinicEmail;
    }

    public LiveData<String> getClinicPic() {
        return clinicPic;
    }

    public LiveData<String> getClinicName() {
        return clinicName;
    }

    public LiveData<String> getOwnerName() {
        return ownerName;
    }

    public LiveData<String> getOwnerPhone() {
        return ownerPhone;
    }

    public LiveData<String> getAlamat() {
        return alamat;
    }

    public LiveData<String> getCoordinate() {
        return koordinat;
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
