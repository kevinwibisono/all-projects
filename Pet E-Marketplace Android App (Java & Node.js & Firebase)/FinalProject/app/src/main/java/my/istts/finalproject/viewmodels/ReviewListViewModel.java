package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Review;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.ReviewItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReviewListViewModel {
    private ReviewDBAccess reviewDB;
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;
    private AkunDBAccess akunDB;
    private Storage storage;
    private Application app;

    public ReviewListViewModel(Application app){
        this.app = app;
        reviewDB = new ReviewDBAccess();
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        akunDB = new AkunDBAccess();
        storage = new Storage();
    }

    private String id_item;
    private int[] scoreList = {0, 5, 4, 3, 2, 1};
    private MutableLiveData<String> targetPicture = new MutableLiveData<>("");
    private MutableLiveData<String> targetName = new MutableLiveData<>("");
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ReviewItemViewModel>> reviewsVMs = new MutableLiveData<>();

    public LiveData<String> getTargetName() {
        return targetName;
    }

    public LiveData<String> getTargetPicture() {
        return targetPicture;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<ArrayList<ReviewItemViewModel>> getReviewsVMs() {
        return reviewsVMs;
    }

    public void setItem(String id_no, int tipe){
        this.id_item = id_no;
        if(tipe == 0){
            //produk
            productDB.getProductById(id_no).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        Product product = new Product(documentSnapshot);
                        targetName.setValue(product.getNama());
                        storage.getPictureUrlFromName(product.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                targetPicture.setValue(uri.toString());
                            }
                        });
                    }
                }
            });
        }
        else if(tipe == 1){
            //hotel
            hotelDB.getRoomById(id_no).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        Hotel hotel = new Hotel(documentSnapshot);
                        targetName.setValue(hotel.getNama());
                        storage.getPictureUrlFromName(hotel.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                targetPicture.setValue(uri.toString());
                            }
                        });
                    }
                }
            });
        }
        else{
            //groomer/klinik
            akunDB.getAccByEmail(id_no).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        targetName.setValue(documentSnapshot.getString("nama"));
                        storage.getPictureUrlFromName(id_no).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                targetPicture.setValue(uri.toString());
                            }
                        });
                    }
                }
            });

        }
        getItemReviews(0);
    }

    public void getItemReviews(int skorIdx){
        int score = scoreList[skorIdx];
        loading.setValue(true);
        reviewsVMs.setValue(new ArrayList<>());
        reviewDB.getItemReviewsFiltered(id_item, score, 0).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Review> revArr = new ArrayList<>();
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        revArr.add(new Review(doc));
                    }
                    addReviewsVMs(revArr, null);
                }
                else{
                    loading.setValue(false);
                }
            }
        });
    }

    private void addReviewsVMs(ArrayList<Review> revArr, DocumentSnapshot last) {
        ArrayList<ReviewItemViewModel> current = reviewsVMs.getValue();
        for (Review rev:
             revArr) {
            current.add(new ReviewItemViewModel(rev, app));
        }
        reviewsVMs.setValue(current);
        loading.setValue(false);
    }
}
