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

public class OrderListViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;

    public OrderListViewModel(Application app){
        orderDB = new PesananJanjitemuDBAccess();
        akunDB = new AkunDBAccess(app);
    }

    private String email;
    private MutableLiveData<ArrayList<PesananItemViewModel>> ordersVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String[]> statuses = new MutableLiveData<>();

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<String[]> getStatuses(){
        return statuses;
    }

    public LiveData<ArrayList<PesananItemViewModel>> getOrderVMs(){
        return ordersVMs;
    }

    public void getOrders(int tipe, int statusChipIdx){
        loading.setValue(true);
        ordersVMs.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    orderDB.getOrdersByTypeStatus(email, tipe, statusChipIdx).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<PesananJanjitemu> orders = new ArrayList<>();
                                for (DocumentSnapshot doc:
                                        queryDocumentSnapshots.getDocuments()) {
                                    PesananJanjitemu pj = new PesananJanjitemu(doc);
                                    orders.add(pj);
                                }
                                addOrdersToVM(orders);
                            }
                            else{
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void addOrdersToVM(ArrayList<PesananJanjitemu> orders){
        ArrayList<PesananItemViewModel> newVMs = new ArrayList<>();
        for (PesananJanjitemu order:
             orders) {
            newVMs.add(new PesananItemViewModel(order));
        }
        ordersVMs.setValue(newVMs);
        loading.setValue(false);
    }

    public void setStatuses(int jenis){
        if(jenis == 0){
            statuses.setValue(new String[]{"Semua", "Menunggu Konfirmasi", "Diproses Penjual", "Pesanan Disiapkan", "Dalam Pengiriman", "Siap Untuk Pickup", "Dikomplain", "Selesai", "Dibatalkan"});
        }
        else if(jenis == 1){
            statuses.setValue(new String[]{"Semua", "Menunggu Konfirmasi", "Menunggu Jadwal Grooming", "Jadwal Grooming Aktif", "Groomer Dalam Perjalanan", "Proses Grooming", "Selesai", "Dibatalkan"});
        }
        else if(jenis == 2){
            statuses.setValue(new String[]{"Semua", "Menunggu Konfirmasi", "Menunggu Jadwal Booking", "Dalam Penginapan", "Selesai", "Dibatalkan"});
        }
        else {
            statuses.setValue(new String[]{"Semua", "Menunggu Konfirmasi", "Menunggu Jadwal Janjitemu", "Jadwal Janjitemu Aktif", "Selesai", "Dibatalkan"});
        }
    }
}
