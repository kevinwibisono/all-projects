package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Adoption;
import my.istts.finalproject.models.AdoptionDBAccess;
import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AdoptionDetailViewModel {
    private AdoptionDBAccess adoptDB;
    private FavoritDBAccess favoritDB;
    private AkunDBAccess akunDB;
    private Storage storage;

    public AdoptionDetailViewModel(Application app){
        this.adoptDB = new AdoptionDBAccess();
        this.favoritDB = new FavoritDBAccess(app);
        this.akunDB = new AkunDBAccess(app);
        this.storage = new Storage();
    }

    private MutableLiveData<Boolean> editable = new MutableLiveData<>();

    private MutableLiveData<String> petId = new MutableLiveData<>("");
    private MutableLiveData<String> petOwner = new MutableLiveData<>("");
    private MutableLiveData<String> petName = new MutableLiveData<>("");
    private MutableLiveData<String> petAge = new MutableLiveData<>("");
    private MutableLiveData<String> petBreed = new MutableLiveData<>("");
    private MutableLiveData<String> petGender = new MutableLiveData<>("");
    private MutableLiveData<String> petDesc = new MutableLiveData<>("");
    private MutableLiveData<String> petPicture = new MutableLiveData<>();

    private Favorit favorit;
    private String email;
    private Adoption adoption;
    private MutableLiveData<Boolean> favoritEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> favorited = new MutableLiveData<>();

    public void getPetDetails(String id_pet){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    adoptDB.getPetAdoptById(id_pet).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Adoption adopt = new Adoption(documentSnapshot);
                            adoption = adopt;

                            petId.setValue(adopt.getId_pet());
                            petOwner.setValue(adoption.getEmail_pemilik());
                            petName.setValue(adoption.getNama());
                            petAge.setValue(adoption.getUmur()+" "+adoption.getSatuanUmurStr());
                            petBreed.setValue(adoption.getJenisHewan()+" "+adoption.getRas());
                            petGender.setValue(adoption.getJenisKelaminHewan());
                            petDesc.setValue(adoption.getDeskripsi());

                            storage.getPictureUrlFromName(adopt.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    petPicture.setValue(uri.toString());
                                }
                            });

                            if(email.equals(adoption.getEmail_pemilik())){
                                editable.setValue(true);
                            }
                            else{
                                editable.setValue(false);
                            }

                            favoritEnabled.setValue(false);
                            favoritDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
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

                            favoritDB.getFavorit(email, adoption.getId_pet());
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();

    }

    public void setFavorite(){
        favoritEnabled.setValue(false);
        if(favorit != null){
            //skrg di favoritkan
            favoritDB.setFavoriteDeletedListener(new FavoritDBAccess.onFavoriteDeleted() {
                @Override
                public void onDeleted() {
                    favoritEnabled.setValue(true);
                    favorited.setValue(false);
                    favorit = null;
                }
            });

            favoritDB.deleteFavorite(favorit.getId());
        }
        else{
            //skrg difavoritkan
            //untuk menghindari kemungkinan addfavorit double, maka sebelum add cek apakah sudah ada di db
            favoritDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                @Override
                public void onGot(List<Favorit> favs) {
                    if(favs.size() > 0){
                        favoritEnabled.setValue(true);
                        favorited.setValue(true);
                        favorit = favs.get(0);
                    }
                    else{
                        favorit = new Favorit(email, adoption.getId_pet(), 2);
                        favoritDB.setFavoriteAddedListener(new FavoritDBAccess.onFavoriteAdded() {
                            @Override
                            public void onAdded() {
                                favoritDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                                    @Override
                                    public void onGot(List<Favorit> favs) {
                                        if(favs.size() > 0){
                                            favorit = favs.get(0);
                                            favoritEnabled.setValue(true);
                                            favorited.setValue(true);
                                        }
                                    }
                                });

                                favoritDB.getFavorit(email, adoption.getId_pet());
                            }
                        });

                        favoritDB.addFavorit(favorit);
                    }
                }
            });

            favoritDB.getFavorit(email, adoption.getId_pet());
        }
    }

    public LiveData<Boolean> isEditable() {
        return editable;
    }

    public LiveData<String> getPetId() {
        return petId;
    }

    public LiveData<String> getPetOwner() {
        return petOwner;
    }

    public LiveData<String> getPetName() {
        return petName;
    }

    public LiveData<String> getPetAge() {
        return petAge;
    }

    public LiveData<String> getPetBreed() {
        return petBreed;
    }

    public LiveData<String> getPetGender() {
        return petGender;
    }

    public LiveData<String> getPetDesc() {
        return petDesc;
    }

    public LiveData<String> getPicture(){
        return petPicture;
    }

    public LiveData<Boolean> isFavoriteEnabled(){
        return favoritEnabled;
    }

    public LiveData<Boolean> isFavorited(){
        return favorited;
    }
}
