package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.DetailPenjual;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.Review;
import com.example.sellerapp.models.ReviewDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.ReviewItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReturnReviewsViewModel {
    private AkunDBAccess akunDB;
    private DetailPenjualDBAccess detailDB;
    private ReviewDBAccess reviewDB;
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;
    private Application app;

    public ReturnReviewsViewModel(Application application) {
        app = application;
        akunDB = new AkunDBAccess(application);
        detailDB = new DetailPenjualDBAccess(application);
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        reviewDB = new ReviewDBAccess();
    }

    private String email;
    private int role;
    private MutableLiveData<ArrayList<ReviewItemViewModel>> reviewsVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> revLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LiveData<ArrayList<ReviewItemViewModel>> getReviewsVMs() {
        return reviewsVMs;
    }

    public LiveData<Boolean> isRevLoading() {
        return revLoading;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public void beginGetReviews(){
        reviewsVMs.setValue(new ArrayList<>());
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();
                    detailDB.setGetDetailCompleteListener(new DetailPenjualDBAccess.onCompleteGetDetailListener() {
                        @Override
                        public void onDetailComplete(List<DetailPenjual> detailsGot) {
                            if(detailsGot.size() > 0){
                                role = detailsGot.get(0).getRole();
                                getReviewsRoleBased(false);
                            }
                        }
                    });

                    detailDB.getLocalDetail();
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void getReviewsRoleBased(boolean returned){
        revLoading.setValue(true);
        reviewsVMs.setValue(new ArrayList<>());
        if(role == 0){
            getProductReviews(returned);
        }
        else if(role == 2){
            getHotelReviews(returned);
        }
        else{
            getGroomClinicReviews(returned);
        }
    }

    private void getProductReviews(boolean returned){
        productDB.getAllProducts(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> productsDocs = queryDocumentSnapshots.getDocuments();
                if(productsDocs.size() > 0){
                    for (int i = 0; i < productsDocs.size(); i++){
                        boolean lastItem = false;
                        if(i == productsDocs.size()-1){
                            lastItem = true;
                        }
                        if(returned){
                            getReturnedReviews(productsDocs.get(i).getId(), lastItem);
                        }
                        else{
                            getUnreturnedReviews(productsDocs.get(i).getId(), lastItem);
                        }
                    }
                }
                else{
                    revLoading.setValue(false);
                }
            }
        });
    }

    private void getHotelReviews(boolean returned){
        hotelDB.getHotelRooms(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> hotelDocs = queryDocumentSnapshots.getDocuments();
                if(hotelDocs.size() > 0){
                    for (int i = 0; i < hotelDocs.size(); i++) {
                        boolean lastItem = false;
                        if(i == hotelDocs.size()-1){
                            lastItem = true;
                        }
                        if(returned){
                            getReturnedReviews(hotelDocs.get(i).getId(), lastItem);
                        }
                        else{
                            getUnreturnedReviews(hotelDocs.get(i).getId(), lastItem);
                        }
                    }
                }
                else{
                    revLoading.setValue(false);
                }
            }
        });
    }

    private void getGroomClinicReviews(boolean returned){
        if(returned){
            getReturnedReviews(email, true);
        }
        else{
            getUnreturnedReviews(email, true);
        }
    }

    private void getUnreturnedReviews(String id, boolean lastItem){
        reviewDB.getUnreturnedReviews(id).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> reviewsDocs = queryDocumentSnapshots.getDocuments();
                if(reviewsDocs.size() > 0){
                    for (int i = 0; i < reviewsDocs.size(); i++) {
                        addReviewToLiveData(new Review(reviewsDocs.get(i)));
                        if(lastItem && i == reviewsDocs.size()-1){
                            revLoading.setValue(false);
                        }
                    }
                }
                else{
                    if(lastItem){
                        revLoading.setValue(false);
                    }
                }
            }
        });
    }

    private void getReturnedReviews(String id, boolean lastItem){
        reviewDB.getReturnedReviews(id).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> reviewsDocs = queryDocumentSnapshots.getDocuments();
                if(reviewsDocs.size() > 0){
                    for (int i = 0; i < reviewsDocs.size(); i++) {
                        addReviewToLiveData(new Review(reviewsDocs.get(i)));
                        if(lastItem && i == reviewsDocs.size()-1){
                            revLoading.setValue(false);
                        }
                    }
                }
                else{
                    if(lastItem){
                        revLoading.setValue(false);
                    }
                }
            }
        });
    }

    private void addReviewToLiveData(Review rev){
        ArrayList<ReviewItemViewModel> currentVMs = reviewsVMs.getValue();
        currentVMs.add(new ReviewItemViewModel(rev, app));
        reviewsVMs.setValue(currentVMs);
    }

    public void addReviewReturn(String id, String balasan){
        if(!balasan.equals("")){
            loading.setValue(true);
            reviewDB.returnReview(id, balasan).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loading.setValue(false);
                    Toast.makeText(app, "Berhasil Menambahkan Balasan", Toast.LENGTH_SHORT).show();
                    getReviewsRoleBased(false);
                }
            });
        }
    }
}
