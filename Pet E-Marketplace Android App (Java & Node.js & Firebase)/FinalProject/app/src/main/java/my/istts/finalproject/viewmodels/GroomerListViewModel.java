package my.istts.finalproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.DetailPenjual;
import my.istts.finalproject.models.DetailPenjualDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.GroomerItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GroomerListViewModel {
    private DetailPenjualDBAccess sellerDB;
    private AkunDBAccess akunDB;

    public GroomerListViewModel() {
        sellerDB = new DetailPenjualDBAccess();
        akunDB = new AkunDBAccess();
    }

    private MutableLiveData<ArrayList<GroomerItemViewModel>> groomerVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<String> searchKeyword = new MutableLiveData<>("");

    public LiveData<ArrayList<GroomerItemViewModel>> getGroomers(){
        return groomerVMs;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public void getAllGroomer(){
        loading.setValue(true);
        groomerVMs.setValue(new ArrayList<>());

        sellerDB.getAllWithRole(1).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Integer> counter = new ArrayList<>();
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        DocumentSnapshot currentDoc = queryDocumentSnapshots.getDocuments().get(i);
                        DetailPenjual groomer = new DetailPenjual(currentDoc);
                        akunDB.getAccByEmail(currentDoc.getId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                counter.add(0);
                                if(documentSnapshot.getData() != null){
                                    if(documentSnapshot.getString("nama").toLowerCase().contains(searchKeyword.getValue().toLowerCase())){
                                        addGroomersToVM(groomer, currentDoc.getId(), documentSnapshot.getString("nama"));
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

    private void addGroomersToVM(DetailPenjual groomer, String email, String name){
        ArrayList<GroomerItemViewModel> current = groomerVMs.getValue();
        current.add(new GroomerItemViewModel(groomer, email, name));
        groomerVMs.setValue(current);
    }
}
