package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.DetailPenjual;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.PesananItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderFragmentViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;
    private DetailPenjualDBAccess detailDB;

    public OrderFragmentViewModel(Application app) {
        this.orderDB = new PesananJanjitemuDBAccess();
        this.akunDB = new AkunDBAccess(app);
        this.detailDB = new DetailPenjualDBAccess(app);
    }

    private String emailUser = "";
    private int role_user = -1;
    private MutableLiveData<ArrayList<PesananItemViewModel>> orderVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String[]> statuses = new MutableLiveData<>();

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<ArrayList<PesananItemViewModel>> getOrderVMs(){
        return orderVMs;
    }

    public LiveData<String[]> getStatuses(){
        return  statuses;
    }

    public void getOrdersBeginning(){
        loading.setValue(true);
        orderVMs.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();
                    detailDB.setGetDetailCompleteListener(new DetailPenjualDBAccess.onCompleteGetDetailListener() {
                        @Override
                        public void onDetailComplete(List<DetailPenjual> detailsGot) {
                            int role = detailsGot.get(0).getRole();
                            role_user = role;
                            setStatuses(role);
                            orderDB.getAllOrders(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                                        ArrayList<PesananJanjitemu> orders = new ArrayList<>();
                                        for (DocumentSnapshot orderDoc:
                                                queryDocumentSnapshots.getDocuments()) {
                                            PesananJanjitemu pj = new PesananJanjitemu(orderDoc);
                                            if(role_user < 3){
                                                if(pj.getStatus() > 0){
                                                    orders.add(new PesananJanjitemu(orderDoc));
                                                }
                                            }
                                            else{
                                                orders.add(new PesananJanjitemu(orderDoc));
                                            }
                                        }
                                        addOrdersToVM(orders);
                                    }
                                    else{
                                        loading.setValue(false);
                                    }
                                }
                            });
                        }
                    });

                    detailDB.getLocalDetail();
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void getOrdersWithStatus(int statusIdx){
        loading.setValue(true);
        orderVMs.setValue(new ArrayList<>());

        if(!emailUser.equals("") && role_user > -1){
            if(statusIdx == 0){
                orderDB.getAllOrders(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size() > 0){
                            ArrayList<PesananJanjitemu> orders = new ArrayList<>();
                            for (DocumentSnapshot orderDoc:
                                    queryDocumentSnapshots.getDocuments()) {
                                PesananJanjitemu pj = new PesananJanjitemu(orderDoc);
                                if(pj.getStatus() > 0){
                                    orders.add(new PesananJanjitemu(orderDoc));
                                }
                            }
                            addOrdersToVM(orders);
                        }
                        else{
                            loading.setValue(false);
                        }
                    }
                });
            }
            else{
                orderDB.getOrdersByStatus(emailUser, statusIdx).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size() > 0){
                            ArrayList<PesananJanjitemu> orders = new ArrayList<>();
                            for (DocumentSnapshot orderDoc:
                                    queryDocumentSnapshots.getDocuments()) {
                                orders.add(new PesananJanjitemu(orderDoc));
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
    }

    private void addOrdersToVM(ArrayList<PesananJanjitemu> orders){
        ArrayList<PesananItemViewModel> currentVMs = orderVMs.getValue();
        for (PesananJanjitemu order:
             orders) {
            currentVMs.add(new PesananItemViewModel(order));
        }
        orderVMs.setValue(currentVMs);
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
