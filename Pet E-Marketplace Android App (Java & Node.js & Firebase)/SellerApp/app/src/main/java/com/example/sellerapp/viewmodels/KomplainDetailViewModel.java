package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Complain;
import com.example.sellerapp.models.ComplainDBAccess;
import com.example.sellerapp.models.ItemPesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.RiwayatDBAccess;
import com.example.sellerapp.models.backend.BackendRetrofitService;
import com.example.sellerapp.models.backend.BackendRetrofitClient;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.viewmodels.itemviewmodels.ItemPJViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class KomplainDetailViewModel {
    private RiwayatDBAccess riwayatDB;
    private ProductDBAccess productDB;
    private ComplainDBAccess complainDB;
    private PesananJanjitemuDBAccess orderDB;
    private Storage storage;
    private BackendRetrofitService firebaseNotifService;
    
    public KomplainDetailViewModel(Application app){
        riwayatDB = new RiwayatDBAccess(app);
        productDB = new ProductDBAccess();
        complainDB = new ComplainDBAccess();
        orderDB = new PesananJanjitemuDBAccess();
        storage = new Storage();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    private MutableLiveData<String> orderDate = new MutableLiveData<>("");
    private MutableLiveData<Integer> orderPrice = new MutableLiveData<>(0);
    private MutableLiveData<String> orderItemName = new MutableLiveData<>("");
    private MutableLiveData<Integer> orderItemsQty = new MutableLiveData<>(0);
    private MutableLiveData<String> orderItemsPic = new MutableLiveData<>();
    private MutableLiveData<String> orderSeller = new MutableLiveData<>("");
    private MutableLiveData<String> orderBuyer = new MutableLiveData<>("");

    private MutableLiveData<ArrayList<ItemPJViewModel>> complainItemPj = new MutableLiveData<>();
    private MutableLiveData<Boolean> pjItemsLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> finishingOrder = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelled = new MutableLiveData<>();

    private MutableLiveData<ArrayList<String>> bukti = new MutableLiveData<>();
    private MutableLiveData<String> complainDetail = new MutableLiveData<>("");
    private MutableLiveData<String> complainDateTime = new MutableLiveData<>("");
    private MutableLiveData<Integer> complainTotal = new MutableLiveData<>();
    private MutableLiveData<String> complainStatus = new MutableLiveData<>("");
    private MutableLiveData<String> complainVideo = new MutableLiveData<>("");

    public void getComplainDetail(String id_complain){
        pjItemsLoading.setValue(true);
        complainItemPj.setValue(new ArrayList<>());
        bukti.setValue(new ArrayList<>());

        complainDB.getComplainById(id_complain).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Complain complain = new Complain(documentSnapshot);
                    getPjItems(complain.getComplainedItems());

                    complainDetail.setValue(complain.getKeluhan());
                    complainDateTime.setValue(complain.getTanggalStr());
                    complainStatus.setValue(complain.getStatusStr());
                    complainVideo.setValue(complain.getLink_video());
                    complainTotal.setValue(complain.getJumlah_kembali());

                    for (String complainProof:
                         complain.getBuktiArray()) {
                        storage.getPictureUrlFromName(complainProof).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                addProof(uri.toString());
                            }
                        });
                    }
                }
            }
        });
    }

    private void addProof(String url){
        ArrayList<String> current = bukti.getValue();
        current.add(url);
        bukti.setValue(current);
    }

    public void getPJDetail(String id_pj){
        orderDB.getPJById(id_pj).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                    orderSeller.setValue(pj.getemail_penjual());
                    orderBuyer.setValue(pj.getemail_pembeli());
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
                    orderPrice.setValue(pj.getTotal());
                }
            }
        });
    }

    public void getPjItems(String[] ids){
        for (int i = 0; i < ids.length; i++) {
            final int index = i;
            orderDB.getItemPJById(ids[i]).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(documentSnapshot);
                        if(index == ids.length-1){
                            addItemToVM(itemPJ, true);
                        }
                        else{
                            addItemToVM(itemPJ, false);
                        }
                    }
                }
            });
        }
    }

    private void addItemToVM(ItemPesananJanjitemu item, boolean last){
        ArrayList<ItemPJViewModel> currentVMs = complainItemPj.getValue();
        currentVMs.add(new ItemPJViewModel(item, 0));
        complainItemPj.setValue(currentVMs);
        if(last){
            pjItemsLoading.setValue(false);
        }
    }

    public void changeComplainStatus(String id_komplain, int status, String id_pj){
        if(status == 1){
            orderDB.getPJById(id_pj).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        orderDB.getItemPJByPesanan(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot itemPJDoc:
                                        queryDocumentSnapshots.getDocuments()) {
                                    ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(itemPJDoc);
                                    productDB.incProductSold(itemPJ.getId_item(), itemPJ.getJumlah());
                                    orderDB.finishShopOrder(id_pj);
                                }
                            }
                        });
                    }
                }
            });
        }

        String[] titles = {"", "Komplain Telah Diterima Penjual", "Komplain Ditolak Penjual"};
        String[] body = {"", "Pesanan Telah Diselesaikan", "Komplain Yang Kamu Ajukan Telah Ditolak Penjual"};
        finishingOrder.setValue(true);

        String topikEmail = orderBuyer.getValue().substring(0, orderBuyer.getValue().indexOf('@'));
        firebaseNotifService.sendNotif(titles[status], body[status], topikEmail);
        complainDB.changeComplainStatus(id_komplain, status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                riwayatDB.addHistoryKomplain(complainTotal.getValue(), orderBuyer.getValue());
                finishingOrder.setValue(false);
            }
        });
    }


    public LiveData<String> getOrderBuyer() {
        return orderBuyer;
    }

    public LiveData<String> getOrderSeller() {
        return orderSeller;
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

    public LiveData<ArrayList<ItemPJViewModel>> getComplainItemPj() {
        return complainItemPj;
    }

    public LiveData<Boolean> getPjItemsLoading() {
        return pjItemsLoading;
    }

    public LiveData<ArrayList<String>> getBukti() {
        return bukti;
    }

    public LiveData<String> getComplainDetail() {
        return complainDetail;
    }

    public LiveData<String> getComplainDateTime() {
        return complainDateTime;
    }

    public LiveData<Integer> getComplainTotal() {
        return complainTotal;
    }

    public LiveData<String> getComplainStatus() {
        return complainStatus;
    }

    public LiveData<String> getComplainVideo() {
        return complainVideo;
    }

    public LiveData<Boolean> isFinishingOrder() {
        return finishingOrder;
    }

    public LiveData<Boolean> isCancelled() {
        return cancelled;
    }
}
