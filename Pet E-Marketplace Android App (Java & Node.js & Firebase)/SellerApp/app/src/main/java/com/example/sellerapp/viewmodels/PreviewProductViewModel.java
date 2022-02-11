package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.Review;
import com.example.sellerapp.models.ReviewDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.models.VarianProduk;
import com.example.sellerapp.models.PromoDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.ReviewItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PreviewProductViewModel {
    private ProductDBAccess productDB;
    private AkunDBAccess akunDB;
    private ReviewDBAccess reviewDB;
    private PromoDBAccess voucherDB;
    private Storage storage;
    private Application app;

    public PreviewProductViewModel(Application application) {
        app = application;
        this.productDB = new ProductDBAccess();
        this.akunDB = new AkunDBAccess(application);
        this.reviewDB = new ReviewDBAccess();
        this.voucherDB = new PromoDBAccess();
        this.storage = new Storage();
    }

    private Product produk;
    private String idProduk;
    private int[] scoreList = {0, 5, 4, 3, 2, 1};
    private MutableLiveData<String[]> pics = new MutableLiveData<>(new String[5]);
    private MutableLiveData<ArrayList<VarianProduk>> variants = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<ReviewItemViewModel>> revVMs = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Boolean> variantsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> reviewsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> deleted = new MutableLiveData<>(false);
    private MutableLiveData<String> namaPenjual = new MutableLiveData<>("");
    private MutableLiveData<String> gambarPenjual = new MutableLiveData<>();
    private MutableLiveData<String> nama = new MutableLiveData<>("");
    private MutableLiveData<String> harga = new MutableLiveData<>("");
    private MutableLiveData<String> stok = new MutableLiveData<>("");
    private MutableLiveData<String> berat = new MutableLiveData<>("");
    private MutableLiveData<String> deskripsi = new MutableLiveData<>("");
    private MutableLiveData<String> kategori = new MutableLiveData<>("");
    private MutableLiveData<String> terjual = new MutableLiveData<>("");
    private MutableLiveData<String> nilai = new MutableLiveData<>("0");
    private MutableLiveData<Integer> jumlahVoucher = new MutableLiveData<>();

    public String getIdProduk(){
        return idProduk;
    }

    public LiveData<ArrayList<VarianProduk>> getVariants(){
        return variants;
    }

    public LiveData<ArrayList<ReviewItemViewModel>> getRevVMs(){
        return revVMs;
    }

    public LiveData<Boolean> isVariantsLoading(){
        return variantsLoading;
    }

    public LiveData<Boolean> isReviewsLoading(){
        return reviewsLoading;
    }

    public LiveData<String> getNamaPenjual(){
        return namaPenjual;
    }

    public LiveData<String> getGambarPenjual(){
        return gambarPenjual;
    }

    public LiveData<String> getNama(){
        return nama;
    }

    public LiveData<String> getHarga(){
        return harga;
    }

    public LiveData<String> getStok(){
        return stok;
    }

    public LiveData<String> getBerat(){
        return berat;
    }

    public LiveData<String> getDesc(){
        return deskripsi;
    }

    public LiveData<String> getKategori(){
        return kategori;
    }

    public LiveData<String> getTerjual(){
        return terjual;
    }

    public LiveData<String> getNilai(){
        return nilai;
    }

    public LiveData<Integer> getJumlahVoucher(){
        return jumlahVoucher;
    }

    public LiveData<String[]> getPictures(){
        return pics;
    }

    public LiveData<Boolean> isDeleted() {
        return deleted;
    }

    public void setProduk(String id_produk){
        idProduk = id_produk;
        String[] initPics = {"", "", "", "", ""};
        pics.setValue(initPics);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    namaPenjual.setValue(accountsGot.get(0).getNama());
                    storage.getPictureUrlFromName(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            gambarPenjual.setValue(uri.toString());
                        }
                    });

                    productDB.getProductById(id_produk).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                produk = new Product(documentSnapshot);

                                nama.setValue(produk.getNama());
                                harga.setValue(produk.getTSHarga());
                                kategori.setValue(produk.getKategoriString());
                                terjual.setValue(String.valueOf(produk.getTerjual()));
                                berat.setValue(String.valueOf(produk.getBerat()));
                                stok.setValue(String.valueOf(produk.getStok()));
                                deskripsi.setValue(produk.getDeskripsi());

                                loadProductPictures();
                                getProductVariants();
                                getProductReviews(id_produk, 0);
                                getVouchers();
                            }
                            else{
                                deleted.setValue(true);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void getProductVariants(){
        variantsLoading.setValue(true);
        productDB.getVariants(produk.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> productVariants = queryDocumentSnapshots.getDocuments();
                ArrayList<VarianProduk> variasiProduk = new ArrayList<>();
                for (int i = 0; i < productVariants.size(); i++) {
                    variasiProduk.add(new VarianProduk(productVariants.get(i)));
                }
                addVariantToLiveData(variasiProduk);
            }
        });
    }

    public void getProductReviews(String id_produk, int skor){
        final int score = scoreList[skor];
        reviewsLoading.setValue(true);
        revVMs.setValue(new ArrayList<>());
        if(score == 0){
            reviewDB.getItemReviews(id_produk).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int jumlahRev = queryDocumentSnapshots.getDocuments().size();
                    float skorNow = 0;
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        skorNow += doc.getLong("nilai");
                    }
                    if(skorNow <= 0){
                        nilai.setValue("0");
                    }
                    else{
                        String skorStr = String.valueOf(skorNow/jumlahRev).substring(0, 3);
                        nilai.setValue(skorStr);
                    }
                }
            });
        }
        reviewDB.getItemReviewsFiltered(id_produk, score, 5).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Review> revArr = new ArrayList<>();
                for (DocumentSnapshot doc:
                        queryDocumentSnapshots.getDocuments()) {
                    revArr.add(new Review(doc));
                }
                addReviewToLiveData(revArr);
            }
        });
    }

    private void getVouchers(){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    voucherDB.getShopPromos(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> vouchers = queryDocumentSnapshots.getDocuments();
                            jumlahVoucher.setValue(vouchers.size());
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void addVariantToLiveData(ArrayList<VarianProduk> variantsFromDB){
        ArrayList<VarianProduk> livedataVariants = variants.getValue();
        livedataVariants.addAll(variantsFromDB);
        variants.setValue(livedataVariants);
        variantsLoading.setValue(false);
    }

    private void addReviewToLiveData(ArrayList<Review> reviewFromDB){
        ArrayList<ReviewItemViewModel> currentVMs = revVMs.getValue();
        for (Review rev:
             reviewFromDB) {
            currentVMs.add(new ReviewItemViewModel(rev, app));
        }
        revVMs.setValue(currentVMs);
        reviewsLoading.setValue(false);
    }

    private void assignPicsLiveData(String pic, int index){
        String[] currentPics = pics.getValue();
        currentPics[index] = pic;
        pics.setValue(currentPics);
    }

    private void loadProductPictures(){
        String[] daftarGambar = produk.getDaftar_gambar().split("\\|");

        for (int i = 0; i < daftarGambar.length; i++) {
            final int index = i+1;
            storage.getPictureUrlFromName(daftarGambar[i]).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    assignPicsLiveData(uri.toString(), index);
                }
            });
        }
    }

    public void chooseVariant(int idxVarian){
        VarianProduk chosenVariant = variants.getValue().get(idxVarian);
        storage.getPictureUrlFromName(chosenVariant.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                assignPicsLiveData(uri.toString(), 0);
                loadProductPictures();
            }
        });

        nama.setValue(produk.getNama()+" - "+chosenVariant.getNama());
        harga.setValue(chosenVariant.getTSHarga());
    }
}
