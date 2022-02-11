package my.istts.finalproject.viewmodels;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Alamat;
import my.istts.finalproject.models.AlamatDBAccess;
import my.istts.finalproject.models.DetailGrooming;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class GroomerLocationViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private AlamatDBAccess addrDB;

    public GroomerLocationViewModel(){
        this.orderDB = new PesananJanjitemuDBAccess();
        this.addrDB = new AlamatDBAccess();
    }

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String[]> groomerLocation = new MutableLiveData<>();
    private MutableLiveData<String[]> destLocation = new MutableLiveData<>();

    public void setPesananJanjitemu(String id_pj){
        loading.setValue(true);

        orderDB.getDetailGroomingSL(id_pj).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null && value.getDocuments().size() > 0){
                    DocumentSnapshot firstDoc = value.getDocuments().get(0);
                    DetailGrooming detailGrooming = new DetailGrooming(firstDoc);
                    groomerLocation.setValue(detailGrooming.getPosisi_groomer());

                    addrDB.getAddressById(detailGrooming.getId_alamat()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                Alamat alamat = new Alamat(documentSnapshot);
                                destLocation.setValue(alamat.getKoordinatArr());
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }
        });
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String[]> getDestLocation() {
        return destLocation;
    }

    public LiveData<String[]> getGroomerLocation() {
        return groomerLocation;
    }
}
