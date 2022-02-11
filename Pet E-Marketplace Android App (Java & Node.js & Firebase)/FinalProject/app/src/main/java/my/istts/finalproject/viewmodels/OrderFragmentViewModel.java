package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.PesananItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderFragmentViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;
    private  Application app;

    public OrderFragmentViewModel(Application app){
        this.app = app;
        this.orderDB = new PesananJanjitemuDBAccess();
        this.akunDB = new AkunDBAccess(app);
    }

    private String email;
    private MutableLiveData<ArrayList<PesananItemViewModel>> orderVMs = new MutableLiveData<>();
    private MutableLiveData<String> zeroItemsTitle = new MutableLiveData<>("Tidak Ada Pesanan Aktif Saat Ini");
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> paymentsLoading = new MutableLiveData<>();
    private MutableLiveData<Integer> unfinishedPayments = new MutableLiveData<>(1);

    public LiveData<ArrayList<PesananItemViewModel>> getOrderVMs(){
        return orderVMs;
    }

    public LiveData<String> getZeroTitle(){
        return zeroItemsTitle;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<Boolean> isPaymentLoading(){
        return paymentsLoading;
    }

    public LiveData<Integer> getUnfinishedPayments(){
        return unfinishedPayments;
    }

    public void getActiveOrders(){
        paymentsLoading.setValue(true);
        orderVMs.setValue(new ArrayList<>());
        loading.setValue(true);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    orderDB.getAllUnfinishedPayment(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            unfinishedPayments.setValue(queryDocumentSnapshots.getDocuments().size());
                            paymentsLoading.setValue(false);
                        }
                    });

                    getOrdersAndSetTitle(0);

                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void getActiveOrdersByType(int tipe){
        paymentsLoading.setValue(true);
        loading.setValue(true);
        orderVMs.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    orderDB.getAllUnfinishedPayment(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            unfinishedPayments.setValue(queryDocumentSnapshots.getDocuments().size());
                            paymentsLoading.setValue(false);
                        }
                    });

                    getOrdersAndSetTitle(tipe);
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void getOrdersAndSetTitle(int tipe){
        if(tipe == 0){
            zeroItemsTitle.setValue("Tidak Ada Pesanan Aktif Saat Ini");
        }
        else if(tipe == 1){
            zeroItemsTitle.setValue("Tidak Ada Grooming Aktif Saat Ini");
        }
        else if(tipe == 2){
            zeroItemsTitle.setValue("Tidak Ada Booking Aktif Saat Ini");
        }
        else{
            zeroItemsTitle.setValue("Tidak Ada Janji Temu Aktif Saat Ini");
        }

        orderDB.getActiveOrdersByType(tipe, email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<PesananItemViewModel> pjs = new ArrayList<>();
                for (DocumentSnapshot pjDoc:
                        queryDocumentSnapshots.getDocuments()) {
                    PesananJanjitemu pj = new PesananJanjitemu(pjDoc);
                    if(pj.getStatus() > 0){
                        PesananItemViewModel vm = new PesananItemViewModel(pj);
                        pjs.add(vm);
                    }
                }
                setPJsToVM(pjs);
            }
        });
    }

    private void setPJsToVM(ArrayList<PesananItemViewModel> pjs){
        ArrayList<PesananItemViewModel> currentVMs = orderVMs.getValue();
        for (PesananItemViewModel pj:
             pjs) {
            currentVMs.add(pj);
        }
        orderVMs.setValue(currentVMs);
        loading.setValue(false);

    }

}
