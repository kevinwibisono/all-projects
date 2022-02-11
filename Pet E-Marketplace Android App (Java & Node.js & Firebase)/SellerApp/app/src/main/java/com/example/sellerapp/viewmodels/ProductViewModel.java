package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.sellerapp.models.VarianProduk;
import com.example.sellerapp.inputclasses.ProductInput;
import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.viewmodels.itemviewmodels.VariantProductViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductViewModel {

    private ProductDBAccess productDB;
    private AkunDBAccess akunDB;
    private Storage storage;
    private MutableLiveData<String>[] productPicturesURL = new MutableLiveData[4];
    private MutableLiveData<Bitmap>[] picturesBitmaps = new MutableLiveData[4];
    private Application app;

    public ProductViewModel(Application application) {
        app = application;
        productDB = new ProductDBAccess();
        akunDB = new AkunDBAccess(application);
        storage = new Storage();
        for (int i = 0; i < 4; i++) {
            productPicturesURL[i] = new MutableLiveData<>("https://firebasestorage.googleapis.com/v0/b/pawfriends-a5086.appspot.com/o/uploadimg.jpg?alt=media&token=901043a8-9426-484b-b852-e8feb3824006");
            picturesBitmaps[i] = new MutableLiveData<>();
        }
    }

    private int kategori = 0;
    private MutableLiveData<Product> updatingProduct = new MutableLiveData<>();
    private MutableLiveData<String> loadingTitle = new MutableLiveData<>("Menambahkan Produk");
    private MutableLiveData<ArrayList<VariantProductViewModel>> variants;
    private ArrayList<VarianProduk> updatingVars = new ArrayList<>();
    private MutableLiveData<Boolean> productAdded;
    private MutableLiveData<Boolean> addLoading;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> productActive = new MutableLiveData<>(false);
    private MutableLiveData<Integer> focusedField;
    public ProductInput inputs = new ProductInput();
    private String[] hargaStokTempHolder = new String[2];
    private MutableLiveData<Boolean> variantsActivated = new MutableLiveData<>(false);
    private MutableLiveData<String[]> fieldErrors;

    public LiveData<Product> getProduct(){
        return updatingProduct;
    }

    public LiveData<ArrayList<VariantProductViewModel>> getVariants(){
        if(variants == null){
            variants = new MutableLiveData<>(new ArrayList<>());
        }
        return variants;
    }

    public LiveData<Boolean> isProductAdded(){
        if(productAdded == null){
            productAdded = new MutableLiveData<>();
        }
        return productAdded;
    }

    public LiveData<Boolean> isAddLoading(){
        if(addLoading == null){
            addLoading = new MutableLiveData<>();
        }
        return addLoading;
    }

    public LiveData<Boolean> isUpdating(){
        return updating;
    }

    public LiveData<Boolean> isProductActive(){
        return productActive;
    }

    public int getKategori(){
        return kategori;
    }

    public LiveData<String> getLoadingTitle(){
        return loadingTitle;
    }

    public LiveData<Integer> getFocusedFieldNumber(){
        if(focusedField == null){
            focusedField = new MutableLiveData<>();
        }
        return focusedField;
    }

    public LiveData<String[]> getFieldErrors(){
        if(fieldErrors == null){
            String[] errors = {"", "", "", "", "", "", "", ""};
            fieldErrors = new MutableLiveData<>(errors);
        }
        return fieldErrors;
    }

    public MutableLiveData<String> getPicturesURL(int index){
        return productPicturesURL[index];
    }

    public LiveData<Bitmap> getPicture(int index){
        return picturesBitmaps[index];
    }

    public LiveData<Boolean> isVariantsActivated() {
        return variantsActivated;
    }

    public void prepareUpdate(String id_produk){
        //jika akses ketika update
        //otomatis isi gambar-gambar, nama, kategori, dll
        loadingTitle.setValue("Merubah Produk");
        updating.setValue(true);

        productDB.getProductById(id_produk).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                updatingProduct.setValue(new Product(documentSnapshot));
                inputs.setemailPenjual(updatingProduct.getValue().getemail_penjual());
                inputs.setKategori(updatingProduct.getValue().getKategori());
                kategori = updatingProduct.getValue().getKategori();

                inputs.deskripsi.setValue(updatingProduct.getValue().getDeskripsi());
                inputs.nama.setValue(updatingProduct.getValue().getNama());
                inputs.berat.setValue(String.valueOf(updatingProduct.getValue().getBerat()));
                String[] oldPic = updatingProduct.getValue().getDaftar_gambar().split("\\|", -1);
                for (int i = 0; i < 4; i++) {
                    final int index = i;
                    if(oldPic[i].equals("")){
                        productPicturesURL[i].setValue("https://firebasestorage.googleapis.com/v0/b/pawfriends-a5086.appspot.com/o/uploadimg.jpg?alt=media&token=901043a8-9426-484b-b852-e8feb3824006");
                    }
                    else{
                        storage.getPictureUrlFromName(oldPic[i]).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                productPicturesURL[index].setValue(uri.toString());
                                Glide
                                    .with(app)
                                    .asBitmap()
                                    .load(uri.toString())
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            uploadProductPicture(index, resource);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });
                            }
                        });
                    }
                }

                productDB.getVariants(updatingProduct.getValue().getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dbResults = queryDocumentSnapshots.getDocuments();
                        if(dbResults.size() > 0){
                            variantsActivated.setValue(true);
                            activateVariants(true);

                            ArrayList<VariantProductViewModel> productVariants = new ArrayList<>();
                            for (int i = 0; i < dbResults.size(); i++) {
                                final int index = i;
                                storage.getPictureUrlFromName(dbResults.get(index).getString("gambar")).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide
                                            .with(app)
                                            .asBitmap()
                                            .load(uri.toString())
                                            .into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    VariantProductViewModel variantInput = new VariantProductViewModel(dbResults.get(index).getString("nama"),
                                                            String.valueOf(dbResults.get(index).getLong("harga")),
                                                            String.valueOf(dbResults.get(index).getLong("stok")), resource,
                                                            dbResults.get(index).getId());
                                                    productVariants.add(variantInput);
                                                    variants.setValue(productVariants);
                                                    updatingVars.add(new VarianProduk(dbResults.get(index)));
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                                }
                                            });
                                    }
                                });
                            }
                        }
                        else{
                            inputs.stok.setValue(String.valueOf(updatingProduct.getValue().getStok()));
                            inputs.harga.setValue(String.valueOf(updatingProduct.getValue().getHarga()));
                        }
                    }
                });

                
                productActive.setValue(updatingProduct.getValue().isAktif());
            }
        });
    }

    public void deleteVariant(int index){
        ArrayList<VariantProductViewModel> tempVariants = variants.getValue();
        if(tempVariants.get(index).getIdVariant() != null){
            productDB.deleteVariant(tempVariants.get(index).getIdVariant());
        }
        tempVariants.remove(index);
        variants.setValue(tempVariants);
    }

    public void insertVariant(String nama, String harga, String stok, Bitmap gambar){
        ArrayList<VariantProductViewModel> tempVariants = variants.getValue();
        tempVariants.add(new VariantProductViewModel(nama, harga, stok, gambar));
        variants.setValue(tempVariants);
    }

    public void updateVariant(String nama, String harga, String stok, Bitmap gambar, String id_varian){
        ArrayList<VariantProductViewModel> tempVariants = variants.getValue();
        for (VariantProductViewModel variant:
             tempVariants) {
            if(variant.getIdVariant().equals(id_varian)){
                variant.setNama(nama);
                variant.setHarga(harga);
                variant.setStok(stok);
                variant.setGambar(gambar);
            }
        }
        variants.setValue(tempVariants);
    }

    public void activateVariants(boolean activated){
        variantsActivated.setValue(activated);
        if(activated){
            hargaStokTempHolder[0] = inputs.harga.getValue();
            hargaStokTempHolder[1] = inputs.stok.getValue();

            //isi agar tidak menimbulkan error pada saat pengecekan (krn pengecekan dibuat menyeluruh)
            inputs.harga.setValue("1");
            inputs.stok.setValue("1");
        }
        else{
            inputs.harga.setValue(String.valueOf(hargaStokTempHolder[0]));
            inputs.stok.setValue(String.valueOf(hargaStokTempHolder[1]));
        }
    }

    private boolean isPicturesEmpty(){
        for (int i=0;i<4;i++){
            if(picturesBitmaps[i].getValue() != null){
                return false;
            }
        }
        return true;
    }

    public void addProduct(){
        if(inputs.emptyField() != -1){
            String[] errors = {"", "", "", "", "", "", "", ""};
            for (int i=0;i<6;i++){
                errors[i] = "Semua Isian Harus Diisi";
            }
            errors[1] = "Mohon Pilih 1 Kategori";
            focusedField.setValue(inputs.emptyField());
            fieldErrors.setValue(errors);
        }
        else if(isPicturesEmpty()){
            String[] errors = {"", "", "", "", "", "", "Minimal 1 Gambar Harus Disertakan", ""};
            focusedField.setValue(6);
            fieldErrors.setValue(errors);
        }
        else if(inputs.zeroField() != -1){
            String[] errors = {"", "", "Isian tidak dapat bernilai 0", "Isian tidak dapat bernilai 0", "Isian tidak dapat bernilai 0", "", "", ""};
            focusedField.setValue(inputs.zeroField());
            fieldErrors.setValue(errors);
        }
        else if(variantsActivated.getValue() && !variasiValid()){
            String[] errors = {"", "", "", "", "", "", "", "Mohon Sertakan Minimal 2 Variasi Dan Tidak Kosong"};
            fieldErrors.setValue(errors);
            focusedField.setValue(7);
        }
        else{
            String[] errors = {"", "", "", "", "", "", "", ""};
            fieldErrors.setValue(errors);
            beginUploads();
        }
    }

    private boolean variasiValid(){
        int totalVariasi = 0;
        for (VariantProductViewModel varianVM:
             variants.getValue()) {
            totalVariasi += Integer.parseInt(varianVM.getStok());
        }
        if(variants.getValue().size() > 1 && totalVariasi > 0){
            return true;
        }
        else{
            return false;
        }
    }

    private void beginUploads(){
        addLoading.setValue(true);
        ArrayList<Bitmap> filledBitmaps = new ArrayList<>();
        for (int i=0;i<4;i++){
            if(picturesBitmaps[i].getValue() != null){
                filledBitmaps.add(picturesBitmaps[i].getValue());
            }
        }

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                //1. dapatkan no hp terlebih dahulu
                if(accountsGot.size() > 0){
                    inputs.setemailPenjual(accountsGot.get(0).getEmail());

                    //2. upload gambar satu per satu
                    for (int i=0;i<filledBitmaps.size();i++){
                        final int index = i;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        filledBitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        uploadPictures(data, index, filledBitmaps);
                    }
                }

            }
        });

        akunDB.getSavedAccounts();

    }

    private void uploadPictures(byte[] data, int index, ArrayList<Bitmap> filledBitmaps){
        String picName = UUID.nameUUIDFromBytes(data).toString();
        storage.uploadPicture(data, picName).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storage.uploadPictureUrl(picName);
                inputs.setPictureUrl(index, picName);
                if (index == filledBitmaps.size()-1){
                    uploadProductData();
                }
            }
        });
    }

    private void uploadProductData(){
        if(variantsActivated.getValue()){
            int maxHarga = -999, stok = 0;
            for (VariantProductViewModel variant:
                    variants.getValue()) {
                if(Integer.parseInt(variant.getHarga()) > maxHarga){
                    maxHarga = Integer.parseInt(variant.getHarga());
                }
                stok += Integer.parseInt(variant.getStok());
            }
            inputs.harga.setValue(String.valueOf(maxHarga));
            inputs.stok.setValue(String.valueOf(stok));
        }

        if(updating.getValue()){
            productDB.updateProduct(inputs, updatingProduct.getValue().getId_produk()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(variantsActivated.getValue()){
                        setVariantsId(updatingProduct.getValue().getId_produk());
                    }
                    else{
                        addLoading.setValue(false);
                        productAdded.setValue(true);
                    }
                }
            });
        }
        else{
            productDB.addProduct(inputs).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    if(variantsActivated.getValue()){
                        setVariantsId(documentReference.getId());
                    }
                    else{
                        addLoading.setValue(false);
                        productAdded.setValue(true);
                    }
                }
            });
        }
    }

    private void setVariantsId(String id){
        ArrayList<VariantProductViewModel> arrayVarianHolder = variants.getValue();
        for (VariantProductViewModel varian:
                arrayVarianHolder) {
            varian.setIdProduk(id);
        }
        variants.setValue(arrayVarianHolder);
        uploadVariantsPictures();
    }

    private void uploadVariantsPictures(){
        for (int i = 0; i < variants.getValue().size(); i++) {
            final int index = i;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            variants.getValue().get(i).getGambar().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            String picName = UUID.nameUUIDFromBytes(data).toString();

            storage.uploadPicture(data, picName).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storage.uploadPictureUrl(picName);
                    if(variants.getValue().get(index).getIdVariant() != null){
                        productDB.updateVariantProduct(variants.getValue().get(index), picName).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (index == variants.getValue().size()-1){
                                    addLoading.setValue(false);
                                    productAdded.setValue(true);
                                }
                            }
                        });
                    }
                    else{
                        productDB.addVariantProduct(variants.getValue().get(index), picName).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                if (index == variants.getValue().size()-1){
                                    addLoading.setValue(false);
                                    productAdded.setValue(true);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void deleteProductPictures(int index){
        picturesBitmaps[index].setValue(null);
    }

    public void uploadProductPicture(int index, Bitmap uploaded){
        picturesBitmaps[index].setValue(uploaded);
    }

    public void activateProduct(){
        if(productActive.getValue()){
            //mengubah aktif menjadi nonaktif
            loadingTitle.setValue("Menonaktifkan Produk");
            beginActivate(false);
        }
        else{
            //mengubah nonaktif menjadi aktif
            int stok = 0;
            if(updatingVars.size() > 0){
                //ada variasi, hitung stok dari variasi
                for (VarianProduk varian:
                     updatingVars) {
                    stok += varian.getStok();
                }
                if(stok <= 0){
                    String[] errors = {"", "", "", "", "", "", "", "Variasi Tidak Boleh Kosong, Mohon Ubah Produk Terlebih Dahulu"};
                    fieldErrors.setValue(errors);
                    focusedField.setValue(7);
                }
                else{
                    loadingTitle.setValue("Mengaktifkan Produk");
                    beginActivate(true);
                }
            }
            else{
                stok = updatingProduct.getValue().getStok();
                if(stok <= 0){
                    String[] errors = {"", "", "", "Stok Tidak Boleh Kosong, Mohon Ubah Produk Terlebih Dahulu", "", "", "", ""};
                    focusedField.setValue(3);
                    fieldErrors.setValue(errors);
                }
                else{
                    loadingTitle.setValue("Mengaktifkan Produk");
                    beginActivate(true);
                }
            }
        }
    }

    private void beginActivate(boolean active){
        addLoading.setValue(true);
        productDB.activateProduct(active, updatingProduct.getValue().getId_produk()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addLoading.setValue(false);
                productAdded.setValue(true);
            }
        });
    }

    public void beginDeletes(){
        //delete semua variasi dan gambar produk terlebih dahulu
        loadingTitle.setValue("Menghapus Produk dan Variasinya");
        addLoading.setValue(true);

        productDB.getVariants(updatingProduct.getValue().getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> dbVariasi = queryDocumentSnapshots.getDocuments();
                if(dbVariasi.size() > 0){
                    for (int i = 0; i < dbVariasi.size(); i++){
                        final int index = i;
                        productDB.deleteVariant(dbVariasi.get(i).getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(index == dbVariasi.size() - 1){
                                    //berhasil hapus variasi terakhir
                                    deleteProduct();
                                }
                            }
                        });
                    }
                }
                else{
                    deleteProduct();
                }
            }
        });
    }

    private void deleteProduct(){
        productDB.deleteProduct(updatingProduct.getValue().getId_produk()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addLoading.setValue(false);
                //untuk trigger tugas view ini sdh selesai, kembali ke product list
                productAdded.setValue(true);
            }
        });
    }

}
