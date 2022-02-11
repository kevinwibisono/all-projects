package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.Promo;
import com.example.sellerapp.models.PromoDBAccess;
import com.example.sellerapp.inputclasses.VoucherInput;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddPromoViewModel extends AndroidViewModel {
    private PromoDBAccess promoDB;
    private ProductDBAccess productDB;
    private AkunDBAccess akunDB;

    public AddPromoViewModel(Application application){
        super(application);
        promoDB = new PromoDBAccess();
        productDB = new ProductDBAccess();
        akunDB = new AkunDBAccess(application);
    }

    private Date berlaku;
    private String id_update;
    public MutableLiveData<Boolean> allProduct = new MutableLiveData<>(true);
    private MutableLiveData<String> tanggal = new MutableLiveData<>("");
    private MutableLiveData<String> loadingTitle = new MutableLiveData<>("Menambahkan Voucher.....");
    private VoucherInput input = new VoucherInput();

    private MutableLiveData<ArrayList<ProductItemViewModel>> chosenProductVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> productLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> promoAdded = new MutableLiveData<>();
    private MutableLiveData<String[]> fieldErrors;
    private MutableLiveData<Integer> focusError;

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<Boolean> isProductLoading(){
        return productLoading;
    }

    public LiveData<Boolean> isAdded(){
        return promoAdded;
    }

    public LiveData<Boolean> isUpdating(){
        return updating;
    }

    public VoucherInput getInput() {
        return input;
    }

    public LiveData<String> getTanggal() {
        return tanggal;
    }

    public LiveData<String> getLoadingTitle() {
        return loadingTitle;
    }

    public void setBerlaku(Date berlaku) {
        this.berlaku = berlaku;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getChosenProductsVMs(){
        return chosenProductVMs;
    }

    public LiveData<String[]> getFieldErrors(){
        if(fieldErrors == null){
            String[] initErrors = {"", "", "", "", "", ""};
            fieldErrors = new MutableLiveData<>(initErrors);
        }
        return fieldErrors;
    }

    public LiveData<Integer> getFocusError(){
        if(focusError == null){
            focusError = new MutableLiveData<>();
        }
        return focusError;
    }

    public void setChosenProductsIds(ArrayList<String> chosenProductsIds) {
        chosenProductVMs.setValue(new ArrayList<>());
        if(chosenProductsIds.size() > 0){
            productLoading.setValue(true);
            for (int i = 0; i < chosenProductsIds.size(); i++) {
                final int index = i;
                productDB.getProductById(chosenProductsIds.get(i)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        addProductToLiveData(new Product(documentSnapshot));
                        if(index == chosenProductsIds.size()-1){
                            productLoading.setValue(false);
                        }
                    }
                });
            }
        }
    }

    public void updateVoucher(String id){
        loadingTitle.setValue("Mengubah Promo.....");
        id_update = id;
        promoDB.getPromoDetail(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                updating.setValue(true);
                Promo updatePromo = new Promo(documentSnapshot);
                if(!updatePromo.getId_produk().equals("")){
                    ArrayList<String> dbProductIDs = new ArrayList<>();
                    String[] idProducts = updatePromo.getId_produk().split("\\|");
                    for (String idProduct:
                         idProducts) {
                        dbProductIDs.add(idProduct);
                    }
                    setChosenProductsIds(dbProductIDs);
                    allProduct.setValue(false);
                }
                input.judul.setValue(updatePromo.getJudul());
                input.jumlah.setValue(String.valueOf(updatePromo.getPersentase()));
                input.maksimum.setValue(String.valueOf(updatePromo.getMaximum_diskon()));
                input.minimum.setValue(String.valueOf(updatePromo.getMinimum_pembelian()));
                berlaku = updatePromo.getValid_hingga().toDate();
                tanggal.setValue(getStringTanggal(berlaku));
            }
        });
    }

    private String getStringTanggal(Date d){
        String tanggal = "";
        tanggal = d.getDate()+"/"+(d.getMonth()+1)+"/"+(d.getYear()+1900);
        return tanggal;
    }

    private void addProductToLiveData(Product p){
        ArrayList<ProductItemViewModel> currentVMs = chosenProductVMs.getValue();
        currentVMs.add(new ProductItemViewModel(p));
        chosenProductVMs.setValue(currentVMs);
    }

    public void addVoucher(){
        if(input.lookForErrors() > -1){
            String[] errors = {"", "", "", "", "", ""};
            errors[input.lookForErrors()] = "Semua isian wajib diisi";
            fieldErrors.setValue(errors);
            focusError.setValue(input.lookForErrors());
        }
        else if(berlaku == null){
            String[] errors = {"", "Mohon Tentukan Tanggal", "", "", "", ""};
            fieldErrors.setValue(errors);
            focusError.setValue(1);
        }
        else if(!allProduct.getValue() && chosenProductVMs.getValue().size() <= 0){
            String[] errors = {"", "", "", "", "Mohon Tentukan Minimal 1 Produk", ""};
            fieldErrors.setValue(errors);
            focusError.setValue(4);
        }
        else if(Integer.parseInt(input.jumlah.getValue()) > 100){
            String[] errors = {"", "", "", "", "", "Persentase Tidak Dapat Lebih dari 100"};
            fieldErrors.setValue(errors);
            focusError.setValue(5);
        }
        else{
            //tanpa error, lanjut add
            Promo tobeAdded = new Promo(input.judul.getValue(), combineArray(chosenProductVMs.getValue()), Integer.parseInt(input.jumlah.getValue()),
                    Integer.parseInt(input.maksimum.getValue()), Integer.parseInt(input.minimum.getValue()), berlaku);

            loading.setValue(true);
            akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
                @Override
                public void onComplete(List<Akun> accountsGot) {
                    if(accountsGot.size() > 0){
                        if(updating.getValue()){
                            promoDB.updatePromo(tobeAdded, id_update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loading.setValue(false);
                                    promoAdded.setValue(true);
                                }
                            });
                        }
                        else{
                            promoDB.addPromo(tobeAdded, accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    loading.setValue(false);
                                    promoAdded.setValue(true);
                                }
                            });
                        }
                    }
                }
            });

            akunDB.getSavedAccounts();
        }
    }

    private String combineArray(ArrayList<ProductItemViewModel> productsVMs){
        String combined = "";
        if(productsVMs != null){
            for (ProductItemViewModel vm:
                    productsVMs) {
                if(combined.equals("")){
                    combined += vm.getId();
                }
                else{
                    combined += "|" +vm.getId();
                }
            }
        }
        return combined;
    }
}
