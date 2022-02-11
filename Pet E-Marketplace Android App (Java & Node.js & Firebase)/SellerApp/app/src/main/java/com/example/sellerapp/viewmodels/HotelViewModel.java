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
import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Hotel;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.inputclasses.HotelInput;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HotelViewModel {

    private HotelDBAccess hotelDB;
    private AkunDBAccess akunDB;
    private Storage storage;
    private MutableLiveData<String>[] hotelPictureUrl = new MutableLiveData[4];
    private MutableLiveData<Bitmap>[] picturesBitmaps = new MutableLiveData[4];
    private Application app;

    public HotelViewModel(Application application) {
        app = application;
        hotelDB = new HotelDBAccess();
        akunDB = new AkunDBAccess(application);
        storage = new Storage();
        for (int i = 0; i < 4; i++) {
            hotelPictureUrl[i] = new MutableLiveData<>("https://firebasestorage.googleapis.com/v0/b/pawfriends-a5086.appspot.com/o/uploadimg.jpg?alt=media&token=901043a8-9426-484b-b852-e8feb3824006");
            picturesBitmaps[i] = new MutableLiveData<>();
        }
    }

    private MutableLiveData<Hotel> updatingHotel = new MutableLiveData<>();
    private MutableLiveData<String> loadingTitle = new MutableLiveData<>("Menambahkan Kamar");
    private MutableLiveData<Boolean> hotelAdded;
    private MutableLiveData<Boolean> addLoading;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> hotelActive = new MutableLiveData<>(false);
    private MutableLiveData<Integer> focusedField;
    public HotelInput inputs = new HotelInput();
    private MutableLiveData<String[]> fieldErrors;

    public LiveData<Hotel> getHotel(){
        return updatingHotel;
    }

    public LiveData<Boolean> isHotelAdded(){
        if(hotelAdded == null){
            hotelAdded = new MutableLiveData<>();
        }
        return hotelAdded;
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

    public LiveData<Boolean> isHotelActive(){
        return hotelActive;
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

    public MutableLiveData<String> getHotelPicturesURL(int index){
        return hotelPictureUrl[index];
    }

    public LiveData<Bitmap> getPicture(int index){
        return picturesBitmaps[index];
    }

    public void prepareUpdate(String id_kamar){
        //jika akses ketika update
        //otomatis isi gambar-gambar, nama, kategori, dll
        loadingTitle.setValue("Merubah Kamar");
        updating.setValue(true);

        hotelDB.getRoomById(id_kamar).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                updatingHotel.setValue(new Hotel(documentSnapshot));
                inputs.setemailPemilik(updatingHotel.getValue().getemail_pemilik());
                inputs.deskripsi.setValue(updatingHotel.getValue().getDeskripsi());
                inputs.nama.setValue(updatingHotel.getValue().getNama());
                inputs.harga.setValue(String.valueOf(updatingHotel.getValue().getHarga()));
                inputs.jumlah.setValue(String.valueOf(updatingHotel.getValue().getTotal()));
                inputs.panjang.setValue(String.valueOf(updatingHotel.getValue().getPanjang()));
                inputs.lebar.setValue(String.valueOf(updatingHotel.getValue().getLebar()));
                assignFacilities();

                String[] oldPic = updatingHotel.getValue().getDaftar_gambar().split("\\|", -1);
                for (int i = 0; i < 4; i++) {
                    final int index = i;
                    if(oldPic[i].equals("")){
                        hotelPictureUrl[i].setValue("https://firebasestorage.googleapis.com/v0/b/pawfriends-a5086.appspot.com/o/uploadimg.jpg?alt=media&token=901043a8-9426-484b-b852-e8feb3824006");
                    }
                    else{
                        storage.getPictureUrlFromName(oldPic[i]).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                hotelPictureUrl[index].setValue(uri.toString());
                                Glide
                                    .with(app)
                                    .asBitmap()
                                    .load(uri.toString())
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            uploadHotelPicture(index, resource);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });
                            }
                        });
                    }
                }
                
                hotelActive.setValue(updatingHotel.getValue().isAktif());
            }
        });
        
    }

    private void assignFacilities(){
        boolean[] currentFacs = inputs.fasilitas.getValue();
        String[] savedFacs = updatingHotel.getValue().getFasilitasArr();
        for (String savedFac:
             savedFacs) {
            int facNum = Integer.parseInt(savedFac);
            currentFacs[facNum] = true;
        }
        inputs.fasilitas.setValue(currentFacs);
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
            String[] errors = {"", "", "", "", "", "", ""};
            for (int i=0;i<7;i++){
                errors[i] = "Semua Isian Harus Diisi";
            }
            errors[6] = "";
            focusedField.setValue(inputs.emptyField());
            fieldErrors.setValue(errors);
        }
        else if(isPicturesEmpty()){
            String[] errors = {"", "", "", "", "", "", "Minimal 1 Gambar Harus Disertakan"};
            focusedField.setValue(6);
            fieldErrors.setValue(errors);
        }
        else if(inputs.zeroField() != -1){
            String[] errors = {"", "Isian tidak dapat bernilai 0", "Isian tidak dapat bernilai 0", "Isian tidak dapat bernilai 0", "Isiian tidak dapat bernilai 0", "", ""};
            focusedField.setValue(inputs.zeroField());
            fieldErrors.setValue(errors);
        }
        else if((updating.getValue()) && Integer.parseInt(inputs.jumlah.getValue()) < updatingHotel.getValue().getSedang_disewa()){
            String[] errors = {"", "", "Total Kamar tidak dapat kurang dari jumlah kamar yang sedang disewa", "", "", "", ""};
            focusedField.setValue(2);
            fieldErrors.setValue(errors);
        }
        else{
            String[] errors = {"", "", "", "", "", "", ""};
            fieldErrors.setValue(errors);
            beginUploads();
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
                    inputs.setemailPemilik(accountsGot.get(0).getEmail());

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
                inputs.setPictureId(index, picName);
                if (index == filledBitmaps.size()-1){
                    uploadHotelData();
                }
            }
        });
    }

    private void uploadHotelData(){
        if(updating.getValue()){
            hotelDB.updateKamar(inputs, updatingHotel.getValue().getId_kamar()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    addLoading.setValue(false);
                    hotelAdded.setValue(true);
                }
            });
        }
        else{
            hotelDB.addKamar(inputs).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    addLoading.setValue(false);
                    hotelAdded.setValue(true);
                }
            });
        }
    }

    public void deleteHotelPicture(int index){
        picturesBitmaps[index].setValue(null);
    }

    public void uploadHotelPicture(int index, Bitmap uploaded){
        picturesBitmaps[index].setValue(uploaded);
    }

    public void activateProduct(){
        boolean active;
        if(hotelActive.getValue()){
            //mengubah aktif menjadi nonaktif
            active = false;
            loadingTitle.setValue("Menonaktifkan Kamar");
        }
        else{
            //mengubah nonaktif menjadi aktif
            active = true;
            loadingTitle.setValue("Mengaktifkan Kamar");
        }
        addLoading.setValue(true);
        hotelDB.activateRoom(active, updatingHotel.getValue().getId_kamar()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addLoading.setValue(false);
                hotelAdded.setValue(true);
            }
        });
    }

    public void beginDeletes(){
        //delete semua variasi dan gambar produk terlebih dahulu
        loadingTitle.setValue("Menghapus Kamar");
        addLoading.setValue(true);

        hotelDB.deleteRoom(updatingHotel.getValue().getId_kamar()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addLoading.setValue(false);
                hotelAdded.setValue(true);
            }
        });
    }

}
