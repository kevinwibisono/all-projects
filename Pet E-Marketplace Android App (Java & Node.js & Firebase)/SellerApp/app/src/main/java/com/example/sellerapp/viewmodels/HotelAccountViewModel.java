package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.ChatConvDBAccess;
import com.example.sellerapp.models.CommentDBAccess;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.ReviewDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.viewmodels.itemviewmodels.HotelItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class HotelAccountViewModel {

    private AkunDBAccess akunDB;
    private PesananJanjitemuDBAccess orderDB;
    private ChatConvDBAccess chatDB;
    private ReviewDBAccess revDB;
    private DetailPenjualDBAccess detailDB;
    private HotelDBAccess hotelDB;
    private CommentDBAccess discDB;
    private Storage storage;

    public HotelAccountViewModel(Application application) {
        akunDB = new AkunDBAccess(application);
        detailDB = new DetailPenjualDBAccess(application);
        orderDB = new PesananJanjitemuDBAccess();
        chatDB = new ChatConvDBAccess();
        revDB = new ReviewDBAccess();
        hotelDB = new HotelDBAccess();
        discDB = new CommentDBAccess();
        storage = new Storage();
    }

    private String emailUser;
    private MutableLiveData<Boolean> doneLogout = new MutableLiveData<>();
    private MutableLiveData<Boolean> canLogout = new MutableLiveData<>(false);
    private MutableLiveData<String> hotelName = new MutableLiveData<>("");
    private MutableLiveData<String> hotelPic = new MutableLiveData<>("");
    private MutableLiveData<Integer> hotelSaldo = new MutableLiveData<>();
    private MutableLiveData<Integer[]> importantThingsCount = new MutableLiveData<>(new Integer[6]);
    private MutableLiveData<ArrayList<HotelItemViewModel>> hotelVMs = new MutableLiveData<>();

    public void arrangeImportantThings(){
        Integer[] initCounts = new Integer[6];
        for (int i = 0; i < 6; i++) {
            initCounts[i] = 0;
        }
        importantThingsCount.setValue(initCounts);
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();
                    hotelName.setValue(accountsGot.get(0).getNama());
                    storage.getPictureUrlFromName(emailUser).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            hotelPic.setValue(uri.toString());
                        }
                    });

                    canLogout.setValue(true);

                    akunDB.getAccByEmail(emailUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            hotelSaldo.setValue(documentSnapshot.getLong("saldo").intValue());
                        }
                    });

                    orderDB.getOrdersHomePage(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            for (int i = 0; i < results.size(); i++) {
                                int status = results.get(i).getLong("status").intValue()-1;
                                if(status < 3){
                                    addImportantThing(status, 1);
                                }
                            }
                        }
                    });

                    chatDB.getUnreadChats(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            addImportantThing(3, results.size());
                        }
                    });

                    hotelVMs.setValue(new ArrayList<>());
                    hotelDB.getHotelRooms(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> hotelResults = queryDocumentSnapshots.getDocuments();
                            if(hotelResults.size() > 0){
                                for (int i = 0; i < hotelResults.size(); i++) {
                                    final int index = i;
                                    //untuk hitung DISKUSI BELUM DIBACA
                                    discDB.getUnreadDiscussions(hotelResults.get(i).getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                            addImportantThing(4, results.size());
                                        }
                                    });

                                    //untuk hitung ULASAN BELUM DIBALAS
                                    revDB.getUnreturnedReviews(hotelResults.get(i).getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                            addImportantThing(5, results.size());
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
    }

    private void addImportantThing(int index, int value){
        Integer[] container = importantThingsCount.getValue();
        container[index] += value;
        importantThingsCount.setValue(container);
    }

    public LiveData<Boolean> isLoggedOut(){
        return doneLogout;
    }

    public LiveData<Boolean> isAbleToLogout(){
        return canLogout;
    }

    public LiveData<String> getHotelName(){
        return hotelName;
    }

    public LiveData<String> getHotelPic(){
        return hotelPic;
    }

    public LiveData<Integer> getHotelSaldo(){
        return hotelSaldo;
    }

    public LiveData<Integer[]> getImportantThings(){
        return importantThingsCount;
    }

    public void logout(){
        canLogout.setValue(false);

        String topikEmail = emailUser.substring(0, emailUser.indexOf('@'));
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topikEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                akunDB.setClearedListener(new AkunDBAccess.onClearedListener() {
                    @Override
                    public void onCleared() {
                        detailDB.setDetailClearedListener(new DetailPenjualDBAccess.onDetailClearedListener() {
                            @Override
                            public void onDetailCleared() {
                                doneLogout.setValue(true);
                            }
                        });

                        detailDB.clearDetails();
                    }
                });

                akunDB.clearAccounts();
            }
        });
    }
}
