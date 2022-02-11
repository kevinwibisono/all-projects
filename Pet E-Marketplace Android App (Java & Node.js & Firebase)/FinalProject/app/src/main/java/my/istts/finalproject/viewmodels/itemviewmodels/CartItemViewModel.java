package my.istts.finalproject.viewmodels.itemviewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.ThousandSeparator;
import my.istts.finalproject.models.AkunDBAccess;
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

public class CartItemViewModel {
    private Cart cart;
    private CartDBAccess cartDB;
    private Storage storage;
    private boolean showSeller;
    private int batasMaksimum = 0;

    //diperlukan untuk perhitungan total, karena setiap kali itemQty ganti total akan dikurangi dengan quantity sebelumnya dan diganti qty baru
    private int itemQtyBeforeChange;

    public CartItemViewModel(Cart cart, boolean showSeller, Application app) {
        storage = new Storage();
        cartDB = new CartDBAccess(app);
        this.showSeller = showSeller;
        this.cart = cart;
        itemQty.setValue(cart.getJumlah());
        itemQtyBeforeChange = cart.getJumlah();
        if (cart.getJumlah() > 1) {
            reducable.setValue(true);
        }

        //pada saat pengecekan cart, terdapat beberapa kemungkinan
        //1. normal
        //2. produk/variasi/kamar/paketgrooming dihapus, maka cart item juga akan langsung dihapus
        //3. stok produk/variasi/kamar berubah dan jumlah di cart melebihi itu, maka update cart
        //4. stok produk/variasi/kamar berubah dan menjadi 0, maka cart item menjadi invalid dengan alasan "Produk/Variasi/Kamar Tidak Tersedia"
        getCartItemSeller(cart.getEmail_seller());
        if (cart.getTipe() == 0) {
            //produk
            checkProduct();
        } else if (cart.getTipe() == 1) {
            //grooming
            addable.setValue(true);
            PaketGroomingDBAccess paketGroomingDB = new PaketGroomingDBAccess();
            paketGroomingDB.getPackageById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getData() != null) {
                        PaketGrooming paketGrooming = new PaketGrooming(documentSnapshot);

                        batasMaksimum = 999999999;
                        itemName.setValue(paketGrooming.getNama());
                        itemPrice.setValue(ThousandSeparator.getTS(paketGrooming.getHarga()));
                        price.setValue(paketGrooming.getHarga());
                    } else {
                        //kemungkinan no 2
                        deleted.setValue(true);
                    }
                }
            });
        } else {
            //hotel
            checkHotel();
        }
    }

    private void checkProduct() {
        ProductDBAccess productDB = new ProductDBAccess();
        productDB.getProductById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getData() != null) {
                    Product produk = new Product(documentSnapshot);
                    productDB.getVariants(produk.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            //sekalian utk alasan invalid cart item
                            String variantFound = "Produk";
                            int stok = produk.getStok();
                            int harga = produk.getHarga();
                            String hargaStr = produk.getTSHarga();
                            String nama = produk.getNama();
                            String variasi = "";
                            String gambar = produk.getGambar();
                            List<DocumentSnapshot> variantDocs = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot varDoc :
                                    variantDocs) {
                                if (varDoc.getId().equals(cart.getId_variasi())) {
                                    variantFound = "Variasi";
                                    VarianProduk varian = new VarianProduk(varDoc);
                                    stok = varian.getStok();
                                    harga = varian.getHarga();
                                    hargaStr = varian.getTSHarga();
                                    variasi = varian.getNama();
                                    gambar = varian.getGambar();
                                }
                            }

                            if (!cart.getId_variasi().equals("") && variantFound.equals("Produk")) {
                                //kemungkinan no 2 variasi dihapus
                                deleted.setValue(true);
                            } else {
                                batasMaksimum = stok;
                                if (cart.getJumlah() < stok) {
                                    addable.setValue(true);
                                } else {
                                    //kemungkinan no 3 jumlah cart melebihi stok produk/variasi
                                    if (stok <= 0) {
                                        //kemungkinan no 4 stok produk/variasi habis, maka jumlah cart juga akan menjadi 0
                                        reason.setValue(variantFound + " Tidak Tersedia");
                                    }
                                    cart.setJumlah(stok);
                                    cartDB.setItemChangedListener(new CartDBAccess.onCartItemsChanged() {
                                        @Override
                                        public void onChangedItems() {
                                            itemQtyBeforeChange = cart.getJumlah();
                                            itemQty.setValue(cart.getJumlah());
                                        }
                                    });

                                    cartDB.updateCartItem(cart);
                                }
                                itemVariasi.setValue(variasi);
                                itemName.setValue(nama);
                                itemPrice.setValue(hargaStr);
                                price.setValue(harga);
                                storageGetItemPic(gambar);
                            }
                        }
                    });
                }
                else{
                    deleted.setValue(true);
                }
            }
        });
    }

    private void checkHotel() {
        HotelDBAccess hotelDB = new HotelDBAccess();
        hotelDB.getRoomById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getData() != null) {
                    Hotel h = new Hotel(documentSnapshot);
                    int available = h.getTotal() - h.getSedang_disewa();

                    batasMaksimum = available;
                    if (cart.getJumlah() < available) {
                        addable.setValue(true);
                    } else {
                        //kemungkinan no 3 jumlah cart melebihi available room
                        if (available <= 0) {
                            //kemungkinan no 4 available room hotel kosong, maka jumlah cart juga akan menjadi 0
                            reason.setValue("Hotel Sedang Penuh");
                        }
                        cart.setJumlah(available);
                        cartDB.setItemChangedListener(new CartDBAccess.onCartItemsChanged() {
                            @Override
                            public void onChangedItems() {
                                itemQtyBeforeChange = cart.getJumlah();
                                itemQty.setValue(cart.getJumlah());
                            }
                        });

                        cartDB.updateCartItem(cart);
                    }
                    itemName.setValue(h.getNama());
                    itemPrice.setValue(h.getTSHarga());
                    price.setValue(h.getHarga());
                    String[] facsList = {"Makanan & Minuman", "Ber-AC", "Kamar Privat", "Akses CCTV", "Update Harian", "Taman Bermain",
                            "Training & Olahraga", "Antar Jemput", "Grooming"};
                    String facs = "";
                    int facsCount = 3;
                    if (h.getFasilitasArr().length < 3) {
                        facsCount = h.getFasilitasArr().length;
                    }
                    for (int i = 0; i < facsCount; i++) {
                        int currentFac = Integer.parseInt(h.getFasilitasArr()[i]);
                        if (facs.equals("")) {
                            facs += facsList[currentFac];
                        } else {
                            facs += " ; " + facsList[currentFac];
                        }
                    }
                    itemFacs.setValue(facs);
                    storageGetItemPic(h.getGambar());

                } else {
                    //kemungkinan no 2 hotel tidak ditemukan, sudah dihapus oleh hotel
                    deleted.setValue(true);
                }
            }
        });
    }

    private void storageGetItemPic(String gambar) {
        storage.getPictureUrlFromName(gambar).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                itemPic.setValue(uri.toString());
            }
        });
    }

    private void getCartItemSeller(String email_seller) {
        AkunDBAccess akunDB = new AkunDBAccess();
        akunDB.getAccByEmail(email_seller).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                storage.getPictureUrlFromName(email_seller).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        sellerPic.setValue(uri.toString());
                        sellerName.setValue(documentSnapshot.getString("nama"));
                    }
                });
            }
        });
    }

    public void addTotal() {
        if (addable.getValue()) {
            addable.setValue(false);
            itemQtyBeforeChange = cart.getJumlah();
            cart.addJumlah();
            cartDB.setItemChangedListener(new CartDBAccess.onCartItemsChanged() {
                @Override
                public void onChangedItems() {
                    if (cart.getJumlah() < batasMaksimum) {
                        addable.setValue(true);
                    }
                    itemQty.setValue(cart.getJumlah());
                    reducable.setValue(true);
                }
            });

            cartDB.updateCartItem(cart);
        }
    }

    public void reduceTotal() {
        if (reducable.getValue()) {
            reducable.setValue(false);
            itemQtyBeforeChange = cart.getJumlah();
            cart.redJumlah();
            cartDB.setItemChangedListener(new CartDBAccess.onCartItemsChanged() {
                @Override
                public void onChangedItems() {
                    if (cart.getJumlah() > 1) {
                        reducable.setValue(true);
                    }
                    itemQty.setValue(cart.getJumlah());
                    addable.setValue(true);
                }
            });

            cartDB.updateCartItem(cart);
        }
    }

    public boolean isSellerShown() {
        return showSeller;
    }

    public int getId() {
        return cart.getId();
    }

    public int getItemQtyBeforeChange() {
        return itemQtyBeforeChange;
    }

    public String getIdItem() {
        return cart.getId_item();
    }

    public int getType() {
        return cart.getTipe();
    }

    public String getEmailSeller() {
        return cart.getEmail_seller();
    }

    private MutableLiveData<String> sellerName = new MutableLiveData<>("");
    private MutableLiveData<String> sellerPic = new MutableLiveData<>("");

    public LiveData<String> getSellerName() {
        return sellerName;
    }

    public LiveData<String> getSellerPic() {
        return sellerPic;
    }

    private MutableLiveData<String> itemName = new MutableLiveData<>("");
    private MutableLiveData<String> itemPic = new MutableLiveData<>("");
    private MutableLiveData<String> itemPrice = new MutableLiveData<>("");
    private MutableLiveData<Integer> price = new MutableLiveData<>(0);
    private MutableLiveData<String> itemVariasi = new MutableLiveData<>("");
    private MutableLiveData<Integer> itemQty = new MutableLiveData<>(0);

    private MutableLiveData<Boolean> addable = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> reducable = new MutableLiveData<>(false);
    private MutableLiveData<String> itemFacs = new MutableLiveData<>("");

    private MutableLiveData<Boolean> deleted = new MutableLiveData<>(false);
    private MutableLiveData<String> reason = new MutableLiveData<>("");

    private MutableLiveData<Boolean> checked = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> sellerChecked = new MutableLiveData<>(false);

    public LiveData<String> getItemName() {
        return itemName;
    }

    public LiveData<String> getItemPic() {
        return itemPic;
    }

    public LiveData<String> getItemPrice() {
        return itemPrice;
    }

    public LiveData<Integer> getPrice() {
        return price;
    }

    public LiveData<String> getItemVariasi() {
        return itemVariasi;
    }

    public LiveData<Integer> getItemQty() {
        return itemQty;
    }

    public LiveData<Boolean> isSellerChecked() {
        return sellerChecked;
    }

    public LiveData<Boolean> isChecked() {
        return checked;
    }

    public LiveData<Boolean> isDeleted() {
        return deleted;
    }

    public LiveData<String> getReason() {
        return reason;
    }

    public void setSellerChecked(Boolean checked) {
        this.sellerChecked.setValue(checked);
    }

    public void setChecked(Boolean checked) {
        this.checked.setValue(checked);
    }

    public void switchItemChecked() {
        Boolean stateNow = checked.getValue();
        this.checked.setValue(!stateNow);
    }

    public void switchSellerChecked() {
        Boolean stateNow = sellerChecked.getValue();
        this.sellerChecked.setValue(!stateNow);
    }

    public LiveData<Boolean> isAddable() {
        return addable;
    }

    public LiveData<Boolean> isReducable() {
        return reducable;
    }

    public LiveData<String> getItemFacs() {
        return itemFacs;
    }

    public void setShowSeller(boolean show) {
        showSeller = show;
    }
}
