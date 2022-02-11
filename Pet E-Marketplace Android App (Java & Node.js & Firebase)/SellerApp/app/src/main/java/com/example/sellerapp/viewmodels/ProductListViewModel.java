package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListViewModel {

    private AkunDBAccess akunDB;
    private ProductDBAccess prodDB;

    public ProductListViewModel(Application application) {
        akunDB = new AkunDBAccess(application);
        prodDB = new ProductDBAccess();
        initFilter(filterChipsCheckedActive);
        initFilter(filterChipsCheckedKat);
        initFilter(filterChipsCheckedSort);
        boolean[] filterSort = filterChipsCheckedSort.getValue();
        filterSort[0] = true;
        filterChipsCheckedSort.setValue(filterSort);
        fieldToSort = "tanggal_upload";
        sortDirection = Query.Direction.DESCENDING;
        String[] chipsFilterText = {"", "Terbaru"};
        activeFilters.setValue(chipsFilterText);
    }

    //LIST PRODUCT
    private String fieldToSort;
    private Query.Direction sortDirection;
    private ArrayList<Integer> kategori = new ArrayList<>();
    private Boolean statusFilter;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String[]> activeFilters = new MutableLiveData<>(new String[]{"", ""});
    private MutableLiveData<ArrayList<String>> categoriesActive;
    public MutableLiveData<boolean[]> filterChipsCheckedActive = new MutableLiveData<>(new boolean[2]);
    public MutableLiveData<boolean[]> filterChipsCheckedKat = new MutableLiveData<>(new boolean[15]);
    public MutableLiveData<boolean[]> filterChipsCheckedSort = new MutableLiveData<>(new boolean[6]);
    private MutableLiveData<ArrayList<ProductItemViewModel>> productVMS;
    public MutableLiveData<String> productName = new MutableLiveData<>("");

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<String[]> getActiveFilters(){
        return activeFilters;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getProductVMS(){
        if(productVMS == null){
            productVMS = new MutableLiveData<>(new ArrayList<>());
        }

        return productVMS;
    }

    public LiveData<ArrayList<String>> getCategoriesActive(){
        if(categoriesActive == null){
            categoriesActive = new MutableLiveData<>(new ArrayList<>());
        }

        return categoriesActive;
    }

    public void getProducts(){
        loading.setValue(true);
        productVMS.setValue(new ArrayList<>());
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    prodDB.getProducts(accountsGot.get(0).getEmail(), fieldToSort, sortDirection, statusFilter).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            if(results.size() > 0){
                                ArrayList<Product> dbProducts = new ArrayList<>();
                                for (int i = 0; i < results.size(); i++){
                                    Product produk = new Product(results.get(i));
                                    //setelah dapat list produk, lakukan operasi LIKE dengan keyword
                                    if(produk.getNama().toLowerCase().contains(productName.getValue().toLowerCase())){
                                        //dan cek dulu dengan list kategori yang telah dipilih
                                        if(kategori.size() <= 0 || categoriesFulfilled(produk.getKategori())){
                                            dbProducts.add(produk);
                                        }
                                    }
                                }
                                addNewProductVM(dbProducts);
                            }
                            else{
                                loading.setValue(false);
                            }
                            productName.setValue("");
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private boolean categoriesFulfilled(int produkCat){
        for (int kat:
             kategori) {
            if(kat == produkCat){
                return true;
            }
        }
        return false;
    }

    public void changeFilterSort(){
        //untuk keperluan chip filter, menampilkan filter-filter yang sedang aktif
        categoriesActive.setValue(new ArrayList<>());
        String[] chipsFilterText = {"", ""};
        String[] kategoriList = {"Makanan Anjing", "Makanan Kucing", "Makanan Kelinci", "Makanan Burung", "Makanan Ikan", "Makanan Hamster", "Makanan Reptil", "Pakan Ternak", "Peralatan Grooming", "Leash dan Handler", "Treats/Snack (Kudapan)", "Peralatan Kesehatan", "Mainan/Alat Ketangkasan", "Peralatan Kebersihan", "Kandang/Tempat Tidur"};
        String[] sortFieldsText = {"Terbaru", "Terdahulu", "Paling Diminati", "Kurang Diminati", "Harga Tertinggi", "Harga Terendah"};

        statusFilter = null;
        kategori = new ArrayList<>();
        fieldToSort = null;
        sortDirection = null;
        String[] sortFields = {"tanggal_upload", "tanggal_upload", "terjual", "terjual", "harga", "harga"};
        Query.Direction[] sortDirections = {Query.Direction.DESCENDING, Query.Direction.ASCENDING, Query.Direction.DESCENDING, Query.Direction.ASCENDING, Query.Direction.DESCENDING, Query.Direction.ASCENDING};

        if(filterChipsCheckedActive.getValue()[0]){
            statusFilter = true;
            chipsFilterText[0] = "Aktif";
        }
        else if(filterChipsCheckedActive.getValue()[1]){
            statusFilter = false;
            chipsFilterText[0] = "Nonaktif";
        }

        for (int i=0;i<15;i++){
            if(filterChipsCheckedKat.getValue()[i]){
                //agar bisa or, maka firebase membutuhkan sebuah array untuk dibandingkan di query
                kategori.add(i);
                addCategoryToLiveData(kategoriList[i]);
            }
        }

        for (int i=0;i<6;i++){
            if(filterChipsCheckedSort.getValue()[i]){
                fieldToSort = sortFields[i];
                sortDirection = sortDirections[i];
                chipsFilterText[1] = sortFieldsText[i];
            }
        }

        setChipFilter(chipsFilterText);
        getProducts();
    }

    public void deleteFilter(int index){
        //ubah text dari chip filter yang dibuang (ditekan close iconnya)
        String[] currentFilterTexts = activeFilters.getValue();
        currentFilterTexts[index] = "";
        activeFilters.setValue(currentFilterTexts);

        //setelah itu hapus isi masing-masing filter
        //dan hilangkan centang di bottom dialog filter products
        if(index == 0){
            statusFilter = null;
            boolean[] filterStatus = {false, false};
            filterChipsCheckedActive.setValue(filterStatus);
        }
        else if(index == 1){
            fieldToSort = null;
            sortDirection = null;
            boolean[] filterSort = {false, false, false, false, false, false};
            filterChipsCheckedSort.setValue(filterSort);
        }

        getProducts();
    }

    public void deleteCategoryFilter(int index){
        //cari dulu mana boolean checked untuk bottom dialog fragment bagian kategori yang perlu dimatikan centangnya
        boolean[] currentFilterCategories = filterChipsCheckedKat.getValue();
        currentFilterCategories[kategori.get(index)] = false;
        filterChipsCheckedKat.setValue(currentFilterCategories);

        kategori.remove(index);
        ArrayList<String> categoriesHolder = categoriesActive.getValue();
        categoriesHolder.remove(index);
        categoriesActive.setValue(categoriesHolder);
    }

    private void addCategoryToLiveData(String categoryText){
        ArrayList<String> categoriesHolder = categoriesActive.getValue();
        categoriesHolder.add(categoryText);
        categoriesActive.setValue(categoriesHolder);
    }

    private void setChipFilter(String[] filterText){
        //set text dari chip filter berdasarkan pilihan user di bottom dialog fragment
        activeFilters.setValue(filterText);
    }

    private void initFilter(MutableLiveData<boolean[]> filters){
        for (int i=0;i<filters.getValue().length;i++){
            filters.getValue()[i] = false;
        }
    }

    private void addNewProductVM(ArrayList<Product> products){
        ArrayList<ProductItemViewModel> holder = productVMS.getValue();
        for (Product p:
             products) {
            holder.add(new ProductItemViewModel(p));
        }
        productVMS.setValue(holder);
        loading.setValue(false);
    }
}
