package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.ChatConvDBAccess;
import com.example.sellerapp.models.CommentDBAccess;
import com.example.sellerapp.models.ComplainDBAccess;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.ReviewDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.models.PromoDBAccess;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class ShopAccountViewModel {

    private AkunDBAccess akunDB;
    private DetailPenjualDBAccess detailDB;
    private PromoDBAccess voucherDB;
    private PesananJanjitemuDBAccess orderDB;
    private ChatConvDBAccess chatDB;
    private ReviewDBAccess revDB;
    private ComplainDBAccess compDB;
    private ProductDBAccess prodDB;
    private CommentDBAccess discDB;
    private Storage storage;
    private Application app;

    public ShopAccountViewModel(Application application) {
        this.app = application;
        akunDB = new AkunDBAccess(application);
        detailDB = new DetailPenjualDBAccess(application);
        voucherDB = new PromoDBAccess();
        orderDB = new PesananJanjitemuDBAccess();
        chatDB = new ChatConvDBAccess();
        revDB = new ReviewDBAccess();
        compDB = new ComplainDBAccess();
        prodDB = new ProductDBAccess();
        discDB = new CommentDBAccess();
        storage = new Storage();
    }

    private String emailUser;
    private MutableLiveData<String> sellerName = new MutableLiveData<>("");
    private MutableLiveData<String> sellerPic = new MutableLiveData<>();
    private MutableLiveData<Integer> saldo = new MutableLiveData<>();
    private MutableLiveData<String> vouchers = new MutableLiveData<>("Belum Ada");

    private MutableLiveData<Integer[]> importantThingsCount = new MutableLiveData<>();
    private MutableLiveData<Boolean> doneLogout = new MutableLiveData<>();
    private MutableLiveData<Boolean> canLogout = new MutableLiveData<>(false);

    public LiveData<String> getSellerName() {
        return sellerName;
    }

    public LiveData<String> getSellerPic() {
        return sellerPic;
    }

    public LiveData<Integer> getSaldo() {
        return saldo;
    }

    public LiveData<String> getVouchers() {
        return vouchers;
    }

    public LiveData<Integer[]> getThingsCount() {
        return importantThingsCount;
    }

    public LiveData<Boolean> isDoneLogout() {
        return doneLogout;
    }

    public LiveData<Boolean> isAbleToLogout(){
        return canLogout;
    }

    public void getSellerDetail(){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();
                    sellerName.setValue(accountsGot.get(0).getNama());
                    storage.getPictureUrlFromName(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sellerPic.setValue(uri.toString());
                        }
                    });
                    
                    canLogout.setValue(true);

                    akunDB.getAccByEmail(emailUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                saldo.setValue(documentSnapshot.getLong("saldo").intValue());
                            }
                        }
                    });

                    voucherDB.getShopPromos(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            if(results.size() > 0){
                                vouchers.setValue(results.size()+" Voucher");
                            }
                            countImportantThings(emailUser);
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void countImportantThings(String email){
        Integer[] counts = new Integer[7];
        for (int i = 0; i < 7; i++) {
            counts[i] = 0;
        }
        importantThingsCount.setValue(counts);

        //untuk jumlah order yang baru, perlu dikirim dan perlu dipickup
        orderDB.getOrdersHomePage(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Integer[] thingsCount = {0, 0, 0};
                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot doc: results) {
                    if(doc.getLong("status").intValue() > 0 && doc.getLong("status").intValue() < 4){
                        thingsCount[doc.getLong("status").intValue()-1]++;
                    }
                }
                for (int i=0;i<3;i++){
                    //0-2
                    updateImportantThings(i, thingsCount[i]);
                }
            }
        });

        //untuk jumlah komplain yang belum terselesaikan
        orderDB.getComplainedOrders(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> complainedOrders = queryDocumentSnapshots.getDocuments();
                if(complainedOrders.size() <= 0){
                    updateImportantThings(3, 0);
                }
                else{
                    for (int i = 0; i < complainedOrders.size(); i++) {
                        compDB.getWaitingComplains(complainedOrders.get(i).getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                updateImportantThings(3, results.size());
                            }
                        });
                    }
                }
            }
        });

        //untuk jumlah chat yang belum dibaca
        chatDB.getUnreadChats(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                updateImportantThings(4, results.size());
            }
        });

        //untuk jumlah diskusi yang belum dibalas
        prodDB.getAllProducts(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot prodDoc:
                     results) {
                    revDB.getUnreturnedReviews(prodDoc.getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> productReviews = queryDocumentSnapshots.getDocuments();
                            updateImportantThings(5, productReviews.size());
                        }
                    });
                    discDB.getUnreadDiscussions(prodDoc.getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> productDiscussions = queryDocumentSnapshots.getDocuments();
                            updateImportantThings(6, productDiscussions.size());
                        }
                    });
                }
            }
        });
    }

    private void updateImportantThings(int index, int value){
        Integer[] countHolder = importantThingsCount.getValue();
        countHolder[index] += value;
        importantThingsCount.setValue(countHolder);
    }

    public void logout(){
        doneLogout.setValue(false);
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(app, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
