package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Pembayaran;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.UnfinishedPaymentItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UnfinishedPaymentViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;

    public UnfinishedPaymentViewModel(Application app){
        this.orderDB = new PesananJanjitemuDBAccess();
        this.akunDB = new AkunDBAccess(app);
    }

    private String email;
    private MutableLiveData<ArrayList<UnfinishedPaymentItemViewModel>> payVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LiveData<ArrayList<UnfinishedPaymentItemViewModel>> getPayVMs(){
        return payVMs;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public void getUnfinishedPayments(){
        loading.setValue(true);
        payVMs.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();
                    orderDB.getUnfinishedPayment(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<Pembayaran> pays = new ArrayList<>();
                                for (DocumentSnapshot payDoc:
                                        queryDocumentSnapshots.getDocuments()) {
                                    pays.add(new Pembayaran(payDoc));
                                }
                                addPaymentsToVM(pays);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void addPaymentsToVM(ArrayList<Pembayaran> payments){
        ArrayList<UnfinishedPaymentItemViewModel> currentVMs = payVMs.getValue();
        for (Pembayaran payment:
             payments) {
            currentVMs.add(new UnfinishedPaymentItemViewModel(payment));
        }
        payVMs.setValue(currentVMs);
        loading.setValue(false);
    }
}
