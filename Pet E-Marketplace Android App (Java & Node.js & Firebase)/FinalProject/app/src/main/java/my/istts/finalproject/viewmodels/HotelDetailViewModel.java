package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.Review;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ReviewItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HotelDetailViewModel {
    private AkunDBAccess akunDB;
    private CartDBAccess cartDB;
    private HotelDBAccess hotelDB;
    private FavoritDBAccess favDB;
    private ReviewDBAccess reviewDB;
    private Storage storage;
    private Application app;

    public HotelDetailViewModel(Application app) {
        this.app = app;
        akunDB = new AkunDBAccess(app);
        cartDB = new CartDBAccess(app);
        favDB = new FavoritDBAccess(app);
        hotelDB = new HotelDBAccess();
        reviewDB = new ReviewDBAccess();
        storage = new Storage();
    }

    private MutableLiveData<Boolean> itemsInCart = new MutableLiveData<>();

    private MutableLiveData<String> nama = new MutableLiveData<>("");
    private MutableLiveData<String> harga = new MutableLiveData<>("");
    private MutableLiveData<String> deskripsi = new MutableLiveData<>("");
    private MutableLiveData<Integer> tersedia = new MutableLiveData<>(0);
    private MutableLiveData<Integer> jumlahSewa = new MutableLiveData<>();
    private MutableLiveData<Integer> panjang = new MutableLiveData<>();
    private MutableLiveData<Integer> lebar = new MutableLiveData<>();
    private MutableLiveData<Boolean> aktif = new MutableLiveData<>();
    private MutableLiveData<String> nilai = new MutableLiveData<>("");
    private MutableLiveData<String[]> pictures = new MutableLiveData<>(new String[4]);
    private MutableLiveData<Boolean[]> facsIncluded = new MutableLiveData<>(new Boolean[9]);
    private MutableLiveData<Boolean> deleted = new MutableLiveData<>();

    private MutableLiveData<String> ownerName = new MutableLiveData<>("");
    private MutableLiveData<String> ownerPic = new MutableLiveData<>("");

    private String emailUser;
    private Hotel kamarHotel;
    private int[] scoreList = {0, 5, 4, 3, 2, 1};
    private MutableLiveData<String> emailHotel = new MutableLiveData<>("");
    private MutableLiveData<String> toastMsg = new MutableLiveData<>("");
    private MutableLiveData<Boolean> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> cartLoading = new MutableLiveData<>();

    private Favorit favorit;
    private MutableLiveData<Boolean> favoritEnabled = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> favorited = new MutableLiveData<>();

    private MutableLiveData<ArrayList<ReviewItemViewModel>> reviews = new MutableLiveData<>();
    private MutableLiveData<Boolean> revLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> hotelRooms = new MutableLiveData<>();
    private MutableLiveData<Boolean> hotelLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> reccRooms = new MutableLiveData<>();
    private MutableLiveData<Boolean> reccLoading = new MutableLiveData<>();

    public void setKamar(String id_kamar){
        Boolean[] initFacs = new Boolean[9];
        String[] initPics = new String[4];
        for (int i = 0; i < 9; i++) {
            if(i < 4){
                initPics[i] = "";
            }
            initFacs[i] = false;
        }
        pictures.setValue(initPics);
        facsIncluded.setValue(initFacs);

        checkCart();

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();

                    hotelDB.seeHotel(id_kamar);

                    hotelDB.getRoomById(id_kamar).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            if(doc.getData() != null){
                                Hotel h = new Hotel(doc);
                                emailHotel.setValue(h.getemail_pemilik());
                                kamarHotel = h;
                                nama.setValue(h.getNama());
                                harga.setValue(h.getTSHarga());
                                deskripsi.setValue(h.getDeskripsi());
                                aktif.setValue(h.isAktif());
                                tersedia.setValue(h.getTotal()-h.getSedang_disewa());
                                jumlahSewa.setValue(h.getTersewa());
                                panjang.setValue(h.getPanjang());
                                lebar.setValue(h.getLebar());

                                String[] hotelPics = h.getDaftar_gambar().split("\\|", -1);
                                for (int i = 0; i < hotelPics.length; i++) {
                                    final int index = i;
                                    storage.getPictureUrlFromName(hotelPics[i]).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            assignPics(uri.toString(), index);
                                        }
                                    });
                                }
                                
                                akunDB.getAccByEmail(h.getemail_pemilik()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.getData() != null){
                                            storage.getPictureUrlFromName(h.getemail_pemilik()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    ownerPic.setValue(uri.toString());
                                                    ownerName.setValue(documentSnapshot.getString("nama"));
                                                }
                                            });
                                        }
                                    }
                                });


                                getFacilities(h.getFasilitasArr());
                                getFavorite(emailUser, h.getId_kamar());
                                getHotelReviews(h.getId_kamar(), 0);
                                getHotelsOtherRooms(emailUser, h.getemail_pemilik());
                                getHotelsOtherOwner(emailUser, h.getemail_pemilik());
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

    private void assignPics(String pic, int index){
        String[] currentPics = pictures.getValue();
        currentPics[index] = pic;
        pictures.setValue(currentPics);
    }

    private void getFacilities(String[] fasilitas){
        Boolean[] currentFacs = facsIncluded.getValue();
        for (String fac : fasilitas) {
            int facNumber = Integer.parseInt(fac);
            currentFacs[facNumber] = true;
        }
        facsIncluded.setValue(currentFacs);
    }

    private void getFavorite(String email, String id_kamar){
        favoritEnabled.setValue(false);

        favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
            @Override
            public void onGot(List<Favorit> favs) {
                if(favs.size() > 0){
                    favorit = favs.get(0);
                    favorited.setValue(true);
                }
                else{
                    favorited.setValue(false);
                }
                favoritEnabled.setValue(true);
            }
        });

        favDB.getFavorit(email, id_kamar);
    }

    public void getHotelReviews(String id_kamar, int skor){
        final int score = scoreList[skor];
        revLoading.setValue(true);
        reviews.setValue(new ArrayList<>());
        if(score == 0){
            reviewDB.getAllReviewsOfItem(id_kamar).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

        reviewDB.getItemReviewsFiltered(id_kamar, score, 5).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Review> revArr = new ArrayList<>();
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        revArr.add(new Review(doc));
                    }
                    addReviewsVMs(revArr);
                }
                else{
                    revLoading.setValue(false);
                }
            }
        });
    }

    private void getHotelsOtherRooms(String email, String hp_pemilik){
        hotelLoading.setValue(false);
        hotelRooms.setValue(new ArrayList<>());

        hotelDB.getTenHotelsOwned(hp_pemilik).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Hotel> hotelArr = new ArrayList<>();
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        if(!doc.getId().equals(kamarHotel.getId_kamar())){
                            hotelArr.add(new Hotel(doc));
                        }
                    }
                    addHotelVMs(hotelArr, email);
                }
                else{
                    hotelLoading.setValue(false);
                }
            }
        });
    }

    private void getHotelsOtherOwner(String email, String hp_pemilik){
        reccLoading.setValue(false);
        reccRooms.setValue(new ArrayList<>());

        hotelDB.getTenHotelsNotOwned(hp_pemilik).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Hotel> reccArr = new ArrayList<>();
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        reccArr.add(new Hotel(doc));
                    }
                    addReccHotelVMs(reccArr, email);
                }
                else{
                    reccLoading.setValue(false);
                }
            }
        });
    }

    private void addReviewsVMs(ArrayList<Review> revs){
        ArrayList<ReviewItemViewModel> currentVMs = reviews.getValue();
        for (Review rev:
             revs) {
            if(currentVMs.size() < 5){
                currentVMs.add(new ReviewItemViewModel(rev, app));
            }
        }
        reviews.setValue(currentVMs);
        revLoading.setValue(false);
    }

    private void addHotelVMs(ArrayList<Hotel> hotels, String email){
        ArrayList<HotelItemViewModel> currentVMs = hotelRooms.getValue();
        for (Hotel hotel:
                hotels) {
            currentVMs.add(new HotelItemViewModel(hotel, email, app));
        }
        hotelRooms.setValue(currentVMs);
        hotelLoading.setValue(false);
    }

    private void addReccHotelVMs(ArrayList<Hotel> hotels, String email){
        ArrayList<HotelItemViewModel> currentVMs = reccRooms.getValue();
        for (Hotel hotel:
                hotels) {
            currentVMs.add(new HotelItemViewModel(hotel, email, app));
        }
        reccRooms.setValue(currentVMs);
        reccLoading.setValue(false);
    }

    public void setFavorite(){
        favoritEnabled.setValue(false);
        if(favorit != null){
            //skrg di nonfavoritkan
            favDB.setFavoriteDeletedListener(new FavoritDBAccess.onFavoriteDeleted() {
                @Override
                public void onDeleted() {
                    favoritEnabled.setValue(true);
                    favorited.setValue(false);
                    favorit = null;
                }
            });

            favDB.deleteFavorite(favorit.getId());
        }
        else{
            //skrg difavoritkan
            //untuk menghindari kemungkinan addfavorit double, maka sebelum add cek apakah sudah ada di db
            favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                @Override
                public void onGot(List<Favorit> favs) {
                    if(favs.size() > 0){
                        favoritEnabled.setValue(true);
                        favorited.setValue(true);
                        favorit = favs.get(0);
                    }
                    else{
                        favorit = new Favorit(emailUser, kamarHotel.getId_kamar(), 1);
                        favDB.setFavoriteAddedListener(new FavoritDBAccess.onFavoriteAdded() {
                            @Override
                            public void onAdded() {
                                favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                                    @Override
                                    public void onGot(List<Favorit> favs) {
                                        if(favs.size() > 0){
                                            favorit = favs.get(0);
                                            favoritEnabled.setValue(true);
                                            favorited.setValue(true);
                                        }
                                    }
                                });

                                favDB.getFavorit(emailUser, kamarHotel.getId_kamar());
                            }
                        });

                        favDB.addFavorit(favorit);
                    }
                }
            });

            favDB.getFavorit(emailUser, kamarHotel.getId_kamar());
        }
    }

    public void addCart(){
        cartLoading.setValue(true);
        cartDB.setItemChangedListener(new CartDBAccess.onCartItemsChanged() {
            @Override
            public void onChangedItems() {
                //listener untuk jika sdh berhasil add/update
                itemsInCart.setValue(true);
                toastMsg.setValue("Booking Kamar Berhasil Ditambahkan");
                error.setValue(false);
                cartLoading.setValue(false);
            }
        });

        cartDB.setFoundListener(new CartDBAccess.onCartFound() {
            @Override
            public void onFound(List<Cart> cartItems) {
                //pertama cari dulu apakah item sudah ada di cart
                if(cartItems.size() > 0){
                    //sudah ada di cart, tinggal update
                    Cart cart = cartItems.get(0);
                    int available = kamarHotel.getTotal() - kamarHotel.getSedang_disewa();
                    if(cart.getJumlah() >= available){
                        //sudah sebanyak batas stok kamar yang tersedia di hotel, tidak bisa ditambah lagi
                        toastMsg.setValue("Jumlah Di Keranjang Tidak Dapat Melebihi Kamar Yang Tersedia");
                        error.setValue(true);
                        cartLoading.setValue(false);
                    }
                    else{
                        cart.addJumlah();
                        cartDB.updateCartItem(cart);
                    }
                }
                else{
                    //belum ada di cart, add cart item baru
                    Cart cart = new Cart(emailHotel.getValue(), kamarHotel.getId_kamar(), "",1, 2);
                    cartDB.insertCartItem(cart);
                }
            }
        });

        cartDB.searchCartItem(kamarHotel.getId_kamar());
    }


    //GETTERS
    public LiveData<String> getEmailHotel(){ return emailHotel;}

    public LiveData<String> getNama(){ return nama; }

    public LiveData<String> getHarga(){ return harga; }

    public LiveData<String> getDesc(){ return deskripsi; }

    public LiveData<Integer> getAvailable(){ return tersedia; }

    public LiveData<Integer> getSold(){ return jumlahSewa; }

    public LiveData<Integer> getWidth(){ return panjang; }

    public LiveData<Integer> getLength(){ return lebar; }

    public LiveData<String> getScore(){ return nilai; }

    public LiveData<Boolean> getAktif(){ return aktif; }

    public LiveData<Boolean> isDeleted(){ return deleted; }

    public LiveData<String[]> getPictures(){ return pictures; }

    public LiveData<Boolean[]> isFacsIncluded(){ return facsIncluded; }

    public LiveData<String> getOwnerName(){ return ownerName; }

    public LiveData<String> getOwnerPic(){ return ownerPic; }

    public LiveData<Boolean> getError(){ return error; }

    public LiveData<String> getToastMsg(){ return toastMsg; }

    public LiveData<Boolean> isCartLoading(){ return cartLoading; }

    public LiveData<Boolean> isItemsInCart(){ return itemsInCart; }

    public LiveData<Boolean> isFavoritEnabled(){ return favoritEnabled; }

    public LiveData<Boolean> isFavorited(){ return favorited; }

    public LiveData<ArrayList<ReviewItemViewModel>> getReviews(){
        return reviews;
    }

    public LiveData<Boolean> isRevLoading(){ return revLoading; }

    public LiveData<ArrayList<HotelItemViewModel>> getHotelRooms(){
        return hotelRooms;
    }

    public LiveData<Boolean> isHotelLoading(){ return hotelLoading; }

    public LiveData<ArrayList<HotelItemViewModel>> getReccRooms(){
        return reccRooms;
    }

    public LiveData<Boolean> isReccLoading(){ return reccLoading; }
}
