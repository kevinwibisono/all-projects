package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Hotel;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.viewmodels.itemviewmodels.HotelItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HotelListViewModel {

    private AkunDBAccess akunDB;
    private HotelDBAccess hotelDB;

    public HotelListViewModel(Application application) {
        akunDB = new AkunDBAccess(application);
        hotelDB = new HotelDBAccess();
        initFilter(filterChipsCheckedActive);
        initFilter(filterChipsCheckedFacs);
        initFilter(filterChipsCheckedSort);
    }

    private String fieldToSort;
    private Query.Direction sortDirection;
    private ArrayList<Integer> fasilitas = new ArrayList<>();
    private Boolean statusFilter;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String[]> activeFilters;
    private MutableLiveData<ArrayList<String>> facsActive;
    public MutableLiveData<boolean[]> filterChipsCheckedActive = new MutableLiveData<>(new boolean[2]);
    public MutableLiveData<boolean[]> filterChipsCheckedFacs = new MutableLiveData<>(new boolean[10]);
    public MutableLiveData<boolean[]> filterChipsCheckedSort = new MutableLiveData<>(new boolean[6]);
    private MutableLiveData<ArrayList<HotelItemViewModel>> hotelVMs;
    public MutableLiveData<String> hotelName = new MutableLiveData<>("");

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<String[]> getActiveFilters(){
        if(activeFilters == null){
            String[] initTexts = {"", ""};
            activeFilters = new MutableLiveData<>(initTexts);
        }
        return activeFilters;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getHotelVMs(){
        if(hotelVMs == null){
            hotelVMs = new MutableLiveData<>(new ArrayList<>());
        }

        return hotelVMs;
    }

    public LiveData<ArrayList<String>> getFacsActive(){
        if(facsActive == null){
            facsActive = new MutableLiveData<>(new ArrayList<>());
        }

        return facsActive;
    }

    public void getHotelRooms(){
        loading.setValue(true);
        hotelVMs.setValue(new ArrayList<>());
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    hotelDB.getHotelRoomsFiltered(accountsGot.get(0).getEmail(), fieldToSort, sortDirection, statusFilter).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            if(results.size() > 0){
                                ArrayList<Hotel> dbHotel = new ArrayList<>();
                                for (int i = 0; i < results.size(); i++){
                                    Hotel hotel = new Hotel(results.get(i));
                                    //setelah dapat list produk, lakukan operasi LIKE dengan keyword
                                    if(hotel.getNama().toLowerCase().contains(hotelName.getValue().toLowerCase())){
                                        //dan cek dulu dengan list kategori yang telah dipilih
                                        if(fasilitas.size() <= 0 || categoriesFulfilled(hotel)){
                                            dbHotel.add(hotel);
                                        }
                                    }
                                }
                                addHotelVM(dbHotel);
                            }
                            else{
                                loading.setValue(false);
                            }
                            hotelName.setValue("");
                        }
                    });
                }
                else{
                    loading.setValue(false);
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public boolean categoriesFulfilled(Hotel h){
        int sameCategory = 0;
        for (int fas:
             fasilitas) {
            if(h.getFasilitas().contains(fas+"")){
                sameCategory++;
            }
        }
        if(sameCategory > 0){
            return false;
        }
        else{
            return true;
        }
    }

    public void changeFilterSort(){
        //untuk keperluan chip filter, menampilkan filter-filter yang sedang aktif
        facsActive.setValue(new ArrayList<>());
        String[] chipsFilterText = {"", "", ""};
        String[] kategoriList = {"Makanan & Minuman", "Ber-AC", "Kamar Privat", "Akses CCTV", "Update Harian", "Taman Bermain", "Training & Olahraga", "Antar Jemput", "Grooming"};
        String[] sortFieldsText = {"Terbaru", "Terdahulu", "Paling Diminati", "Kurang Diminati", "Harga Tertinggi", "Harga Terendah"};

        statusFilter = null;
        fasilitas = new ArrayList<>();
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

        for (int i=0;i<9;i++){
            if(filterChipsCheckedFacs.getValue()[i]){
                //agar bisa or, maka firebase membutuhkan sebuah array untuk dibandingkan di query
                fasilitas.add(i);
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
        getHotelRooms();
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

        getHotelRooms();
    }

    public void deleteFacsFilter(int index){
        //cari dulu mana boolean checked untuk bottom dialog fragment bagian kategori yang perlu dimatikan centangnya
        boolean[] currentFilterCategories = filterChipsCheckedFacs.getValue();
        currentFilterCategories[fasilitas.get(index)] = false;
        filterChipsCheckedFacs.setValue(currentFilterCategories);

        fasilitas.remove(index);
        ArrayList<String> facsHolder = facsActive.getValue();
        facsHolder.remove(index);
        facsActive.setValue(facsHolder);
    }

    private void addCategoryToLiveData(String categoryText){
        ArrayList<String> facsHolder = facsActive.getValue();
        facsHolder.add(categoryText);
        facsActive.setValue(facsHolder);
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

    private void addHotelVM(ArrayList<Hotel> hotels){
        ArrayList<HotelItemViewModel> holder = hotelVMs.getValue();
        for (Hotel h:
             hotels) {
            holder.add(new HotelItemViewModel(h));
        }
        hotelVMs.setValue(holder);
        loading.setValue(false);
    }
}
