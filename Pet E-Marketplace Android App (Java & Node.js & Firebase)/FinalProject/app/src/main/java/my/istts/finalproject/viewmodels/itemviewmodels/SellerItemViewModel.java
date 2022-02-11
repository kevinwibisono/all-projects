package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.DetailPenjual;
import my.istts.finalproject.models.DetailPenjualDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Review;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SellerItemViewModel {
    private String email;
    private String name;
    private ReviewDBAccess reviewDBAccess;

    public SellerItemViewModel(String email, String name) {
        this.email = email;
        this.name = name;
        reviewDBAccess = new ReviewDBAccess();

        Storage storage = new Storage();
        storage.getPictureUrlFromName(email).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                sellerPic.setValue(uri.toString());
            }
        });

        DetailPenjualDBAccess detailDB = new DetailPenjualDBAccess();
        detailDB.getDBAkunDetail(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DetailPenjual detail = new DetailPenjual(documentSnapshot);
                if(detail.getRole() == 0){
                    //shopping
                    countScoreShop(email);
                }
                else if(detail.getRole() == 1 || detail.getRole() == 3){
                    countScoreGroomerClinic(email);
                }
                else if(detail.getRole() == 2){
                    countScoreHotel(email);
                }
            }
        });
    }

    private void countScoreShop(String email_seller){
        ProductDBAccess productDBAccess = new ProductDBAccess();
        productDBAccess.getAllSellerProducts(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    for (DocumentSnapshot productDoc:
                            queryDocumentSnapshots) {
                        Product product = new Product(productDoc);
                        reviewDBAccess.getAllReviewsOfItem(product.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    int jumlahReview = queryDocumentSnapshots.getDocuments().size();
                                    float skorCtr = 0;
                                    for (DocumentSnapshot doc:
                                            queryDocumentSnapshots.getDocuments()) {
                                        Review review = new Review(doc);
                                        skorCtr += review.getNilai();
                                    }
                                    addScoreAvg(skorCtr/jumlahReview, jumlahReview);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void countScoreHotel(String email_seller){
        HotelDBAccess hotelDBAccess = new HotelDBAccess();
        hotelDBAccess.getAllHotelsOwned(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    for (DocumentSnapshot hotelDoc:
                            queryDocumentSnapshots) {
                        Hotel hotel = new Hotel(hotelDoc);
                        reviewDBAccess.getAllReviewsOfItem(hotel.getId_kamar()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    int jumlahReview = queryDocumentSnapshots.getDocuments().size();
                                    float skorCtr = 0;
                                    for (DocumentSnapshot doc:
                                            queryDocumentSnapshots.getDocuments()) {
                                        Review review = new Review(doc);
                                        skorCtr += review.getNilai();
                                    }
                                    addScoreAvg(skorCtr/jumlahReview, jumlahReview);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void addScoreAvg(float skor, int jumlah){
        Float scoreNow = sellerScore.getValue();
        Integer revsNow = sellerReviews.getValue();
        if(scoreNow == 0.0){
            scoreNow = skor;
        }
        scoreNow = (scoreNow + skor)/2;
        revsNow += jumlah;
        sellerScore.setValue(scoreNow);
        sellerReviews.setValue(revsNow);
    }

    public void countScoreGroomerClinic(String email_seller){
        reviewDBAccess.getAllReviewsOfItem(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int jumlahRev = queryDocumentSnapshots.getDocuments().size();
                float skorNow = 0;
                for (DocumentSnapshot doc:
                        queryDocumentSnapshots.getDocuments()) {
                    skorNow += doc.getLong("nilai");
                }
                sellerScore.setValue(skorNow/jumlahRev);
                sellerReviews.setValue(jumlahRev);
            }
        });
    }

    private MutableLiveData<String> sellerPic = new MutableLiveData<>("");
    private MutableLiveData<Float> sellerScore = new MutableLiveData<>((float) 0.0);
    private MutableLiveData<Integer> sellerReviews = new MutableLiveData<>(0);

    public LiveData<String> getSellerPic(){
        return sellerPic;
    }

    public LiveData<Integer> getSellerReviews() {
        return sellerReviews;
    }

    public LiveData<Float> getSellerScore() {
        return sellerScore;
    }

    public String getSellerName(){
        return name;
    }

    public String getSellerEmail(){
        return email;
    }


}
