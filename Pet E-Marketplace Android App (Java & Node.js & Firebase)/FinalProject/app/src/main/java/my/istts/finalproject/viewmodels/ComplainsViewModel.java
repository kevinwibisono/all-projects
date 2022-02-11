package my.istts.finalproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Complain;
import my.istts.finalproject.models.ComplainDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.ComplainItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ComplainsViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private ComplainDBAccess complainDB;

    public ComplainsViewModel(){
        orderDB = new PesananJanjitemuDBAccess();
        complainDB = new ComplainDBAccess();
    }

    private MutableLiveData<ArrayList<ComplainItemViewModel>> pjComplains = new MutableLiveData<>();
    private MutableLiveData<String> orderDate = new MutableLiveData<>("");
    private MutableLiveData<Integer> orderPrice = new MutableLiveData<>(0);
    private MutableLiveData<String> orderItemName = new MutableLiveData<>("");
    private MutableLiveData<Integer> orderItemsQty = new MutableLiveData<>(0);
    private MutableLiveData<String> orderItemsPic = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private MutableLiveData<Boolean> canAdd = new MutableLiveData<>();

    public void getPJDetail(String id_pj){
        loading.setValue(true);
        pjComplains.setValue(new ArrayList<>());

        orderDB.getPJById(id_pj).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                    orderDB.getItemPJByPesanan(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int total = 0;
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                                ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(firstDoc);
                                orderItemName.setValue(itemPJ.getNama());
                                orderItemsPic.setValue(itemPJ.getGambar());
                                total += (itemPJ.getHarga() * itemPJ.getJumlah());

                            }
                            orderItemsQty.setValue(queryDocumentSnapshots.getDocuments().size());
                            orderPrice.setValue(total);
                        }
                    });
                    orderDate.setValue(pj.gettanggalStr());
                    getPJComplains(id_pj);
                    
                }
            }
        });
    }
    
    private void getPJComplains(String id_pj){
        complainDB.getPJComplains(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean addEnabled = true;
                ArrayList<Complain> complains = new ArrayList<>();
                for (DocumentSnapshot complainDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    Complain complain = new Complain(complainDoc);
                    if(complain.getStatus() == 0 || complain.getStatus() == 1){
                        addEnabled = false;
                    }
                    complains.add(new Complain(complainDoc));
                }
                canAdd.setValue(addEnabled);
                addComplainsToVM(complains);
            }
        });
    }

    private void addComplainsToVM(ArrayList<Complain> complains){
        ArrayList<ComplainItemViewModel> currentVMs = pjComplains.getValue();
        for (Complain complain:
             complains) {
            currentVMs.add(new ComplainItemViewModel(complain));
        }
        pjComplains.setValue(currentVMs);
        loading.setValue(false);
    }

    public LiveData<ArrayList<ComplainItemViewModel>> getPjComplains() {
        return pjComplains;
    }

    public LiveData<String> getOrderDate() {
        return orderDate;
    }

    public LiveData<Integer> getOrderPrice() {
        return orderPrice;
    }

    public LiveData<String> getOrderItemName() {
        return orderItemName;
    }

    public LiveData<Integer> getOrderItemsQty() {
        return orderItemsQty;
    }

    public LiveData<String> getOrderItemsPic() {
        return orderItemsPic;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<Boolean> getCanAdd() {
        return canAdd;
    }
}
