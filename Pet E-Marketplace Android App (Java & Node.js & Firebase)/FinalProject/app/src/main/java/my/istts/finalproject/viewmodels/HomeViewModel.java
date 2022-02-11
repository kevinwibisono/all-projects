package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Artikel;
import my.istts.finalproject.models.ArtikelDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.ArticleItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel {
    private AkunDBAccess akunDB;
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;
    private ArtikelDBAccess artikelDB;
    private Application app;

    public HomeViewModel(Application app){
        this.app = app;
        akunDB = new AkunDBAccess(app);
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        artikelDB = new ArtikelDBAccess();
    }

    private String email;
    private MutableLiveData<ArrayList<ProductItemViewModel>> productVMs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> hotelVMs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ArticleItemViewModel>> articlesVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> productLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> hotelLoading = new MutableLiveData<>();

    public LiveData<ArrayList<ProductItemViewModel>> getProductsVMs(){
        return productVMs;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getHotelVMs(){
        return hotelVMs;
    }

    public LiveData<ArrayList<ArticleItemViewModel>> getArticleVMs(){
        return articlesVMs;
    }

    public LiveData<Boolean> isProductLoading(){
        return productLoading;
    }

    public LiveData<Boolean> isHotelLoading(){
        return hotelLoading;
    }

    public void getRecommendeds(){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();
                    getRecHotels();
                    getRecProducts();
                    getRecArticles();
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void getRecProducts(){
        productLoading.setValue(true);
        productVMs.setValue(new ArrayList<>());
        productDB.getTenProducts().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Product> products = new ArrayList<>();
                    for (DocumentSnapshot prodDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        products.add(new Product(prodDoc));
                    }
                    addProductsToVM(products);
                }
                else{
                    productLoading.setValue(false);
                }
            }
        });
    }

    private void addProductsToVM(ArrayList<Product> products){
        ArrayList<ProductItemViewModel> currentProdVMs = productVMs.getValue();
        for (Product product : products) {
            currentProdVMs.add(new ProductItemViewModel(product, email, app));
        }
        productVMs.setValue(currentProdVMs);
        productLoading.setValue(false);
    }

    private void getRecHotels(){
        hotelLoading.setValue(true);
        hotelVMs.setValue(new ArrayList<>());
        hotelDB.getTenHotels().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Hotel> hotels = new ArrayList<>();
                    for (DocumentSnapshot hotelDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        hotels.add(new Hotel(hotelDoc));
                    }
                    addNewHotelVM(hotels);
                }
                else{
                    hotelLoading.setValue(false);
                }
            }
        });
    }

    private void addNewHotelVM(ArrayList<Hotel> hotels){
        ArrayList<HotelItemViewModel> currentHotelVMs = hotelVMs.getValue();
        for (Hotel hotel:
             hotels) {
            currentHotelVMs.add(new HotelItemViewModel(hotel, email, app));
        }
        hotelVMs.setValue(currentHotelVMs);
        hotelLoading.setValue(false);
    }

    private void getRecArticles(){
        articlesVMs.setValue(new ArrayList<>());

        artikelDB.getLimitedArticles().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    for (DocumentSnapshot articleDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        addArticleToVM(new Artikel(articleDoc));
                    }
                }
            }
        });
    }

    private void addArticleToVM(Artikel artikel){
        ArrayList<ArticleItemViewModel> currentArticles = articlesVMs.getValue();
        currentArticles.add(new ArticleItemViewModel(artikel));
        articlesVMs.setValue(currentArticles);
    }
}
