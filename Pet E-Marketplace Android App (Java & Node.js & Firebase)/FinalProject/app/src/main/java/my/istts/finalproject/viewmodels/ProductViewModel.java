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
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Promo;
import my.istts.finalproject.models.PromoDBAccess;
import my.istts.finalproject.models.Review;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.models.VarianProduk;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ReviewItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel {
    private AkunDBAccess akunDB;
    private ProductDBAccess productDB;
    private PromoDBAccess promoDB;
    private FavoritDBAccess favDB;
    private ReviewDBAccess reviewDB;
    private CartDBAccess cartDB;
    private Storage storage;
    private Application app;

    public ProductViewModel(Application app) {
        this.app = app;
        akunDB = new AkunDBAccess(app);
        favDB = new FavoritDBAccess(app);
        productDB = new ProductDBAccess();
        promoDB = new PromoDBAccess();
        cartDB = new CartDBAccess(app);
        reviewDB = new ReviewDBAccess();
        storage = new Storage();
    }

    private MutableLiveData<Boolean> itemsInCartAppo = new MutableLiveData<>();

    private MutableLiveData<String> nama = new MutableLiveData<>("");
    private MutableLiveData<String> harga = new MutableLiveData<>("");
    private MutableLiveData<String> deskripsi = new MutableLiveData<>("");
    private MutableLiveData<Integer> promo = new MutableLiveData<>();
    private MutableLiveData<Integer> stok = new MutableLiveData<>();
    private MutableLiveData<Integer> berat = new MutableLiveData<>();
    private MutableLiveData<Integer> sold = new MutableLiveData<>();
    private MutableLiveData<Boolean> aktif = new MutableLiveData<>();
    private MutableLiveData<String> nilai = new MutableLiveData<>("");
    private MutableLiveData<String> kategori = new MutableLiveData<>("");
    private MutableLiveData<String[]> pictures = new MutableLiveData<>(new String[5]);
    private MutableLiveData<ArrayList<VarianProduk>> variants = new MutableLiveData<>();

    private MutableLiveData<String> ownerName = new MutableLiveData<>("");
    private MutableLiveData<String> ownerPic = new MutableLiveData<>("");

    private Favorit favorit;
    private Product produk;
    private VarianProduk chosenVariant;
    private String emailUser;
    private int[] scoreList = {0, 5, 4, 3, 2, 1};
    private MutableLiveData<String> emailShop = new MutableLiveData<>("");
    private MutableLiveData<String> toastMsg = new MutableLiveData<>("");
    private MutableLiveData<Boolean> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> cartLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> favoritEnabled = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> favorited = new MutableLiveData<>();

    private MutableLiveData<ArrayList<ReviewItemViewModel>> reviews = new MutableLiveData<>();
    private MutableLiveData<Boolean> revLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProductItemViewModel>> shopProducts = new MutableLiveData<>();
    private MutableLiveData<Boolean> prodLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProductItemViewModel>> reccProducts = new MutableLiveData<>();
    private MutableLiveData<Boolean> reccLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> deleted = new MutableLiveData<>();

    public void setProduk(String id_produk){
        String[] initPics = new String[5];
        for (int i = 0; i < 5; i++) {
            initPics[i] = "";
        }
        pictures.setValue(initPics);

        checkCart();

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();

                    productDB.seeProduct(id_produk);

                    productDB.getProductById(id_produk).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            if(doc.getData() != null){
                                Product p = new Product(doc);
                                produk = p;
                                emailShop.setValue(p.getemail_penjual());
                                nama.setValue(p.getNama());
                                harga.setValue(p.getTSHarga());
                                deskripsi.setValue(p.getDeskripsi());
                                berat.setValue(p.getBerat());
                                stok.setValue(p.getStok());
                                sold.setValue(p.getTerjual());
                                aktif.setValue(p.isAktif());
                                kategori.setValue(p.getKategoriString());

                                String[] hotelPics = p.getDaftar_gambar().split("\\|", -1);
                                for (int i = 0; i < hotelPics.length; i++) {
                                    final int index = i;
                                    //diberi +1 karena picture[0] adalah tempat gambar varian
                                    storage.getPictureUrlFromName(hotelPics[i]).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            assignPics(uri.toString(), index+1);
                                        }
                                    });
                                }
                                
                                akunDB.getAccByEmail(p.getemail_penjual()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.getData() != null){
                                            storage.getPictureUrlFromName(p.getemail_penjual()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    ownerPic.setValue(uri.toString());
                                                    ownerName.setValue(documentSnapshot.getString("nama"));
                                                }
                                            });
                                        }
                                    }
                                });

                                getShopPromos(p.getId_produk(), p.getemail_penjual());
                                getVariants(p.getId_produk());
                                getFavorite(emailUser, p.getId_produk());
                                getProductReviews(p.getId_produk(), 0);
                                getShopOtherProducts(emailUser, p.getemail_penjual());
                                getProductsReccomendation(emailUser, p.getemail_penjual());
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
                    itemsInCartAppo.setValue(true);
                }
            }
        });

        cartDB.getAllCartItemsByType(0);
    }

    private void assignPics(String pic, int index){
        String[] currentPics = pictures.getValue();
        currentPics[index] = pic;
        pictures.setValue(currentPics);
    }

    private void getShopPromos(String id_produk, String email_penjual){
        promoDB.getShopPromos(email_penjual).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    int jumPromo = 0;
                    for (DocumentSnapshot promoDoc:
                         queryDocumentSnapshots.getDocuments()) {
                        Promo promo = new Promo(promoDoc);
                        if(promo.getId_produk().equals("") || promo.getId_produk().contains(id_produk)){
                            jumPromo++;
                        }
                    }
                    promo.setValue(jumPromo);
                }
                else{
                    promo.setValue(0);
                }
            }
        });
    }

    private void getVariants(String id_produk){
        variants.setValue(new ArrayList<>());
        productDB.getVariants(id_produk).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<VarianProduk> varsArr = new ArrayList<>();
                    for (DocumentSnapshot varDoc:
                         queryDocumentSnapshots.getDocuments()) {
                        varsArr.add(new VarianProduk(varDoc));
                    }
                    addVariantsToLiveData(varsArr);
                }
            }
        });
    }

    private void getFavorite(String email, String id_produk){
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

        favDB.getFavorit(email, id_produk);
    }

    public void getProductReviews(String id_produk, int skor){
        final int score = scoreList[skor];
        revLoading.setValue(true);
        reviews.setValue(new ArrayList<>());
        if(score == 0){
            reviewDB.getAllReviewsOfItem(id_produk).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

    private void getShopOtherProducts(String email, String email_penjual){
        prodLoading.setValue(false);
        shopProducts.setValue(new ArrayList<>());

        productDB.getTenProductsOwned(email_penjual).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Product> prodArr = new ArrayList<>();
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        if(!doc.getId().equals(produk.getId_produk())){
                            prodArr.add(new Product(doc));
                        }
                    }
                    addProductVMs(prodArr, email);
                }
                else{
                    prodLoading.setValue(false);
                }
            }
        });
    }

    private void getProductsReccomendation(String email, String email_penjual){
        reccLoading.setValue(false);
        reccProducts.setValue(new ArrayList<>());

        productDB.getTenProductsNotOwned(email_penjual).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Product> reccArr = new ArrayList<>();
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        reccArr.add(new Product(doc));
                    }
                    addReccProductVMs(reccArr, email);
                }
                else{
                    reccLoading.setValue(false);
                }
            }
        });
    }

    private void addVariantsToLiveData(ArrayList<VarianProduk> vars){
        ArrayList<VarianProduk> currentVMs = variants.getValue();
        for (VarianProduk var:
                vars) {
            currentVMs.add(var);
        }
        variants.setValue(currentVMs);
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

    private void addProductVMs(ArrayList<Product> products, String email){
        ArrayList<ProductItemViewModel> currentVMs = shopProducts.getValue();
        for (Product product:
                products) {
            currentVMs.add(new ProductItemViewModel(product, email, app));
        }
        shopProducts.setValue(currentVMs);
        prodLoading.setValue(false);
    }

    private void addReccProductVMs(ArrayList<Product> products, String email){
        ArrayList<ProductItemViewModel> currentVMs = reccProducts.getValue();
        for (Product product:
                products) {
            currentVMs.add(new ProductItemViewModel(product, email, app));
        }
        reccProducts.setValue(currentVMs);
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
                        favorit = new Favorit(emailUser, produk.getId_produk(), 0);
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

                                favDB.getFavorit(emailUser, produk.getId_produk());
                            }
                        });

                        favDB.addFavorit(favorit);
                    }
                }
            });

            favDB.getFavorit(emailUser, produk.getId_produk());
        }
    }

    public void chooseVariant(int index){
        VarianProduk chosen = variants.getValue().get(index);
        chosenVariant = chosen;
        storage.getPictureUrlFromName(chosen.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String[] pictureNow = pictures.getValue();
                pictureNow[0] = uri.toString();
                pictures.setValue(pictureNow);
            }
        });

        if(produk != null){
            nama.setValue(produk.getNama()+" - "+chosen.getNama());
        }
        harga.setValue(chosen.getTSHarga());
    }

    public void beginAddCart(){
        cartLoading.setValue(true);
        cartDB.setItemChangedListener(new CartDBAccess.onCartItemsChanged() {
            @Override
            public void onChangedItems() {
                //listener untuk jika sdh berhasil add/update
                toastMsg.setValue("Produk Berhasil Ditambahkan");
                error.setValue(false);
                cartLoading.setValue(false);
            }
        });

        if(variants.getValue().size() > 0){
            //produk punya variasi
            if(chosenVariant == null){
                toastMsg.setValue("Mohon Pilih Salah 1 Variasi");
                error.setValue(true);
                cartLoading.setValue(false);
            }
            else{
                cartDB.setFoundListener(new CartDBAccess.onCartFound() {
                    @Override
                    public void onFound(List<Cart> cartItems) {
                        addCart(cartItems, chosenVariant.getStok());
                    }
                });
                cartDB.searchCartItemVariant(produk.getId_produk(), chosenVariant.getId_varian());
            }
        }
        else{
            cartDB.setFoundListener(new CartDBAccess.onCartFound() {
                @Override
                public void onFound(List<Cart> cartItems) {
                    addCart(cartItems, produk.getStok());
                }
            });
            cartDB.searchCartItem(produk.getId_produk());
        }
    }

    private void addCart(List<Cart> cartItems, int batasStok){
        //pertama cari dulu apakah item sudah ada di cart
        itemsInCartAppo.setValue(true);
        if(cartItems.size() > 0){
            //sudah ada di cart, tinggal update
            Cart cart = cartItems.get(0);
            if(cart.getJumlah() >= batasStok){
                //sudah sebanyak batas stok produk/variasi, tidak bisa ditambah lagi
                toastMsg.setValue("Jumlah Di Keranjang Tidak Dapat Melebihi Stok Produk/Variasi");
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
            String varianId = "";
            if(variants.getValue().size() > 0){
                varianId = chosenVariant.getId_varian();
            }
            Cart cart = new Cart(emailShop.getValue(), produk.getId_produk(), varianId,1, 0);
            cartDB.insertCartItem(cart);
        }
    }



    //GETTERS
    public LiveData<String> getEmailShop(){ return emailShop;}

    public LiveData<String> getNama(){ return nama; }

    public LiveData<String> getHarga(){ return harga; }

    public LiveData<String> getDesc(){ return deskripsi; }

    public LiveData<String> getKategori(){ return kategori; }

    public LiveData<Integer> getPromo(){ return promo; }

    public LiveData<Integer> getStok(){ return stok; }

    public LiveData<Integer> getWeight(){ return berat; }

    public LiveData<Integer> getSold(){ return sold; }

    public LiveData<Boolean> getAktif(){ return aktif; }

    public LiveData<Boolean> isDeleted(){ return deleted; }

    public LiveData<String> getScore(){ return nilai; }

    public LiveData<String[]> getPictures(){ return pictures; }

    public LiveData<ArrayList<VarianProduk>> getVariants(){ return variants; }

    public LiveData<String> getOwnerName(){ return ownerName; }

    public LiveData<String> getOwnerPic(){ return ownerPic; }

    public LiveData<Boolean> getError(){ return error; }

    public LiveData<Boolean> isItemsInCart(){ return itemsInCartAppo; }

    public LiveData<String> getToastMsg(){ return toastMsg; }

    public LiveData<Boolean> isCartLoading(){ return cartLoading; }

    public LiveData<Boolean> isFavoritEnabled(){ return favoritEnabled; }

    public LiveData<Boolean> isFavorited(){ return favorited; }

    public LiveData<ArrayList<ReviewItemViewModel>> getReviews(){
        return reviews;
    }

    public LiveData<Boolean> isRevLoading(){ return revLoading; }

    public LiveData<ArrayList<ProductItemViewModel>> getShopProducts(){
        return shopProducts;
    }

    public LiveData<Boolean> isProductLoading(){ return prodLoading; }

    public LiveData<ArrayList<ProductItemViewModel>> getReccProducts(){
        return reccProducts;
    }

    public LiveData<Boolean> isReccLoading(){ return reccLoading; }
}
