package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.ChatConvDBAccess;
import com.example.sellerapp.models.DetailGrooming;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.ReviewDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.viewmodels.itemviewmodels.GroomingItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class GroomingAccountViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private AkunDBAccess akunDB;
    private DetailPenjualDBAccess detailDB;
    private ChatConvDBAccess chatDB;
    private ReviewDBAccess reviewDB;

    public GroomingAccountViewModel(Application application) {
        orderDB = new PesananJanjitemuDBAccess();
        akunDB = new AkunDBAccess(application);
        detailDB = new DetailPenjualDBAccess(application);
        chatDB = new ChatConvDBAccess();
        reviewDB = new ReviewDBAccess();
    }

    private String emailUser;
    private MutableLiveData<Integer> groomerSaldo = new MutableLiveData<>();
    private MutableLiveData<String> sellerName = new MutableLiveData<>("");
    private MutableLiveData<String> sellerPic = new MutableLiveData<>("");
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> doneLogout = new MutableLiveData<>();
    private MutableLiveData<Boolean> canLogout = new MutableLiveData<>(false);
    private MutableLiveData<Integer[]> importantThingsCount = new MutableLiveData<>(new Integer[2]);
    private MutableLiveData<ArrayList<GroomingItemViewModel>> activeGroomings = new MutableLiveData<>();

    public LiveData<Integer> getGroomerSaldo(){
        return groomerSaldo;
    }

    public LiveData<String> getSellerName(){
        return sellerName;
    }

    public LiveData<String> getSellerPic(){
        return sellerPic;
    }

    public LiveData<Boolean> isDoneLogout(){
        return doneLogout;
    }

    public LiveData<Boolean> isAbleToLogout(){
        return canLogout;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<ArrayList<GroomingItemViewModel>> getActiveGroomings(){
        return activeGroomings;
    }

    public LiveData<Integer[]> getImportantThings(){
        return importantThingsCount;
    }

    public void getGroomerDetails(){
        loading.setValue(true);
        activeGroomings.setValue(new ArrayList<>());
        Integer[] importantInit = new Integer[]{0, 0};
        importantThingsCount.setValue(importantInit);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();
                    sellerName.setValue(accountsGot.get(0).getNama());
                    Storage storage = new Storage();
                    storage.getPictureUrlFromName(emailUser).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sellerPic.setValue(uri.toString());
                        }
                    });

                    chatDB.getUnreadChats(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            setImportantThing(0, results.size());
                        }
                    });

                    reviewDB.getUnreturnedReviews(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            setImportantThing(1, queryDocumentSnapshots.getDocuments().size());
                        }
                    });

                    getGroomerActiveGroomings(emailUser);

                    akunDB.getAccByEmail(emailUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                groomerSaldo.setValue(documentSnapshot.getLong("saldo").intValue());
                            }
                        }
                    });

                    canLogout.setValue(true);
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void getGroomerActiveGroomings(String no_hp){
        orderDB.getActiveGrooming(no_hp).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Integer> counter = new ArrayList<>();
                    for (DocumentSnapshot pjDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        PesananJanjitemu pj = new PesananJanjitemu(pjDoc);

                        orderDB.getDetailGrooming(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    DetailGrooming detailGrooming = new DetailGrooming(queryDocumentSnapshots.getDocuments().get(0));
                                    if(pj.getStatus() > 0){
                                        addGroomingToVM(detailGrooming, pj);
                                    }
                                }
                                counter.add(0);
                                if(counter.size() >= queryDocumentSnapshots.getDocuments().size()){
                                    loading.setValue(false);
                                }
                            }
                        });
                    }
                }
                else{
                    loading.setValue(false);
                }
            }
        });
    }

    private void addGroomingToVM(DetailGrooming detailGrooming, PesananJanjitemu pj){
        ArrayList<GroomingItemViewModel> currentGroomings = activeGroomings.getValue();
        currentGroomings.add(new GroomingItemViewModel(detailGrooming, pj));
        activeGroomings.setValue(currentGroomings);
    }

    private void setImportantThing(int index, int value){
        Integer[] container = importantThingsCount.getValue();
        container[index] += value;
        importantThingsCount.setValue(container);
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
