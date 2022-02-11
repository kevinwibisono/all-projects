package my.istts.finalproject.viewmodels.itemviewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HotelItemViewModel {
    private Hotel hotel;
    private String email;
    private Favorit favorit;
    private FavoritDBAccess favDB;

    private MutableLiveData<String> ownerName = new MutableLiveData<>();
    private MutableLiveData<String> ownerPic = new MutableLiveData<>();
    private MutableLiveData<String> hotelPic = new MutableLiveData<>();
    private MutableLiveData<String> skorString = new MutableLiveData<>();
    private MutableLiveData<Integer> reviews = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> favoritEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> favorited = new MutableLiveData<>();

    public LiveData<String> getOwnerName(){
        return ownerName;
    }

    public LiveData<String> getOwnerPic(){
        return ownerPic;
    }

    public LiveData<String> getPicture(){
        return hotelPic;
    }

    public LiveData<String> getSkor(){
        return skorString;
    }

    public LiveData<Integer> getReviews(){
        return reviews;
    }

    public LiveData<Boolean> isFavoritEnabled(){
        return favoritEnabled;
    }

    public LiveData<Boolean> isFavorited(){
        return favorited;
    }

    public HotelItemViewModel(Hotel hotel) {
        this.hotel = hotel;

        Storage storage = new Storage();
        storage.getPictureUrlFromName(hotel.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hotelPic.setValue(uri.toString());
            }
        });
    }


    public HotelItemViewModel(Hotel hotel, String email, Application app) {
        this.hotel = hotel;
        this.email = email;

        favDB = new FavoritDBAccess(app);
        favoritEnabled.setValue(false);

        favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
            @Override
            public void onGot(List<Favorit> favs) {
                if(favs.size() > 0){
                    favorit = favs.get(0);
                    favorited.setValue(true);
                }
                else{
                    favorited.setValue(false);
                }
                favoritEnabled.setValue(true);
            }
        });

        favDB.getFavorit(email, hotel.getId_kamar());

        Storage storage = new Storage();

        AkunDBAccess akunDB = new AkunDBAccess();
        akunDB.getAccByEmail(hotel.getemail_pemilik()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    storage.getPictureUrlFromName(hotel.getemail_pemilik()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ownerPic.setValue(uri.toString());
                            ownerName.setValue(documentSnapshot.getString("nama"));
                        }
                    });
                }
            }
        });

        storage.getPictureUrlFromName(hotel.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hotelPic.setValue(uri.toString());
            }
        });

        ReviewDBAccess reviewDB = new ReviewDBAccess();
        reviewDB.getAllReviewsOfItem(hotel.getId_kamar()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    int jumlahReviews = queryDocumentSnapshots.getDocuments().size();
                    float skorTotal = 0;
                    for (int i = 0; i < jumlahReviews; i++) {
                        DocumentSnapshot reviewDoc = queryDocumentSnapshots.getDocuments().get(i);
                        skorTotal += reviewDoc.getLong("nilai");
                    }
                    float skor = skorTotal/jumlahReviews;
                    skorString.setValue(String.valueOf(skor).substring(0, 3));
                    reviews.setValue(jumlahReviews);
                }
            }
        });
    }

    public int getTotalKamar(){
        return hotel.getTotal();
    }

    public int getSedangDisewa(){
        return hotel.getSedang_disewa();
    }

    public boolean facilityIncluded(int facNum){
        if(hotel.getFasilitas().contains(facNum+"|") || hotel.getFasilitas().contains("|"+facNum)){
            return true;
        }
        else{
            return false;
        }
    }

    public String getHotelName(){
        return hotel.getNama();
    }

    public String getDescription(){
        return hotel.getDeskripsi();
    }

    public String getHarga(){
        return hotel.getTSHarga();
    }

    public String getId(){
        return hotel.getId_kamar();
    }

    public void setFavorite(){
        favoritEnabled.setValue(false);
        if(favorit != null){
            //skrg di nonfavoritkan
            favDB.setFavoriteDeletedListener(new FavoritDBAccess.onFavoriteDeleted() {
                @Override
                public void onDeleted() {
                    favoritEnabled.setValue(true);
                    favorited.setValue(false);
                    favorit = null;
                }
            });

            favDB.deleteFavorite(favorit.getId());
        }
        else{
            //skrg difavoritkan
            //untuk menghindari kemungkinan addfavorit double, maka sebelum add cek apakah sudah ada di db
            favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                @Override
                public void onGot(List<Favorit> favs) {
                    if(favs.size() > 0){
                        favoritEnabled.setValue(true);
                        favorited.setValue(true);
                        favorit = favs.get(0);
                    }
                    else{
                        favorit = new Favorit(email, hotel.getId_kamar(), 1);

                        favDB.setFavoriteAddedListener(new FavoritDBAccess.onFavoriteAdded() {
                            @Override
                            public void onAdded() {
                                favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                                    @Override
                                    public void onGot(List<Favorit> favs) {
                                        if(favs.size() > 0){
                                            favorit = favs.get(0);
                                            favoritEnabled.setValue(true);
                                            favorited.setValue(true);
                                        }
                                    }
                                });

                                favDB.getFavorit(email, hotel.getId_kamar());
                            }
                        });

                        favDB.addFavorit(favorit);
                    }
                }
            });

            favDB.getFavorit(email, hotel.getId_kamar());
        }
    }

}
