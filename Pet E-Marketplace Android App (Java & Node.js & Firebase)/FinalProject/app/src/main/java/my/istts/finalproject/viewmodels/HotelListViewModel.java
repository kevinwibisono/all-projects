package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HotelListViewModel {
    private HotelDBAccess hotelDB;
    private AkunDBAccess akunDB;
    private CartDBAccess cartDB;
    private Application app;

    public HotelListViewModel(Application app){
        this.app = app;
        hotelDB = new HotelDBAccess();
        akunDB = new AkunDBAccess(app);
        cartDB = new CartDBAccess(app);
    }

    private MutableLiveData<Boolean> itemsInCart = new MutableLiveData<>();

    private String[] facsList = {"Makanan & Minuman", "Ber-AC", "Kamar Privat", "Akses CCTV", "Update Harian", "Taman Bermain",
            "Training & Olahraga", "Antar Jemput", "Grooming"};
    private MutableLiveData<ArrayList<String>> activeFacs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> activeFacsInt = new MutableLiveData<>();
    public MutableLiveData<boolean[]> chipFacsChecked = new MutableLiveData<>(new boolean[9]);

    private String[] sortTypes = {"ulasan", "harga", "harga"};
    private String[] sortNames = {"Ulasan", "Harga Terendah", "Harga Tertinggi"};
    private Query.Direction[] sortDirections = {Query.Direction.DESCENDING, Query.Direction.ASCENDING, Query.Direction.DESCENDING};
    private String fieldSorted = "";
    private Query.Direction directionSort = Query.Direction.ASCENDING;
    private MutableLiveData<String> activeSort = new MutableLiveData<>("");
    public MutableLiveData<boolean[]> chipSortChecked = new MutableLiveData<>(new boolean[4]);

    private String searchKeyword = "";

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> hotelVMs = new MutableLiveData<>();

    public void initFilters(){
        activeFacs.setValue(new ArrayList<>());
        activeFacsInt.setValue(new ArrayList<>());

        boolean[] initFacs = new boolean[9];
        boolean[] initSorts = new boolean[4];
        for (int i = 0; i < 9; i++) {
            if(i < 4){
                initSorts[i] = false;
            }
            initFacs[i] = false;
        }
        chipFacsChecked.setValue(initFacs);
        chipSortChecked.setValue(initSorts);

    }

    public void getHotelsFiltered(){
        loading.setValue(true);
        hotelVMs.setValue(new ArrayList<>());

        checkCart();

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    hotelDB.getHotelRoomsFiltered(fieldSorted, directionSort).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<Hotel> hotels = new ArrayList<>();
                                for (DocumentSnapshot hotelDoc:
                                     queryDocumentSnapshots.getDocuments()) {
                                    Hotel hotel = new Hotel(hotelDoc);
                                    if(hotel.getNama().toLowerCase().contains(searchKeyword.toLowerCase()) && categoriesFulfilled(hotel, activeFacsInt.getValue())){
                                        hotels.add(hotel);
                                    }
                                }
                                addHotelsToVM(hotels, accountsGot.get(0).getEmail(), null);
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

    private boolean categoriesFulfilled(Hotel h, ArrayList<Integer> facsSelected){
        if(facsSelected.size() <= 0){
            return true;
        }
        else{
            int sameCategory = 0;
            for (int fac:
                    facsSelected) {
                if(h.getFasilitas().contains(fac+"")){
                    sameCategory++;
                }
            }
            if(sameCategory > 0){
                return true;
            }
            else{
                return false;
            }
        }
    }

    private void checkCart(){
        cartDB.setItemsGotListener(new CartDBAccess.onCartItemsGot() {
            @Override
            public void onGotItems(List<Cart> items) {
                if(items.size() > 0){
                    itemsInCart.setValue(true);
                }
            }
        });

        cartDB.getAllCartItemsByType(2);
    }

    private void addHotelsToVM(ArrayList<Hotel> hotels, String email, DocumentSnapshot last){
        ArrayList<HotelItemViewModel> currentVMs = hotelVMs.getValue();
        for (Hotel hotel:
             hotels) {
            currentVMs.add(new HotelItemViewModel(hotel, email, app));
        }
        hotelVMs.setValue(currentVMs);
        loading.setValue(false);
    }

    public void setFilters(){
        //untuk tampilan quick filter
        activeFacs.setValue(new ArrayList<>());
        activeFacsInt.setValue(new ArrayList<>());
        activeSort.setValue("");

        //untuk keperluan query db
        fieldSorted = "";
        directionSort = Query.Direction.ASCENDING;

        for (int i = 0; i < 9; i++) {
            if(i < 4 && chipSortChecked.getValue()[i]){
                fieldSorted = sortTypes[i];
                directionSort = sortDirections[i];
                activeSort.setValue(sortNames[i]);
            }
            if(chipFacsChecked.getValue()[i]){
                addCheckedFacility(i);
            }
        }

        getHotelsFiltered();
    }

    public void setFac(int fac){
        initFilters();

        ArrayList<String> currentCategories = activeFacs.getValue();
        ArrayList<Integer> currentCategoriesIndexes = activeFacsInt.getValue();
        currentCategories.add(facsList[fac]);
        currentCategoriesIndexes.add(fac);
        activeFacs.setValue(currentCategories);
        activeFacsInt.setValue(currentCategoriesIndexes);

        boolean[] currentChecked = chipFacsChecked.getValue();
        currentChecked[fac] = true;
        chipFacsChecked.setValue(currentChecked);

        getHotelsFiltered();
    }

    public void setSearchKeyword(String searchKeyword){
        initFilters();
        this.searchKeyword = searchKeyword;

        getHotelsFiltered();
    }

    private void addCheckedFacility(int index){
        ArrayList<String> currentCategories = activeFacs.getValue();
        ArrayList<Integer> currentCategoriesIndexes = activeFacsInt.getValue();
        currentCategories.add(facsList[index]);
        currentCategoriesIndexes.add(index);
        activeFacs.setValue(currentCategories);
        activeFacsInt.setValue(currentCategoriesIndexes);
    }

    public void removeSort(){
        activeSort.setValue("");
        fieldSorted = "";
        directionSort = Query.Direction.ASCENDING;

        boolean[] sortChecked = new boolean[4];
        for (int i = 0; i < 4; i++) {
            sortChecked[i] = false;
        }
        chipSortChecked.setValue(sortChecked);

        getHotelsFiltered();
    }

    public void removeFac(int position){
        ArrayList<String> currentCategories = activeFacs.getValue();
        ArrayList<Integer> currentCategoriesIndexes = activeFacsInt.getValue();
        int facNum = currentCategoriesIndexes.get(position);
        currentCategories.remove(position);
        currentCategoriesIndexes.remove(position);
        activeFacs.setValue(currentCategories);
        activeFacsInt.setValue(currentCategoriesIndexes);

        boolean[] categories = chipFacsChecked.getValue();
        categories[facNum] = false;
        chipFacsChecked.setValue(categories);

        getHotelsFiltered();
    }


    public LiveData<ArrayList<String>> getActiveFacs() {
        return activeFacs;
    }

    public LiveData<String> getActiveSort() {
        return activeSort;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> isItemInCart() {
        return itemsInCart;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getHotelRooms() {
        return hotelVMs;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }
}
