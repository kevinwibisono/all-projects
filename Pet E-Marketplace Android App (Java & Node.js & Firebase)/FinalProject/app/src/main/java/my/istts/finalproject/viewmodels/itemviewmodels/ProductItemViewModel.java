package my.istts.finalproject.viewmodels.itemviewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ProductItemViewModel {
    private Product produk;
    private String email;
    private Favorit favorit;
    private FavoritDBAccess favDB;

    private MutableLiveData<String> skorString = new MutableLiveData<>();
    private MutableLiveData<String> reviews = new MutableLiveData<>();
    private MutableLiveData<String> picture = new MutableLiveData<>();
    private MutableLiveData<Boolean> favoritEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> favorited = new MutableLiveData<>();

    public LiveData<String> getSkor(){
        return skorString;
    }

    public LiveData<String> getReviews(){
        return reviews;
    }

    public LiveData<String> getPicture(){
        return picture;
    }

    public LiveData<Boolean> isFavoriteEnabled(){
        return favoritEnabled;
    }

    public LiveData<Boolean> isFavorited(){
        return favorited;
    }

    public ProductItemViewModel(Product produk) {
        this.produk = produk;

        Storage storage = new Storage();
        storage.getPictureUrlFromName(produk.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                picture.setValue(uri.toString());
            }
        });
    }

    public ProductItemViewModel(Product produk, String email, Application app) {
        this.produk = produk;
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

        favDB.getFavorit(email, produk.getId_produk());

        Storage storage = new Storage();
        storage.getPictureUrlFromName(produk.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                picture.setValue(uri.toString());
            }
        });

        ReviewDBAccess reviewDB = new ReviewDBAccess();
        reviewDB.getAllReviewsOfItem(produk.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    int jumlahReviews = queryDocumentSnapshots.getDocuments().size();
                    float skorTotal = 0;
                    for (int i = 0; i < jumlahReviews; i++) {
                        DocumentSnapshot reviewDoc = queryDocumentSnapshots.getDocuments().get(i);
                        skorTotal += reviewDoc.getLong("nilai").floatValue();
                    }
                    float skor = skorTotal/jumlahReviews;
                    skorString.setValue(String.valueOf(skor).substring(0, 3));
                    reviews.setValue(String.valueOf(jumlahReviews));
                }
            }
        });
    }

    public String getId(){
        return produk.getId_produk();
    }

    public String getName(){
        return produk.getNama();
    }

    public String getPrice(){
        return produk.getTSHarga();
    }

    public int getStok(){
        return produk.getStok();
    }

    public void setFavorite(){
        favoritEnabled.setValue(false);
        if(favorit != null){
            //skrg di favoritkan
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
                        favorit = new Favorit(email, produk.getId_produk(), 0);
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

                                favDB.getFavorit(email, produk.getId_produk());
                            }
                        });

                        favDB.addFavorit(favorit);
                    }
                }
            });

            favDB.getFavorit(email, produk.getId_produk());
        }
    }
}
