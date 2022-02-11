package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListViewModel {
    private ProductDBAccess productDB;
    private AkunDBAccess akunDB;
    private CartDBAccess cartDB;
    private Application app;

    public ProductListViewModel(Application app){
        this.app = app;
        productDB = new ProductDBAccess();
        akunDB = new AkunDBAccess(app);
        cartDB = new CartDBAccess(app);
    }

    private MutableLiveData<Boolean> itemsInCartAppo = new MutableLiveData<>();

    private String[] kategoriList = {"Makanan Anjing", "Makanan Kucing", "Makanan Kelinci", "Makanan Burung", "Makanan Ikan", "Makanan Hamster",
            "Makanan Reptil", "Pakan Ternak", "Peralatan Grooming", "Leash dan Handler", "Treats/Snack (Kudapan)", "Peralatan Kesehatan",
            "Mainan/Alat Ketangkasan", "Peralatan Kebersihan", "Kandang/Tempat Tidur"};
    private MutableLiveData<ArrayList<String>> activeCategories = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> activeCategoriesInIndex = new MutableLiveData<>();
    public MutableLiveData<boolean[]> chipCategoriesChecked = new MutableLiveData<>(new boolean[15]);

    private String[] sortTypes = {"ulasan", "harga", "harga"};
    private String[] sortNames = {"Ulasan", "Harga Terendah", "Harga Tertinggi"};
    private Query.Direction[] sortDirections = {Query.Direction.DESCENDING, Query.Direction.ASCENDING, Query.Direction.DESCENDING};
    private String fieldSorted = "";
    private Query.Direction directionSort = Query.Direction.ASCENDING;
    private MutableLiveData<String> activeSort = new MutableLiveData<>("");
    public MutableLiveData<boolean[]> chipSortChecked = new MutableLiveData<>(new boolean[3]);
    
//    public MutableLiveData<String> priceMin = new MutableLiveData<>("");
//    public MutableLiveData<String> priceMax = new MutableLiveData<>("");

    private String searchKeyword = "";

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProductItemViewModel>> productsVMs = new MutableLiveData<>();

    public void initFilters(){
        activeCategories.setValue(new ArrayList<>());
        activeCategoriesInIndex.setValue(new ArrayList<>());

        boolean[] initCategories = new boolean[15];
        boolean[] initSorts = new boolean[4];
        for (int i = 0; i < 15; i++) {
            if(i < 3){
                initSorts[i] = false;
            }
            initCategories[i] = false;
        }
        chipCategoriesChecked.setValue(initCategories);
        chipSortChecked.setValue(initSorts);
    }

    public void getProductsFiltered(){
        loading.setValue(true);
        productsVMs.setValue(new ArrayList<>());

        checkCart();

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    productDB.getProductsFiltered(fieldSorted, directionSort, activeCategoriesInIndex.getValue()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<Product> products = new ArrayList<>();
                                for (DocumentSnapshot productDoc:
                                        queryDocumentSnapshots.getDocuments()) {
                                    Product product = new Product(productDoc);
                                    if(product.getNama().toLowerCase().contains(searchKeyword.toLowerCase())){
                                        products.add(product);
                                    }
                                }
                                addProductsToVM(products, accountsGot.get(0).getEmail());
                            }
                            else{
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

//    private boolean checkPriceRange(Product p){
//        boolean minValid = true;
//        boolean maxValid = true;
//        if(!priceMin.getValue().equals("")){
//            Integer min = Integer.parseInt(priceMin.getValue());
//            if(p.getHarga() < min){
//                minValid = false;
//            }
//        }
//        if(!priceMax.getValue().equals("")){
//            Integer max = Integer.parseInt(priceMax.getValue());
//            if(p.getHarga() > max){
//                maxValid = false;
//            }
//        }
//
//        if(minValid && maxValid){
//            return true;
//        }
//        else{
//            return false;
//        }
//    }

    private void addProductsToVM(ArrayList<Product> products, String no_hp){
        ArrayList<ProductItemViewModel> currentVMs = productsVMs.getValue();
        for (Product product:
             products) {
            currentVMs.add(new ProductItemViewModel(product, no_hp, app));
        }
        productsVMs.setValue(currentVMs);
        loading.setValue(false);
    }

    private void checkCart(){
        cartDB.setItemsGotListener(new CartDBAccess.onCartItemsGot() {
            @Override
            public void onGotItems(List<Cart> items) {
                if(items.size() > 0){
                    itemsInCartAppo.setValue(true);
                }
            }
        });

        cartDB.getAllCartItemsByType(0);
    }

    public void setFilters(){
        //untuk tampilan quick filter
        activeCategories.setValue(new ArrayList<>());
        activeSort.setValue("");

        //untuk keperluan query db
        activeCategoriesInIndex.setValue(new ArrayList<>());
        fieldSorted = "";
        directionSort = Query.Direction.ASCENDING;

        for (int i = 0; i < 15; i++) {
            if(i < 3 && chipSortChecked.getValue()[i]){
                fieldSorted = sortTypes[i];
                directionSort = sortDirections[i];
                activeSort.setValue(sortNames[i]);
            }
            if(chipCategoriesChecked.getValue()[i]){
                addCheckedCategories(i);
            }
        }

        getProductsFiltered();
    }

    public void setCategory(int kategori){
        initFilters();

        ArrayList<String> currentCategories = activeCategories.getValue();
        ArrayList<Integer> currentCategoriesIndexes = activeCategoriesInIndex.getValue();
        currentCategories.add(kategoriList[kategori]);
        currentCategoriesIndexes.add(kategori);
        activeCategories.setValue(currentCategories);
        activeCategoriesInIndex.setValue(currentCategoriesIndexes);

        boolean[] currentChecked = chipCategoriesChecked.getValue();
        currentChecked[kategori] = true;
        chipCategoriesChecked.setValue(currentChecked);

        getProductsFiltered();
    }

    public void setSearchKeyword(String searchKeyword){
        initFilters();
        this.searchKeyword = searchKeyword;

        getProductsFiltered();
    }

    private void addCheckedCategories(int index){
        ArrayList<String> currentCategories = activeCategories.getValue();
        ArrayList<Integer> currentCategoriesIndexes = activeCategoriesInIndex.getValue();
        currentCategories.add(kategoriList[index]);
        currentCategoriesIndexes.add(index);
        activeCategories.setValue(currentCategories);
        activeCategoriesInIndex.setValue(currentCategoriesIndexes);
    }

    public void removeSort(){
        activeSort.setValue("");
        fieldSorted = "";
        directionSort = Query.Direction.ASCENDING;

        boolean[] sortChecked = new boolean[4];
        for (int i = 0; i < 3; i++) {
            sortChecked[i] = false;
        }
        chipSortChecked.setValue(sortChecked);

        getProductsFiltered();
    }

    public void removeCategories(int position){
        ArrayList<String> currentCategories = activeCategories.getValue();
        ArrayList<Integer> currentCategoriesIndexes = activeCategoriesInIndex.getValue();
        int kategoriNum = currentCategoriesIndexes.get(position);
        currentCategories.remove(position);
        currentCategoriesIndexes.remove(position);
        activeCategories.setValue(currentCategories);
        activeCategoriesInIndex.setValue(currentCategoriesIndexes);

        boolean[] categories = chipCategoriesChecked.getValue();
        categories[kategoriNum] = false;
        chipCategoriesChecked.setValue(categories);

        getProductsFiltered();
    }


    public LiveData<ArrayList<String>> getActiveCategories() {
        return activeCategories;
    }

    public LiveData<String> getActiveSort() {
        return activeSort;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> isItemsInCart() {
        return itemsInCartAppo;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getProducts() {
        return productsVMs;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }
}
