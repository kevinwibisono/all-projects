package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.Pembayaran;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.RiwayatDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.ItemPaymentViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FinishPaymentViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private RiwayatDBAccess riwayatDB;

    public FinishPaymentViewModel(Application app){
        this.orderDB = new PesananJanjitemuDBAccess();
        this.riwayatDB = new RiwayatDBAccess(app);
    }

    private MutableLiveData<ArrayList<ItemPaymentViewModel>> paymentDetailItems = new MutableLiveData<>();
    private MutableLiveData<String> judulNo = new MutableLiveData<>("");
    private MutableLiveData<String> selesai = new MutableLiveData<>("");
    private MutableLiveData<String> metode = new MutableLiveData<>("");
    private MutableLiveData<String> nomor = new MutableLiveData<>("");
    private MutableLiveData<String> qr = new MutableLiveData<>("");
    private MutableLiveData<String> keterangan = new MutableLiveData<>("");
    private MutableLiveData<String> bukti_transfer = new MutableLiveData<>("");
    private MutableLiveData<Integer> total = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> detailLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> finishCancel = new MutableLiveData<>();

    public void getPayment(String id_payment){
        orderDB.getPayment(id_payment).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Pembayaran pembayaran = new Pembayaran(documentSnapshot);

                    if(pembayaran.getMetode().contains("BCA")){
                        judulNo.setValue("Nomor Rekening:");
                    }
                    else if(pembayaran.getMetode().contains("Gerai")){
                        judulNo.setValue("Nomor Pembayaran:");
                    }
                    else if(pembayaran.getMetode().contains("QRIS")){
                        judulNo.setValue("QR Code:");
                    }
                    else{
                        judulNo.setValue("Nomor Virtual Account:");
                    }
                    selesai.setValue(pembayaran.getSelesaiStr());
                    metode.setValue(pembayaran.getMetode());
                    nomor.setValue(pembayaran.getNo_bayar());
                    qr.setValue(pembayaran.getGambar_qr());
                    total.setValue(pembayaran.getTotal_bayar());
                    keterangan.setValue(pembayaran.getKeterangan());

                    getPaymentDetail(pembayaran.getId_pjs());
                }
            }
        });
    }

    private void getPaymentDetail(String id_pjs){
        detailLoading.setValue(true);
        paymentDetailItems.setValue(new ArrayList<>());

        ArrayList<PesananJanjitemu> arrayPJs = new ArrayList<>();
        String[] ids = id_pjs.split("\\|");
        for (int i = 0; i < ids.length; i++) {
            orderDB.getPJById(ids[i]).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                    arrayPJs.add(pj);
                    orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                for (int j = 0; j < queryDocumentSnapshots.getDocuments().size(); j++) {
                                    DocumentSnapshot itemPJDoc = queryDocumentSnapshots.getDocuments().get(j);
                                    ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(itemPJDoc);
                                    if(arrayPJs.size() == ids.length && j == queryDocumentSnapshots.getDocuments().size()-1){
                                        addItemPaymentToVM(itemPJ, pj, true);
                                    }
                                    else{
                                        addItemPaymentToVM(itemPJ, pj, false);
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private void addItemPaymentToVM(ItemPesananJanjitemu itemPJ, PesananJanjitemu pj, boolean last){
        ArrayList<ItemPaymentViewModel> currentVMs = paymentDetailItems.getValue();
        currentVMs.add(new ItemPaymentViewModel(itemPJ, pj));
        paymentDetailItems.setValue(currentVMs);
        if(last){
            determineVMShowSellerOngkir();
        }
    }

    private void determineVMShowSellerOngkir(){
        ArrayList<ItemPaymentViewModel> currentVMs = paymentDetailItems.getValue();
        String lastSeller = "";
        for (int i = 0; i < currentVMs.size(); i++) {
            ItemPaymentViewModel currentVM = currentVMs.get(i);
            if(!currentVM.getSeller().equals(lastSeller)){
                currentVM.setShowSeller(true);
                lastSeller = currentVM.getSeller();
            }
            //bukan item terakhir, cek seller setelahnya
            if(i < currentVMs.size()-1){
                //jika seller setelahnya berbeda, maka munculkan sisi ongkir
                ItemPaymentViewModel nextVM = currentVMs.get(i+1);
                if(!currentVM.getSeller().equals(nextVM.getSeller())){
                    currentVM.setShowDetail(true);
                }
            }
            else{
                //item terakhir, munculkan sisi ongkir
                currentVM.setShowDetail(true);
            }
        }
        detailLoading.setValue(false);
    }

    public void cancelPayment(String id_payment){
        //urutan delete
        //1. seluruh pesanan
        //2. pembayaran
        cancelLoading.setValue(true);
        orderDB.getPayment(id_payment).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Pembayaran payment = new Pembayaran(documentSnapshot);

                    String[] id_pjs = payment.getId_pjs().split("\\|");
                    ArrayList<Integer> counterDeleted = new ArrayList<>();

                    for (int i = 0; i < id_pjs.length; i++) {
                        orderDB.getPJById(id_pjs[i]).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.getData() != null){
                                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                                    addItemQtyAgainAfterCancel(pj);

                                    orderDB.deletePJ(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            counterDeleted.add(0);
                                            if(counterDeleted.size() == id_pjs.length){
                                                //pj sdh didelete semua
                                                //delete payment
                                                orderDB.deletePayment(id_payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        cancelLoading.setValue(false);
                                                        finishCancel.setValue(true);
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    orderDB.deleteDetailPJ(pj.getId_pj(), pj.getJenis());
                                }

                            }
                        });

                    }
                }
            }
        });
    }

    private void addItemQtyAgainAfterCancel(PesananJanjitemu pj){
        orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(doc);
                        if(pj.getJenis() == 0){
                            ProductDBAccess productDB = new ProductDBAccess();
                            if(itemPJ.getId_variasi().equals("")){
                                productDB.addProductQtyAfterCancel(itemPJ.getId_item(), itemPJ.getJumlah());
                            }
                            else{
                                productDB.addVarQtyAfterCancel(itemPJ.getId_item(), itemPJ.getId_variasi(), itemPJ.getJumlah());
                            }
                        }
                        else if(pj.getJenis() == 2){
                            HotelDBAccess hotelDB = new HotelDBAccess();
                            hotelDB.reduceOccupiedRoomAfterCancel(itemPJ.getId_item(), itemPJ.getJumlah());
                        }
                    }
                }
            }
        });
    }

    public LiveData<ArrayList<ItemPaymentViewModel>> getPaymentDetailItems(){
        return paymentDetailItems;
    }

    public LiveData<String> getJudulNo(){
        return judulNo;
    }

    public LiveData<String> getBatasPembayaran(){
        return selesai;
    }

    public LiveData<String> getMethod(){
        return metode;
    }

    public LiveData<String> getNomor(){
        return nomor;
    }

    public LiveData<String> getQR(){
        return qr;
    }

    public LiveData<String> getKeterangan(){
        return keterangan;
    }

    public LiveData<String> getBuktiTransfer(){
        return bukti_transfer;
    }

    public LiveData<Integer> getTotal(){
        return total;
    }

    public LiveData<Boolean> isDetailLoading(){
        return detailLoading;
    }

    public LiveData<Boolean> isCancelLoading(){
        return cancelLoading;
    }

    public LiveData<Boolean> isCancelFinished(){
        return finishCancel;
    }
}
