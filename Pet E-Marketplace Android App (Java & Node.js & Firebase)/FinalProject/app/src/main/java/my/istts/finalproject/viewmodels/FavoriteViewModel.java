package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Adoption;
import my.istts.finalproject.models.AdoptionDBAccess;
import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.AdoptionItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteViewModel {
    private FavoritDBAccess favDB;
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;
    private AdoptionDBAccess adoptDB;
    private AkunDBAccess akunDB;
    private Application app;
    
    public FavoriteViewModel(Application app){
        this.app = app;
        favDB = new FavoritDBAccess(app);
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        adoptDB = new AdoptionDBAccess();
        akunDB = new AkunDBAccess(app);
    }
    
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Integer> itemCount = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProductItemViewModel>> favProducts = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> favHotels = new MutableLiveData<>();
    private MutableLiveData<ArrayList<AdoptionItemViewModel>> favPetAdopt = new MutableLiveData<>();

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Integer> getItemCount() {
        return itemCount;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getFavProducts() {
        return favProducts;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getFavHotels() {
        return favHotels;
    }

    public LiveData<ArrayList<AdoptionItemViewModel>> getFavPetAdopt() {
        return favPetAdopt;
    }

    public void getFavorites(int tipe){
        loading.setValue(true);
        favProducts.setValue(new ArrayList<>());
        favHotels.setValue(new ArrayList<>());
        favPetAdopt.setValue(new ArrayList<>());
        itemCount.setValue(0);
        
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){

                    favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                        @Override
                        public void onGot(List<Favorit> favs) {
                            if(tipe == 0){
                                getProductsFromFavs(favs, accountsGot.get(0).getEmail());
                            }
                            else if(tipe == 1){
                                getHotelsFromFav(favs, accountsGot.get(0).getEmail());
                            }
                            else{
                                getPetAdoptsFromFav(favs, accountsGot.get(0).getEmail());
                            }
                        }
                    });

                    favDB.getFavoritTipe(tipe);
                }
            }
        });
        
        akunDB.getSavedAccounts();
        
        
    }
    
    private void getProductsFromFavs(List<Favorit> favs, String email){
        ArrayList<Integer> counterDone = new ArrayList<>();

        if(favs.size() > 0){
            for (Favorit fav:
                    favs) {
                productDB.getProductById(fav.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getData() != null){
                            Product product = new Product(documentSnapshot);
                            addProductToVM(product, email);
                        }
                        counterDone.add(0);
                        if(counterDone.size() == favs.size()){
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
    
    private void addProductToVM(Product product, String email){
        Integer counterNow = itemCount.getValue();
        counterNow++;
        itemCount.setValue(counterNow);

        ArrayList<ProductItemViewModel> currentFav = favProducts.getValue();
        currentFav.add(new ProductItemViewModel(product, email, app));
        favProducts.setValue(currentFav);
    }

    private void getHotelsFromFav(List<Favorit> favs, String email){
        ArrayList<Integer> counterDone = new ArrayList<>();

        if(favs.size() > 0){
            for (Favorit fav:
                    favs) {
                hotelDB.getRoomById(fav.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getData() != null){
                            Hotel hotel = new Hotel(documentSnapshot);
                            addHotelToVM(hotel, email);
                        }
                        counterDone.add(0);
                        if(counterDone.size() == favs.size()){
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

    private void addHotelToVM(Hotel hotel, String email){
        Integer counterNow = itemCount.getValue();
        counterNow++;
        itemCount.setValue(counterNow);

        ArrayList<HotelItemViewModel> currentFav = favHotels.getValue();
        currentFav.add(new HotelItemViewModel(hotel, email, app));
        favHotels.setValue(currentFav);
    }

    private void getPetAdoptsFromFav(List<Favorit> favs, String email){
        ArrayList<Integer> counterDone = new ArrayList<>();

        if(favs.size() > 0){
            for (Favorit fav:
                    favs) {
                adoptDB.getPetAdoptById(fav.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getData() != null){
                            Adoption adoption = new Adoption(documentSnapshot);
                            addPetAdoptToVM(adoption, email);
                        }
                        counterDone.add(0);
                        if(counterDone.size() == favs.size()){
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

    private void addPetAdoptToVM(Adoption adoption, String email){
        Integer counterNow = itemCount.getValue();
        counterNow++;
        itemCount.setValue(counterNow);

        ArrayList<AdoptionItemViewModel> currentFav = favPetAdopt.getValue();
        currentFav.add(new AdoptionItemViewModel(adoption, email, app));
        favPetAdopt.setValue(currentFav);
    }
}
