package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Hotel;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.Review;
import com.example.sellerapp.models.ReviewDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.viewmodels.itemviewmodels.ReviewItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HotelPreviewViewModel {
    private Application app;
    private AkunDBAccess akunDB;
    private HotelDBAccess hotelDB;
    private ReviewDBAccess reviewDB;
    private Storage storage;

    public HotelPreviewViewModel(Application app){
        this.app = app;
        akunDB = new AkunDBAccess(app);
        hotelDB = new HotelDBAccess();
        reviewDB = new ReviewDBAccess();
        storage = new Storage();
    }

    private Hotel hotel;
    private int[] scoreList = {0, 5, 4, 3, 2, 1};
    private MutableLiveData<String[]> pics = new MutableLiveData<>(new String[5]);
    private MutableLiveData<Boolean[]> facsIncluded = new MutableLiveData<>(new Boolean[9]);
    private MutableLiveData<ArrayList<ReviewItemViewModel>> revVMs = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Boolean> reviewsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> deleted = new MutableLiveData<>(false);
    private MutableLiveData<String> namaPenjual = new MutableLiveData<>("");
    private MutableLiveData<String> gambarPenjual = new MutableLiveData<>();
    private MutableLiveData<String> nama = new MutableLiveData<>("");
    private MutableLiveData<String> harga = new MutableLiveData<>("");
    private MutableLiveData<String> sisa = new MutableLiveData<>("");
    private MutableLiveData<String> panjang = new MutableLiveData<>("");
    private MutableLiveData<String> lebar = new MutableLiveData<>("");
    private MutableLiveData<String> deskripsi = new MutableLiveData<>("");
    private MutableLiveData<String> nilai = new MutableLiveData<>("0");

    public LiveData<ArrayList<ReviewItemViewModel>> getRevVMs(){
        return revVMs;
    }

    public LiveData<Boolean> isReviewsLoading(){
        return reviewsLoading;
    }

    public LiveData<Boolean> isDeleted(){
        return deleted;
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

    public LiveData<String> getLength(){
        return panjang;
    }

    public LiveData<String> getWidth(){
        return lebar;
    }

    public LiveData<String> getSisa(){
        return sisa;
    }

    public String getIdKamar(){
        return hotel.getId_kamar();
    }

    public LiveData<String> getDesc(){
        return deskripsi;
    }

    public LiveData<String> getNilai(){
        return nilai;
    }

    public LiveData<String[]> getPictures(){
        return pics;
    }

    public LiveData<Boolean[]> isFacsIncluded(){
        return facsIncluded;
    }

    public void setKamar(String id_kamar){
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

                    hotelDB.getRoomById(id_kamar).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                hotel = new Hotel(documentSnapshot);

                                //dapatkan properti-properti hotel
                                nama.setValue(hotel.getNama());
                                harga.setValue(hotel.getTSHarga());
                                deskripsi.setValue(hotel.getDeskripsi());
                                panjang.setValue(String.valueOf(hotel.getPanjang()));
                                lebar.setValue(String.valueOf(hotel.getLebar()));
                                int available = hotel.getTotal() - hotel.getSedang_disewa();
                                sisa.setValue(String.valueOf(available));

                                loadProductPictures();
                                getFacilities();
                                getHotelReviews(id_kamar, 0);
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

    private void getFacilities(){
        Boolean[] initFacs = {false, false, false, false, false, false, false, false, false};
        String[] hotelFacs = hotel.getFasilitas().split("\\|");
        for (int i = 0; i < hotelFacs.length; i++) {
            int facNum = Integer.parseInt(hotelFacs[i]);
            initFacs[facNum] = true;
        }
        facsIncluded.setValue(initFacs);
    }

    public void getHotelReviews(String id_hotel, int skor){
        final int score = scoreList[skor];
        reviewsLoading.setValue(true);
        revVMs.setValue(new ArrayList<>());
        if(score == 0){
            reviewDB.getItemReviews(id_hotel).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
        reviewDB.getItemReviewsFiltered(id_hotel, score, 5).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
        String[] daftarGambar = hotel.getDaftar_gambar().split("\\|");

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
}
