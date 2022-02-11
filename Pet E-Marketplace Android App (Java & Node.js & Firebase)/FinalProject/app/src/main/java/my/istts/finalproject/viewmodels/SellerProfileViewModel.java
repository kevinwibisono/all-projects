package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.DetailPenjual;
import my.istts.finalproject.models.DetailPenjualDBAccess;
import my.istts.finalproject.models.FollowDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.PaketGrooming;
import my.istts.finalproject.models.PaketGroomingDBAccess;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Promo;
import my.istts.finalproject.models.PromoDBAccess;
import my.istts.finalproject.models.Review;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.ClinicItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.PaketGroomingItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.PromoItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ReviewItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SellerProfileViewModel {
    private DetailPenjualDBAccess detailDB;
    private ProductDBAccess productDBAccess;
    private HotelDBAccess hotelDBAccess;
    private PaketGroomingDBAccess paketGroomingDBAccess;
    private ReviewDBAccess reviewDBAccess;
    private FollowDBAccess followDBAccess;
    private PromoDBAccess promoDBAccess;
    private AkunDBAccess akunDBAccess;
    private CartDBAccess cartDB;
    private PesananJanjitemuDBAccess orderDB;
    private Application app;

    public SellerProfileViewModel(Application app){
        this.app = app;
        akunDBAccess = new AkunDBAccess(app);
        detailDB = new DetailPenjualDBAccess();
        productDBAccess = new ProductDBAccess();
        hotelDBAccess = new HotelDBAccess();
        paketGroomingDBAccess = new PaketGroomingDBAccess();
        followDBAccess = new FollowDBAccess();
        reviewDBAccess = new ReviewDBAccess();
        promoDBAccess = new PromoDBAccess();
        orderDB = new PesananJanjitemuDBAccess();
        cartDB = new CartDBAccess(app);
    }

    private String email;
    private int[] scoreList = {0, 5, 4, 3, 2, 1};

    private MutableLiveData<Boolean> itemsInCartAppo = new MutableLiveData<>();

    private MutableLiveData<ArrayList<PromoItemViewModel>> shopPromos = new MutableLiveData<>();
    private MutableLiveData<Boolean> shopPromosLoading = new MutableLiveData<>();

    private MutableLiveData<ArrayList<PaketGroomingItemViewModel>> groomingPacks = new MutableLiveData<>();
    private MutableLiveData<Boolean> groomingLoading = new MutableLiveData<>();

    private MutableLiveData<ArrayList<ProductItemViewModel>> topProducts = new MutableLiveData<>();
    private MutableLiveData<Boolean> topProductsLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProductItemViewModel>> allProducts = new MutableLiveData<>();
    private MutableLiveData<Boolean> allProductsLoading = new MutableLiveData<>();

    private MutableLiveData<ArrayList<HotelItemViewModel>> topHotel = new MutableLiveData<>();
    private MutableLiveData<Boolean> topHotelLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> allHotel = new MutableLiveData<>();
    private MutableLiveData<Boolean> allHotelLoading = new MutableLiveData<>();

    private MutableLiveData<String> sellerName = new MutableLiveData<>("");
    private MutableLiveData<Integer> sellerFollower = new MutableLiveData<>(0);
    private MutableLiveData<String[]> sellerPosters = new MutableLiveData<>(new String[]{"", "", "", ""});
    private MutableLiveData<String> sellerPic = new MutableLiveData<>("");
    private MutableLiveData<String> sellerDesc = new MutableLiveData<>("");
    private MutableLiveData<Float> sellerScore = new MutableLiveData<>();

    private MutableLiveData<String> followId = new MutableLiveData<>("");
    private MutableLiveData<Boolean> followEnabled = new MutableLiveData<>(false);

    private MutableLiveData<Boolean> clinicOpenNow = new MutableLiveData<>(false);
    private MutableLiveData<String[]> clinicOpens = new MutableLiveData<>();
    private MutableLiveData<String[]> clinicCloses = new MutableLiveData<>();
    private MutableLiveData<String> clinicAddress = new MutableLiveData<>();
    private MutableLiveData<String> clinicCoors = new MutableLiveData<>("");

    private MutableLiveData<ArrayList<ReviewItemViewModel>> reviews = new MutableLiveData<>();
    private MutableLiveData<Boolean> revLoading = new MutableLiveData<>();

    private MutableLiveData<Integer> role = new MutableLiveData<>();

    public void getSellerDetail(String email_seller){
        akunDBAccess.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();
                    getFollow(email_seller);

                    akunDBAccess.getAccByEmail(email_seller).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot akunDoc) {
                            if(akunDoc.getData() != null){
                                sellerName.setValue(akunDoc.getString("nama"));

                                detailDB.getDBAkunDetail(email_seller).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        DetailPenjual detailPenjual = new DetailPenjual(documentSnapshot);
                                        role.setValue(detailPenjual.getRole());

                                        getPicturePosters(email_seller, detailPenjual.getPosterSplitted());
                                        sellerDesc.setValue(detailPenjual.getDeskripsi());

                                        if(detailPenjual.getRole() == 3){
                                            ClinicItemViewModel clinicVM = new ClinicItemViewModel(detailPenjual, email_seller, akunDoc.getString("nama"));
                                            clinicOpenNow.setValue(clinicVM.isClinicOpen());
                                            clinicAddress.setValue(detailPenjual.getAlamat());
                                            clinicCoors.setValue(detailPenjual.getKoordinat());
                                            clinicOpens.setValue(detailPenjual.getJamBukaSplitted());
                                            clinicCloses.setValue(detailPenjual.getJamTutupSplitted());
                                            checkAppo(email);
                                        }
                                        else{
                                            checkCart(detailPenjual.getRole());
                                        }

                                        followDBAccess.getFollowers(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                sellerFollower.setValue(queryDocumentSnapshots.getDocuments().size());
                                            }
                                        });

                                        if(detailPenjual.getRole() == 0){
                                            //shop
                                            countScoreShop(email_seller);
                                            getSellerProducts(email_seller);
                                            getShopPromos(email_seller);
                                        }
                                        else if(detailPenjual.getRole() % 2 > 0){
                                            //groomer & klinik
                                            countScoreGroomerClinic(email_seller, 0);
                                            getGroomerPackages(email_seller);
                                        }
                                        else if(detailPenjual.getRole() == 2){
                                            //hotel
                                            countScoreHotel(email_seller);
                                            getHotelRooms(email_seller);
                                        }
                                    }
                                });

                            }
                        }
                    });

                }
            }
        });

        akunDBAccess.getSavedAccounts();
    }

    public void checkCart(int role){
        cartDB.setItemsGotListener(new CartDBAccess.onCartItemsGot() {
            @Override
            public void onGotItems(List<Cart> items) {
                if(items.size() > 0){
                    itemsInCartAppo.setValue(true);
                }
                else{
                    itemsInCartAppo.setValue(false);
                }
            }
        });

        cartDB.getAllCartItemsByType(role);
    }

    private void checkAppo(String email_user){
        orderDB.getOrdersByTypeStatus(email_user, 3, 0).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    itemsInCartAppo.setValue(true);
                }
                else{
                    itemsInCartAppo.setValue(false);
                }
            }
        });
    }

    private void getPicturePosters(String picture, String[] posters){
        Storage storage = new Storage();

        for (int i = 0; i < posters.length; i++) {
            final int index = i;
            storage.getPictureUrlFromName(posters[i]).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    addSellerPoster(index, uri.toString());
                }
            });
        }

        storage.getPictureUrlFromName(picture).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                sellerPic.setValue(uri.toString());
            }
        });
    }

    private void getFollow(String email_seller){
        followDBAccess.getIsFollowing(email, email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    followId.setValue(queryDocumentSnapshots.getDocuments().get(0).getId());
                }
                else{
                    followId.setValue("");
                }
                followEnabled.setValue(true);
            }
        });
    }

    public void changeFollow(String email_seller){
        followEnabled.setValue(false);

        if(followId.getValue().equals("")){
            //tidak ada followid, jadi skrg follow
            followDBAccess.getIsFollowing(email, email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                        //jika sudah pernah follow, masukkan idnya
                        followId.setValue(queryDocumentSnapshots.getDocuments().get(0).getId());
                        followEnabled.setValue(true);
                        Integer currentFollower = sellerFollower.getValue();
                        currentFollower++;
                        sellerFollower.setValue(currentFollower);
                    }
                    else{
                        //jika belum follow, maka follow skrg
                        followDBAccess.followSeller(email, email_seller, role.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        followId.setValue(documentSnapshot.getId());
                                        followEnabled.setValue(true);
                                        Integer currentFollower = sellerFollower.getValue();
                                        currentFollower++;
                                        sellerFollower.setValue(currentFollower);
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
        else{
            //ada followid jadi skrg unfollow
            followDBAccess.unfollowSeller(followId.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    followId.setValue("");
                    followEnabled.setValue(true);
                    Integer currentFollower = sellerFollower.getValue();
                    currentFollower--;
                    sellerFollower.setValue(currentFollower);
                }
            });
        }
    }

    private void countScoreShop(String email_seller){
        sellerScore.setValue((float) 0.0);

        productDBAccess.getAllSellerProducts(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    for (DocumentSnapshot productDoc:
                         queryDocumentSnapshots) {
                        Product product = new Product(productDoc);
                        reviewDBAccess.getAllReviewsOfItem(product.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    int jumlahReview = queryDocumentSnapshots.getDocuments().size();
                                    float skorCtr = 0;
                                    for (DocumentSnapshot doc:
                                            queryDocumentSnapshots.getDocuments()) {
                                        Review review = new Review(doc);
                                        skorCtr += review.getNilai();
                                    }
                                    addScoreAvg(skorCtr/jumlahReview);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void countScoreHotel(String email_seller){
        sellerScore.setValue((float) 0.0);

        hotelDBAccess.getAllHotelsOwned(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    for (DocumentSnapshot hotelDoc:
                            queryDocumentSnapshots) {
                        Hotel hotel = new Hotel(hotelDoc);
                        reviewDBAccess.getAllReviewsOfItem(hotel.getId_kamar()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    int jumlahReview = queryDocumentSnapshots.getDocuments().size();
                                    float skorCtr = 0;
                                    for (DocumentSnapshot doc:
                                            queryDocumentSnapshots.getDocuments()) {
                                        Review review = new Review(doc);
                                        skorCtr += review.getNilai();
                                    }
                                    addScoreAvg(skorCtr/jumlahReview);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void addScoreAvg(float skor){
        Float scoreNow = sellerScore.getValue();
        if(scoreNow == 0.0){
            scoreNow = skor;
        }
        scoreNow = (scoreNow + skor)/2;
        sellerScore.setValue(scoreNow);
    }

    public void countScoreGroomerClinic(String email_seller, int idxTab){
        final int score = scoreList[idxTab];
        revLoading.setValue(true);
        reviews.setValue(new ArrayList<>());
        if(score == 0){
            reviewDBAccess.getAllReviewsOfItem(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int jumlahRev = queryDocumentSnapshots.getDocuments().size();
                    float skorNow = 0;
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        skorNow += doc.getLong("nilai");
                    }
                    sellerScore.setValue(skorNow/jumlahRev);
                }
            });
        }
        reviewDBAccess.getItemReviewsFiltered(email_seller, score, 5).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

    private void getGroomerPackages(String email_seller){
        groomingLoading.setValue(true);
        groomingPacks.setValue(new ArrayList<>());

        paketGroomingDBAccess.getPackages(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<PaketGrooming> packs = new ArrayList<>();
                    for (DocumentSnapshot paketDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        PaketGrooming paketGrooming = new PaketGrooming(paketDoc);
                        packs.add(paketGrooming);
                    }
                    addPacksToVM(packs);
                }
                else{
                    shopPromosLoading.setValue(false);
                }
            }
        });
    }

    private void addPacksToVM(ArrayList<PaketGrooming> packs){
        ArrayList<PaketGroomingItemViewModel> currentVMs = groomingPacks.getValue();
        for (PaketGrooming pack:
                packs) {
            currentVMs.add(new PaketGroomingItemViewModel(pack, app));
        }
        groomingPacks.setValue(currentVMs);
        groomingLoading.setValue(false);
    }

    private void getShopPromos(String email_seller){
        shopPromosLoading.setValue(true);
        shopPromos.setValue(new ArrayList<>());

        promoDBAccess.getShopPromos(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Promo> promos = new ArrayList<>();
                    for (DocumentSnapshot promoDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        Promo promo = new Promo(promoDoc);
                        promos.add(promo);
                    }
                    addPromosToVM(promos);
                }
                else{
                    shopPromosLoading.setValue(false);
                }
            }
        });
    }

    private void addPromosToVM(ArrayList<Promo> promos){
        ArrayList<PromoItemViewModel> currentVMs = shopPromos.getValue();
        for (Promo promo:
                promos) {
            currentVMs.add(new PromoItemViewModel(promo));
        }
        shopPromos.setValue(currentVMs);
        shopPromosLoading.setValue(false);
    }

    private void getSellerProducts(String email_seller){
        topProducts.setValue(new ArrayList<>());
        topProductsLoading.setValue(true);
        allProducts.setValue(new ArrayList<>());
        allProductsLoading.setValue(true);

        productDBAccess.getAllSellerProducts(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc:
                         queryDocumentSnapshots.getDocuments()) {
                        products.add(new Product(doc));
                    }
                    addProductToVM(products, false);
                }
                else{
                    allProductsLoading.setValue(false);
                }
            }
        });

        productDBAccess.getTopSellerProducts(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        products.add(new Product(doc));
                    }
                    addProductToVM(products, true);
                }
                else{
                    topProductsLoading.setValue(true);
                }
            }
        });
    }

    private void addProductToVM(ArrayList<Product> products, boolean top){
        ArrayList<ProductItemViewModel> currentVMs;
        if(top){
            currentVMs = topProducts.getValue();
            for (Product prod :
                    products) {
                currentVMs.add(new ProductItemViewModel(prod, email, app));
            }
            topProducts.setValue(currentVMs);

            topProductsLoading.setValue(false);
        }
        else{
            currentVMs = allProducts.getValue();
            for (Product prod :
                    products) {
                currentVMs.add(new ProductItemViewModel(prod, email, app));
            }
            allProducts.setValue(currentVMs);
            allProductsLoading.setValue(false);
        }
    }

    private void getHotelRooms(String email_seller){
        topHotel.setValue(new ArrayList<>());
        topHotelLoading.setValue(true);
        allHotel.setValue(new ArrayList<>());
        allHotelLoading.setValue(true);

        hotelDBAccess.getAllHotelsOwned(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Hotel> hotels = new ArrayList<>();
                for (DocumentSnapshot doc:
                        queryDocumentSnapshots.getDocuments()) {
                    hotels.add(new Hotel(doc));
                }
                addRoomsToVM(hotels, false);
            }
        });

        hotelDBAccess.getTopHotelRooms(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Hotel> hotels = new ArrayList<>();
                for (DocumentSnapshot doc:
                        queryDocumentSnapshots.getDocuments()) {
                    hotels.add(new Hotel(doc));
                }
                addRoomsToVM(hotels, true);
            }
        });
    }

    private void addRoomsToVM(ArrayList<Hotel> hotels, boolean top){
        ArrayList<HotelItemViewModel> currentVMs;
        if(top){
            currentVMs = topHotel.getValue();
            for (Hotel hotel :
                    hotels) {
                currentVMs.add(new HotelItemViewModel(hotel, email, app));
            }
            topHotel.setValue(currentVMs);
            topHotelLoading.setValue(false);
        }
        else{
            currentVMs = allHotel.getValue();
            for (Hotel hotel :
                    hotels) {
                currentVMs.add(new HotelItemViewModel(hotel, email, app));
            }
            allHotel.setValue(currentVMs);
            allHotelLoading.setValue(false);
        }
    }

    public LiveData<ArrayList<PaketGroomingItemViewModel>> getGroomingPacks() {
        return groomingPacks;
    }

    public LiveData<Boolean> isGroomLoading() {
        return groomingLoading;
    }

    public LiveData<ArrayList<ReviewItemViewModel>> getReviews() {
        return reviews;
    }

    public LiveData<Boolean> isRevLoading() {
        return revLoading;
    }

    public LiveData<ArrayList<PromoItemViewModel>> getShopPromos() {
        return shopPromos;
    }

    public LiveData<Boolean> getShopPromosLoading() {
        return shopPromosLoading;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getTopProducts() {
        return topProducts;
    }

    public LiveData<Boolean> getTopProductsLoading() {
        return topProductsLoading;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getAllProducts() {
        return allProducts;
    }

    public LiveData<Boolean> getAllProductsLoading() {
        return allProductsLoading;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getTopHotel() {
        return topHotel;
    }

    public LiveData<Boolean> getTopHotelLoading() {
        return topHotelLoading;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getAllHotel() {
        return allHotel;
    }

    public LiveData<Boolean> getAllHotelLoading() {
        return allHotelLoading;
    }

    public LiveData<String> getSellerName() {
        return sellerName;
    }

    public LiveData<Integer> getSellerFollower() {
        return sellerFollower;
    }

    public LiveData<String[]> getSellerPosters() {
        return sellerPosters;
    }

    private void addSellerPoster(int index, String poster){
        String[] currentPosters = sellerPosters.getValue();
        currentPosters[index] = poster;
        sellerPosters.setValue(currentPosters);
    }

    public LiveData<String> getSellerPic() {
        return sellerPic;
    }

    public LiveData<String> getSellerDesc() {
        return sellerDesc;
    }

    public LiveData<Float> getSellerScore() {
        return sellerScore;
    }

    public LiveData<String[]> getClinicOpens() {
        return clinicOpens;
    }

    public LiveData<Boolean> isClinicOpenNow() {
        return clinicOpenNow;
    }

    public MutableLiveData<Boolean> isItemsInCartAppo() {
        return itemsInCartAppo;
    }

    public LiveData<String[]> getClinicCloses() {
        return clinicCloses;
    }

    public LiveData<String> getClinicAddress() {
        return clinicAddress;
    }

    public LiveData<String> getClinicCoors() {
        return clinicCoors;
    }

    public LiveData<Integer> getRole() {
        return role;
    }

    public LiveData<String> getFollowId() {
        return followId;
    }

    public LiveData<Boolean> getFollowEnabled() {
        return followEnabled;
    }
}
