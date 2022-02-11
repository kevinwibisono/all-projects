package my.istts.finalproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.DetailPenjual;
import my.istts.finalproject.models.DetailPenjualDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.ClinicItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ClinicListViewModel {
    private DetailPenjualDBAccess sellerDB;
    private AkunDBAccess akunDB;

    public ClinicListViewModel() {
        sellerDB = new DetailPenjualDBAccess();
        akunDB = new AkunDBAccess();
    }

    private MutableLiveData<ArrayList<ClinicItemViewModel>> clinicVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<String> searchKeyword = new MutableLiveData<>("");

    public LiveData<ArrayList<ClinicItemViewModel>> getClinics(){
        return clinicVMs;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public void getAllClinics(){
        loading.setValue(true);
        clinicVMs.setValue(new ArrayList<>());

        sellerDB.getAllWithRole(3).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Integer> counter = new ArrayList<>();
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        DocumentSnapshot currentDoc = queryDocumentSnapshots.getDocuments().get(i);
                        DetailPenjual clinic = new DetailPenjual(currentDoc);
                        akunDB.getAccByEmail(currentDoc.getId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                counter.add(0);
                                if(documentSnapshot.getData() != null){
                                    if(documentSnapshot.getString("nama").toLowerCase().contains(searchKeyword.getValue().toLowerCase())){
                                        addClinicsToVM(clinic, currentDoc.getId(), documentSnapshot.getString("nama"));
                                    }
                                }
                                if(counter.size() == queryDocumentSnapshots.getDocuments().size()){
                                    loading.setValue(false);
                                }
                            }
                        });
                    }
                }
                else{
                    loading.setValue(false);
                }
            }
        });
    }

    private void addClinicsToVM(DetailPenjual clinic, String email, String name){
        ArrayList<ClinicItemViewModel> current = clinicVMs.getValue();
        current.add(new ClinicItemViewModel(clinic, email, name));
        clinicVMs.setValue(current);
    }
}
