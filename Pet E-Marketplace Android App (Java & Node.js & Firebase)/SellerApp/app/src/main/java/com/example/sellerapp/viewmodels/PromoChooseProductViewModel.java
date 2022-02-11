package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PromoChooseProductViewModel extends AndroidViewModel {

    private AkunDBAccess akunDB;
    private ProductDBAccess productDB;

    public PromoChooseProductViewModel(@NonNull Application application) {
        super(application);
        akunDB = new AkunDBAccess(application);
        productDB = new ProductDBAccess();
    }

    private MutableLiveData<Boolean> loading = new MutableLiveData<>(true);
    private MutableLiveData<ArrayList<ProductItemViewModel>> productVMs = new MutableLiveData<>();

    public LiveData<ArrayList<ProductItemViewModel>> getProductVMs(){
        return productVMs;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public ArrayList<String> getProductIncludes(){
        ArrayList<String> productIncludes = new ArrayList<>();
        for (ProductItemViewModel vm:
             productVMs.getValue()) {
            if(vm.included.getValue()){
                //produk dicentang, diinclude dalam voucher
                productIncludes.add(vm.getId());
            }
        }
        return productIncludes;
    }

    public void addRemoveAll(boolean add){
        ArrayList<ProductItemViewModel> current = productVMs.getValue();
        for (int i = 0; i < productVMs.getValue().size(); i++) {
            current.get(i).included.setValue(add);
        }
        productVMs.setValue(current);
    }

    public void getSellerProducts(){
        loading.setValue(true);
        productVMs.setValue(new ArrayList<>());
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    productDB.getAllProducts(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            ArrayList<Product> products = new ArrayList<>();
                            for (DocumentSnapshot doc:
                                 queryDocumentSnapshots.getDocuments()) {
                                products.add(new Product(doc));
                            }
                            addProductToLiveData(products);
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void addProductToLiveData(ArrayList<Product> products){
        ArrayList<ProductItemViewModel> current = productVMs.getValue();
        for (Product p:
             products) {
            current.add(new ProductItemViewModel(p));
        }
        productVMs.setValue(current);
        loading.setValue(false);
    }
}
