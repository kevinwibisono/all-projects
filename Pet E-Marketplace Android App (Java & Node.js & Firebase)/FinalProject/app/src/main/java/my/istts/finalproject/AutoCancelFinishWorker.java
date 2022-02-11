package my.istts.finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.Pembayaran;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.RiwayatDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutoCancelFinishWorker extends Worker {

    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;
    private RiwayatDBAccess riwayatDB;
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;
    private BackendRetrofitService firebaseNotifService;

    public AutoCancelFinishWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        orderDB = new PesananJanjitemuDBAccess();
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        akunDB = new AkunDBAccess((Application) context);
        riwayatDB = new RiwayatDBAccess((Application) context);
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    @NonNull
    @Override
    public Result doWork() {
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    String email = accountsGot.get(0).getEmail();

                    orderDB.getUnfinishedPayment(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot paymentDoc:
                                    queryDocumentSnapshots.getDocuments()) {
                                Pembayaran pj = new Pembayaran(paymentDoc);
                                if(pj.getSelesai_otomatis().toDate().before(new Date())){
                                    //batal otomatis
                                    cancelPayment(email, pj.getId_payment());
                                }
                            }
                        }
                    });

                    orderDB.getCancelableOrders(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot orderDoc:
                                 queryDocumentSnapshots.getDocuments()) {
                                PesananJanjitemu pj = new PesananJanjitemu(orderDoc);
                                if(pj.getSelesai_otomatis().toDate().before(new Date())){
                                    //batal otomatis
                                    if(pj.isFinishable()){
                                        finishAnOrder(pj, email, pj.getemail_penjual());
                                    }
                                    else{
                                        cancelAnOrder(pj, email, pj.getemail_penjual());
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();

        return Result.success();
    }

    public void cancelPayment(String email, String id_payment){
        //urutan delete
        //1. seluruh pesanan
        //2. pembayaran
        String topikEmail = email.substring(0, email.indexOf('@'));
        firebaseNotifService.sendNotif("Pembayaran Telah Dibatalkan", "Masa Waktu Telah Habis", topikEmail);

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
                                                orderDB.deletePayment(id_payment);
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

    private void finishAnOrder(PesananJanjitemu pj, String email, String email_penjual){
        String topikEmail = email_penjual.substring(0, email_penjual.indexOf('@'));
        firebaseNotifService.sendNotif("Pesanananmu Telah Selesai", "Pesanan Telah Otomatis Diselesaikan", topikEmail);

        topikEmail = email.substring(0, email.indexOf('@'));
        firebaseNotifService.sendNotif("Pesanananmu Telah Selesai", "Pesanananmu Telah Otomatis Diselesaikan", topikEmail);
        int statusSelesai;
        if(pj.getJenis() == 0){
            orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot itemPJDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(itemPJDoc);
                        productDB.incProductSold(itemPJ.getId_item(), itemPJ.getJumlah());
                    }
                }
            });
            statusSelesai = 7;
        }
        else if(pj.getJenis() == 1){
            statusSelesai = 6;
        }
        else if(pj.getJenis() == 2){
            orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot itemPJDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(itemPJDoc);
                        hotelDB.incHotelBooked(itemPJ.getId_item(), itemPJ.getJumlah());
                        hotelDB.reduceOccupiedRoomAfterCancel(itemPJ.getId_item(), itemPJ.getJumlah());
                    }
                }
            });
            statusSelesai = 4;
        }
        else{
            statusSelesai = 4;
        }
        orderDB.finishOrder(statusSelesai, pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                riwayatDB.addHistoryOrderSuccess(pj.getTotal(), pj.getemail_penjual());
            }
        });

    }

    private void cancelAnOrder(PesananJanjitemu pj, String email, String email_penjual){
        String topikEmail = email_penjual.substring(0, email_penjual.indexOf('@'));
        firebaseNotifService.sendNotif("Pesanananmu Telah Dibatalkan", "Kamu Tidak Merespon Dalam Batas Waktu Yang Ditentukan. " +
                "Yuk tingkatkan lagi performamu", topikEmail);

        topikEmail = email.substring(0, email.indexOf('@'));
        firebaseNotifService.sendNotif("Pesanananmu Telah Dibatalkan", "Penjual Tidak Merespon Dalam Batas Waktu Yang Ditentukan",
                topikEmail);

        addItemQtyAgainAfterCancel(pj);
        int statusBatal;
        if(pj.getJenis() == 0){
            statusBatal = 8;
        }
        else if(pj.getJenis() == 1){
            statusBatal = 7;
        }
        else {
            statusBatal = 5;
        }
        orderDB.cancelOrder(statusBatal, pj.getId_pj(), "Dibatalkan otomatis oleh sistem").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(pj.getTotal() > 0){
                    riwayatDB.addHistoryCancel(pj.getTotal(), pj.getemail_pembeli());
                }
            }
        });


    }
}
