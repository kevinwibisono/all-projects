package my.istts.finalproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.DetailBookingHotel;
import my.istts.finalproject.models.DetailGrooming;
import my.istts.finalproject.models.DetailJanjiTemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.SendNotifResponse;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoStartWorker extends Worker {

    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;
    private BackendRetrofitService firebaseNotifService;

    public AutoStartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        orderDB = new PesananJanjitemuDBAccess();
        akunDB = new AkunDBAccess(context);
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

                    orderDB.getWaitingOrder(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot orderDoc:
                                 queryDocumentSnapshots.getDocuments()) {
                                PesananJanjitemu pj = new PesananJanjitemu(orderDoc);
                                if(pj.getJenis() == 1){
                                    //grooming
                                    orderDB.getDetailGrooming(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                                DetailGrooming detailGrooming = new DetailGrooming(queryDocumentSnapshots.getDocuments().get(0));
                                                checkGroomingDate(detailGrooming, email, pj.getemail_penjual());
                                            }
                                        }
                                    });
                                }
                                else if(pj.getJenis() == 2){
                                    //grooming
                                    orderDB.getDetailHotelBooking(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                                DetailBookingHotel detailBookingHotel = new DetailBookingHotel(queryDocumentSnapshots.getDocuments().get(0));
                                                checkBookingDate(detailBookingHotel, email, pj.getemail_penjual());
                                            }
                                        }
                                    });
                                }
                                else{
                                    orderDB.getDetailJanjiTemu(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                                DetailJanjiTemu detailJT = new DetailJanjiTemu(queryDocumentSnapshots.getDocuments().get(0));
                                                checkAppoDate(detailJT, email, pj.getemail_penjual());
                                            }
                                        }
                                    });
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


    private void checkGroomingDate(DetailGrooming detailGrooming, String email, String email_seller){
        if(detailGrooming.getTgl_booking().toDate().getTime() < new Date().getTime()){
            String topikEmail = email_seller.substring(0, email_seller.indexOf('@'));
            firebaseNotifService.sendNotif("Jadwal Grooming Telah Aktif", "Jadwal Groomingmu Telah Aktif", topikEmail);

            topikEmail = email.substring(0, email.indexOf('@'));
            firebaseNotifService.sendNotif("Jadwal Grooming Telah Aktif", "Jadwal Groomingmu Telah Aktif", topikEmail);
            orderDB.activateOrder(3, detailGrooming.getId_pj(), detailGrooming.getTgl_booking());
        }
    }

    private void checkBookingDate(DetailBookingHotel detailBook, String email, String email_seller){
        if(detailBook.getTgl_masuk().toDate().getTime() < new Date().getTime()){
            String topikEmail = email_seller.substring(0, email_seller.indexOf('@'));
            firebaseNotifService.sendNotif("Jadwal Penginapan Telah Aktif", "Jadwal Penginapanmu Telah Aktif", topikEmail);

            topikEmail = email.substring(0, email.indexOf('@'));
            firebaseNotifService.sendNotif("Jadwal Penginapan Telah Aktif", "Jadwal Penginapanmu Telah Aktif", topikEmail);
            orderDB.activateOrder(3, detailBook.getId_pj(), detailBook.getTgl_keluar());
        }
    }

    private void checkAppoDate(DetailJanjiTemu detailAppo, String email, String email_seller){
        if(detailAppo.getTgl_janjitemu().toDate().getTime() < new Date().getTime()){
            String topikEmail = email_seller.substring(0, email_seller.indexOf('@'));
            firebaseNotifService.sendNotif("Jadwal JanjiTemu Telah Aktif", "Jadwal JanjiTemu Telah Aktif", topikEmail);

            topikEmail = email.substring(0, email.indexOf('@'));
            firebaseNotifService.sendNotif("Jadwal JanjiTemu Telah Aktif", "Jadwal JanjiTemu Telah Aktif", topikEmail);
            orderDB.activateOrder(3, detailAppo.getId_pj(), detailAppo.getTgl_janjitemu());
        }
    }
}
