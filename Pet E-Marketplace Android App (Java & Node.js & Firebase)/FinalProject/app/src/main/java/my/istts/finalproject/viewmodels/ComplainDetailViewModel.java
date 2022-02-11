package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Complain;
import my.istts.finalproject.models.ComplainDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.RiwayatDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.SendNotifResponse;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.ItemPJViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplainDetailViewModel {
    private RiwayatDBAccess riwayatDB;
    private ProductDBAccess productDB;
    private ComplainDBAccess complainDB;
    private PesananJanjitemuDBAccess orderDB;
    private Storage storage;
    private BackendRetrofitService firebaseNotifService;
    
    public ComplainDetailViewModel(Application app){
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

    private MutableLiveData<ArrayList<ItemPJViewModel>> complainItemPj = new MutableLiveData<>();
    private MutableLiveData<Boolean> pjItemsLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> finishingOrder = new MutableLiveData<>();
    private MutableLiveData<Boolean> canCancel = new MutableLiveData<>();

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

        complainDB.getComplainById(id_complain).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.getData() != null){
                    Complain complain = new Complain(value);
                    getPjItems(complain.getComplainedItems());

                    complainDetail.setValue(complain.getKeluhan());
                    complainDateTime.setValue(complain.getTanggalStr());
                    complainStatus.setValue(complain.getStatusStr());
                    complainVideo.setValue(complain.getLink_video());
                    complainTotal.setValue(complain.getJumlah_kembali());
                    if(complain.getStatus() == 0){
                        canCancel.setValue(true);
                    }
                    else{
                        canCancel.setValue(false);
                    }

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

    public void cancelComplain(String id_pj){
        finishingOrder.setValue(true);

        orderDB.getPJById(id_pj).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                    orderDB.getItemPJByPesanan(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot itemPJDoc:
                                    queryDocumentSnapshots.getDocuments()) {
                                ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(itemPJDoc);
                                productDB.incProductSold(itemPJ.getId_item(), itemPJ.getJumlah());
                            }
                        }
                    });

                    String topikEmail = pj.getemail_penjual().substring(0, pj.getemail_penjual().indexOf('@'));
                    firebaseNotifService.sendNotif("Pesanan Telah Diselesaikan", "Pesanan Telah Diselesaikan Oleh Pelanggan", topikEmail);
                    orderDB.finishOrder(7, id_pj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            riwayatDB.addHistoryOrderSuccess(pj.getTotal(), pj.getemail_penjual());
                            finishingOrder.setValue(false);
                        }
                    });
                }
            }
        });

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

    public LiveData<Boolean> isCancelable() {
        return canCancel;
    }
}
