package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.DetailPenjual;
import my.istts.finalproject.models.PaketGrooming;
import my.istts.finalproject.models.PaketGroomingDBAccess;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class GroomerItemViewModel {
    private DetailPenjual groomerDetail;
    private String email;
    private String nama;

    public GroomerItemViewModel(DetailPenjual groomerDetail, String email, String nama) {
        this.groomerDetail = groomerDetail;
        this.email = email;
        this.nama = nama;

        Storage storage = new Storage();
        storage.getPictureUrlFromName(email).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                groomerPic.setValue(uri.toString());
            }
        });

        PaketGroomingDBAccess paketDB = new PaketGroomingDBAccess();
        paketDB.getPackages(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int max = 0, min = 999999999;
                for (DocumentSnapshot packDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    PaketGrooming paketGrooming = new PaketGrooming(packDoc);
                    if(paketGrooming.getHarga() < min){
                        min = paketGrooming.getHarga();
                    }
                    if(paketGrooming.getHarga() > max){
                        max = paketGrooming.getHarga();
                    }
                }
                groomerPriceMin.setValue(min);
                groomerPriceMax.setValue(max);
            }
        });

        ReviewDBAccess reviewDB = new ReviewDBAccess();
        reviewDB.getAllReviewsOfItem(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int jumlahReview = queryDocumentSnapshots.getDocuments().size();
                float skorNow = 0;
                for (DocumentSnapshot reviewDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    skorNow += reviewDoc.getLong("nilai");
                }
                skorNow = skorNow/jumlahReview;
                groomerScore.setValue(String.valueOf(skorNow).substring(0, 3));
                groomerReviews.setValue(queryDocumentSnapshots.getDocuments().size());
            }
        });
    }

    private MutableLiveData<String> groomerPic = new MutableLiveData<>("");
    private MutableLiveData<String> groomerScore = new MutableLiveData<>("");
    private MutableLiveData<Integer> groomerPriceMin = new MutableLiveData<>();
    private MutableLiveData<Integer> groomerPriceMax = new MutableLiveData<>();
    private MutableLiveData<Integer> groomerReviews = new MutableLiveData<>(0);

    public LiveData<String> getGroomerPic(){
        return groomerPic;
    }

    public LiveData<String> getGroomerScore(){
        return groomerScore;
    }

    public LiveData<Integer> getGroomerPriceMin(){
        return groomerPriceMin;
    }

    public LiveData<Integer> getGroomerPriceMax(){
        return groomerPriceMax;
    }

    public LiveData<Integer> getGroomerReviews(){
        return groomerReviews;
    }

    public String getEmailGroomer(){
        return email;
    }

    public String getGroomerName(){
        return nama;
    }

    public String getGroomerDesc(){
        return groomerDetail.getDeskripsi();
    }
}
