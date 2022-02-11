package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.models.VarianProduk;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class AddVariasiViewModel extends ViewModel {

    private ProductDBAccess productDB;
    private Storage storage;
    private Application app;

    public AddVariasiViewModel(Application app) {
        this.app = app;
        this.storage = new Storage();
        this.productDB = new ProductDBAccess();
    }

    private MutableLiveData<Boolean> allowed;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private MutableLiveData<Bitmap> pictureBitmap = new MutableLiveData<>();
    public MutableLiveData<String> nama = new MutableLiveData<>("");
    public MutableLiveData<String> harga = new MutableLiveData<>("");
    public MutableLiveData<String> stok = new MutableLiveData<>("");
    private MutableLiveData<String> errorPic = new MutableLiveData<>("");
    private MutableLiveData<Integer> focusedField;
    private MutableLiveData<String[]> fieldErrors;

    public LiveData<Boolean> isAllowed(){
        if(allowed == null){
            allowed = new MutableLiveData<>();
        }
        return allowed;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<Boolean> isUpdating(){
        return updating;
    }

    public LiveData<String[]> getFieldErrors(){
        if(fieldErrors == null){
            String[] initErrors = {"", "", ""};
            fieldErrors = new MutableLiveData<>(initErrors);
        }
        return fieldErrors;
    }

    public LiveData<Integer> getFocusedField(){
        if(focusedField == null){
            focusedField = new MutableLiveData<>();
        }
        return focusedField;
    }

    private void setFieldErrors(String errorNama, String errorHarga, String errorStok){
        String[] errors = {errorNama, errorHarga, errorStok};
        fieldErrors.setValue(errors);
    }

    public LiveData<String> getErrorPic(){
        if(errorPic == null){
            errorPic = new MutableLiveData<>("");
        }
        return errorPic;
    }

    public LiveData<Bitmap> getPicture() {
        return pictureBitmap;
    }

    public void setPicture(Bitmap picture) {
        this.pictureBitmap.setValue(picture);
    }

    public void addVariasi(){
        if(pictureBitmap.getValue() == null){
            errorPic.setValue("Mohon Sertakan Gambar Variasi");
            setFieldErrors("", "", "");
        }
        else if(nama.getValue().equals("") || harga.getValue().equals("") || stok.getValue().equals("")){
            errorPic.setValue("");
            setFieldErrors("Semua Isian Harus Diisi", "Semua Isian Harus Diisi", "Semua Isian Harus Diisi");
            focusedField.setValue(0);
        }
        else if(Integer.parseInt(harga.getValue()) < 100){
            errorPic.setValue("");
            setFieldErrors("", "Harga Minimum Adalah Rp 100", "");
            focusedField.setValue(1);
        }
        else if(Integer.parseInt(stok.getValue()) < 1){
            errorPic.setValue("");
            setFieldErrors("", "", "Stok Tidak Boleh Kosong");
            focusedField.setValue(2);
        }
        else{
            errorPic.setValue("");
            setFieldErrors("", "", "");
            allowed.setValue(true);
        }
    }

    public void getVariantDetail(String id_variasi){
        loading.setValue(true);
        updating.setValue(true);

        productDB.getVariantsById(id_variasi).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    VarianProduk varianProduk = new VarianProduk(documentSnapshot);

                    storage.getPictureUrlFromName(varianProduk.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide
                                    .with(app)
                                    .asBitmap()
                                    .load(uri.toString())
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            pictureBitmap.setValue(resource);
                                            nama.setValue(varianProduk.getNama());
                                            harga.setValue(String.valueOf(varianProduk.getHarga()));
                                            stok.setValue(String.valueOf(varianProduk.getStok()));
                                            loading.setValue(false);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });
                        }
                    });

                }
            }
        });
    }
}
