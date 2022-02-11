package my.istts.finalproject.viewmodels.itemviewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Adoption;
import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class AdoptionItemViewModel {
    private Adoption adoption;
    private String email;
    private Favorit favorit;
    private FavoritDBAccess favDB;

    public AdoptionItemViewModel(Adoption adoption, String email, Application app) {
        this.adoption = adoption;
        this.email = email;
        this.favDB = new FavoritDBAccess(app);

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

        favDB.getFavorit(email, adoption.getId_pet());

        Storage storage = new Storage();
        storage.getPictureUrlFromName(adoption.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                picture.setValue(uri.toString());
            }
        });
    }

    private MutableLiveData<String> picture = new MutableLiveData<>();
    private MutableLiveData<Boolean> favoritEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> favorited = new MutableLiveData<>();

    public LiveData<String> getPicture(){
        return picture;
    }

    public LiveData<Boolean> isFavoriteEnabled(){
        return favoritEnabled;
    }

    public LiveData<Boolean> isFavorited(){
        return favorited;
    }

    public String getNama(){
        return adoption.getNama();
    }

    public String getJenis(){
        return adoption.getJenisHewan()+" "+adoption.getJenisKelaminHewan();
    }

    public String getId(){
        return adoption.getId_pet();
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
                        favorit = new Favorit(email, adoption.getId_pet(), 2);
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

                                favDB.getFavorit(email, adoption.getId_pet());
                            }
                        });

                        favDB.addFavorit(favorit);
                    }
                }
            });

            favDB.getFavorit(email, adoption.getId_pet());
        }
    }
}
