package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.ChatConvDBAccess;
import com.example.sellerapp.models.CommentDBAccess;
import com.example.sellerapp.models.Hotel;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.ReviewDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.HotelItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HotelHomeViewModel {

    private AkunDBAccess akunDB;
    private PesananJanjitemuDBAccess orderDB;
    private ChatConvDBAccess chatDB;
    private ReviewDBAccess revDB;
    private HotelDBAccess hotelDB;
    private CommentDBAccess discDB;

    public HotelHomeViewModel(Application application) {
        akunDB = new AkunDBAccess(application);
        orderDB = new PesananJanjitemuDBAccess();
        chatDB = new ChatConvDBAccess();
        revDB = new ReviewDBAccess();
        hotelDB = new HotelDBAccess();
        discDB = new CommentDBAccess();
    }

    private MutableLiveData<Boolean[]> doneCounting = new MutableLiveData<>();
    private MutableLiveData<Boolean> hotelLoading = new MutableLiveData<>(true);
    private MutableLiveData<String> hotelName = new MutableLiveData<>("");
    private MutableLiveData<Integer[]> importantThingsCount = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> hotelVMs = new MutableLiveData<>();

    public void arrangeImportantThings(){
        Boolean[] done = new Boolean[6];
        Integer[] count = new Integer[6];
        for (int i = 0; i < 6; i++) {
            done[i] = false;
            count[i] = 0;
        }
        doneCounting.setValue(done);
        importantThingsCount.setValue(count);
        hotelLoading.setValue(true);
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    hotelName.setValue(accountsGot.get(0).getNama());

                    orderDB.getOrdersHomePage(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            if(results.size() > 0){
                                for (int i = 0; i < results.size(); i++) {
                                    int status = results.get(i).getLong("status").intValue();
                                    addImportantThing(status-1, 1);
                                    if(i == results.size() -1){
                                        setDoneCounting(0);
                                        setDoneCounting(1);
                                        setDoneCounting(2);
                                    }
                                }
                            }
                            else{
                                setDoneCounting(0);
                                setDoneCounting(1);
                                setDoneCounting(2);
                            }
                        }
                    });

                    chatDB.getUnreadChats(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            addImportantThing(3, results.size());
                            setDoneCounting(3);
                        }
                    });

                    hotelVMs.setValue(new ArrayList<>());
                    hotelDB.getHotelRooms(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> hotelResults = queryDocumentSnapshots.getDocuments();
                            if(hotelResults.size() <= 0){
                                hotelLoading.setValue(false);
                                addImportantThing(4, 0);
                                addImportantThing(5, 0);
                                setDoneCounting(4);
                                setDoneCounting(5);
                            }
                            else{
                                ArrayList<Hotel> problemHotels = new ArrayList<>();
                                for (int i = 0; i < hotelResults.size(); i++) {
                                    final int index = i;
                                    //untuk hitung DISKUSI BELUM DIBACA
                                    discDB.getUnreadDiscussions(hotelResults.get(i).getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                            addImportantThing(4, results.size());
                                            if(index == hotelResults.size() - 1){
                                                setDoneCounting(4);
                                            }
                                        }
                                    });

                                    //untuk hitung ULASAN BELUM DIBALAS
                                    revDB.getUnreturnedReviews(hotelResults.get(i).getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                                            addImportantThing(5, results.size());
                                            if(index == hotelResults.size() - 1){
                                                setDoneCounting(5);
                                            }
                                        }
                                    });

                                    //untuk hitung PRODUK BERMASALAH (stok/variasi habis, nonaktif)
                                    Hotel hotel = new Hotel(hotelResults.get(i));
                                    if(hotel.getSedang_disewa() > 0){
                                        problemHotels.add(hotel);
                                    }
                                    else if(!hotel.isAktif()){
                                        problemHotels.add(hotel);
                                    }
                                }
                                addHotelsToLiveData(problemHotels);
                            }
                        }
                    });
                }
            }
        });
        akunDB.getSavedAccounts();
    }

    private void addImportantThing(int index, int value){
        Integer[] container = importantThingsCount.getValue();
        container[index] += value;
        importantThingsCount.setValue(container);
    }

    public LiveData<Boolean[]> isDoneCounting(){
        return doneCounting;
    }

    public LiveData<Boolean> isHotelLoading(){
        return hotelLoading;
    }

    private void setDoneCounting(int index){
        Boolean[] done = doneCounting.getValue();
        done[index] = true;
        doneCounting.setValue(done);
    }

    public LiveData<String> getHotelName(){
        return hotelName;
    }

    public LiveData<Integer[]> getImportantThings(){
        return importantThingsCount;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getHotelVMs(){
        return hotelVMs;
    }

    private void addHotelsToLiveData(ArrayList<Hotel> hotels){
        ArrayList<HotelItemViewModel> currentVMs = hotelVMs.getValue();
        for (Hotel h:
                hotels) {
            currentVMs.add(new HotelItemViewModel(h));
        }
        hotelVMs.setValue(currentVMs);
        hotelLoading.setValue(false);
    }
}
