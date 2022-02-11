package my.istts.finalproject.viewmodels.itemviewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.PaketGrooming;
import my.istts.finalproject.models.PaketGroomingDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.models.VarianProduk;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CartInvItemViewModel {
    private Cart cart;
    private Storage storage;
    private String reason;

    public CartInvItemViewModel(CartItemViewModel cartVM, String reason, Application app) {
        storage = new Storage();
        CartDBAccess cartDB = new CartDBAccess(app);
        this.reason = reason;
        cartDB.setItemsGotListener(new CartDBAccess.onCartItemsGot() {
            @Override
            public void onGotItems(List<Cart> items) {
                cart = items.get(0);
                type.setValue(cart.getTipe());
                id.setValue(cart.getId());
                id_item.setValue(cart.getId_item());

                itemQty.setValue(cart.getJumlah());
                if(cart.getTipe() == 0){
                    //produk
                    ProductDBAccess productDB = new ProductDBAccess();
                    productDB.getProductById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                Product produk = new Product(documentSnapshot);
                                productDB.getVariants(produk.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        //sekalian utk alasan invalid cart item
                                        int harga = produk.getHarga();
                                        String nama = produk.getNama();
                                        String variasi = "";
                                        String gambar = produk.getGambar();
                                        List<DocumentSnapshot> variantDocs = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot varDoc:
                                                variantDocs) {
                                            if(varDoc.getId().equals(cart.getId_variasi())){
                                                VarianProduk varian = new VarianProduk(varDoc);
                                                harga = varian.getHarga();
                                                variasi = varian.getNama();
                                                gambar = varian.getGambar();
                                            }
                                        }

                                        itemVariasi.setValue(variasi);
                                        itemName.setValue(nama);
                                        price.setValue(harga);
                                        storageGetItemPic(gambar);
                                    }
                                });
                            }
                        }
                    });
                }
                else if(cart.getTipe() == 1){
                    //grooming
                    PaketGroomingDBAccess paketGroomingDB = new PaketGroomingDBAccess();
                    paketGroomingDB.getPackageById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                PaketGrooming paketGrooming = new PaketGrooming(documentSnapshot);

                                itemName.setValue(paketGrooming.getNama());
                                price.setValue(paketGrooming.getHarga());
                            }
                        }
                    });
                }
                else{
                    //hotel
                    HotelDBAccess hotelDB = new HotelDBAccess();
                    hotelDB.getRoomById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                Hotel h = new Hotel(documentSnapshot);
                                itemName.setValue(h.getNama());
                                price.setValue(h.getHarga());
                                String[] facsList = {"Makanan & Minuman", "Ber-AC", "Kamar Privat", "Akses CCTV", "Update Harian", "Taman Bermain",
                                        "Training & Olahraga", "Antar Jemput", "Grooming"};
                                String facs = ""; int facsCount = 3;
                                if(h.getFasilitasArr().length < 3){
                                    facsCount = h.getFasilitasArr().length;
                                }
                                for (int i = 0; i < facsCount; i++) {
                                    int currentFac = Integer.parseInt(h.getFasilitasArr()[i]);
                                    if(facs.equals("")){
                                        facs += facsList[currentFac];
                                    }
                                    else{
                                        facs += " ; "+facsList[currentFac];
                                    }
                                }
                                itemFacs.setValue(facs);
                                storageGetItemPic(h.getGambar());

                            }
                        }
                    });
                }
            }
        });

        cartDB.getCartWithId(cartVM.getId());
    }

    private void storageGetItemPic(String gambar){
        storage.getPictureUrlFromName(gambar).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                itemPic.setValue(uri.toString());
            }
        });
    }

    private MutableLiveData<Integer> id = new MutableLiveData<>();
    private MutableLiveData<String> id_item = new MutableLiveData<>("");
    private MutableLiveData<Integer> type = new MutableLiveData<>();

    private MutableLiveData<String> itemName = new MutableLiveData<>("");
    private MutableLiveData<String> itemPic = new MutableLiveData<>("");
    private MutableLiveData<Integer> price = new MutableLiveData<>(0);
    private MutableLiveData<String> itemVariasi = new MutableLiveData<>("");
    private MutableLiveData<Integer> itemQty = new MutableLiveData<>(0);

    private MutableLiveData<String> itemFacs = new MutableLiveData<>("");

    public LiveData<String> getItemName(){
        return itemName;
    }

    public LiveData<String> getItemPic(){
        return itemPic;
    }

    public LiveData<Integer> getPrice(){
        return price;
    }

    public LiveData<String> getItemVariasi(){
        return itemVariasi;
    }

    public LiveData<Integer> getItemQty(){
        return itemQty;
    }

    public LiveData<String> getItemFacs(){
        return itemFacs;
    }

    public String getReason() {
        return reason;
    }

    public LiveData<Integer> getId() {
        return id;
    }

    public LiveData<String> getId_item() {
        return id_item;
    }

    public LiveData<Integer> getType() {
        return type;
    }
}
