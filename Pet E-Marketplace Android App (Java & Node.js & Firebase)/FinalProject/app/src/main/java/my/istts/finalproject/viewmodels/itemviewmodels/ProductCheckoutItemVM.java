package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.models.VarianProduk;
import my.istts.finalproject.inputclasses.PJInput;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ProductCheckoutItemVM {
    private Cart cart;
    private PJInput pjInput;
    private boolean showSeller, showOngkir;

    public ProductCheckoutItemVM(Cart cart, PJInput pjInput, boolean showSeller, boolean showOngkir) {
        this.cart = cart;
        this.pjInput = pjInput;
        this.showSeller = showSeller;
        this.showOngkir = showOngkir;

        ProductDBAccess productDB = new ProductDBAccess();
        productDB.getProductById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Product produk = new Product(documentSnapshot);
                    productDB.getVariants(produk.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            VarianProduk varian = null;
                            List<DocumentSnapshot> variantDocs = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot varDoc:
                                    variantDocs) {
                                if(varDoc.getId().equals(cart.getId_variasi())){
                                    varian = new VarianProduk(varDoc);
                                }
                            }
                            nama.setValue(produk.getNama());
                            berat.setValue(produk.getBerat() * cart.getJumlah());
                            if(varian != null){
                                id_variasi.setValue(cart.getId_variasi());
                                variasi.setValue(varian.getNama());
                                harga.setValue(varian.getHarga());
                                pjInput.addSubtotal(varian.getHarga() * cart.getJumlah());
                                searchPictureURL(varian.getGambar());
                            }
                            else{
                                harga.setValue(produk.getHarga());
                                pjInput.addSubtotal(produk.getHarga() * cart.getJumlah());
                                searchPictureURL(produk.getGambar());
                            }
                        }
                    });
                }
            }
        });
    }

    private void searchPictureURL(String url){
        Storage storage = new Storage();
        storage.getPictureUrlFromName(url).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                picture.setValue(uri.toString());
            }
        });
    }


    private MutableLiveData<String> picture = new MutableLiveData<>("");
    private MutableLiveData<String> nama = new MutableLiveData<>("");
    private MutableLiveData<String> id_variasi = new MutableLiveData<>("");
    private MutableLiveData<String> variasi = new MutableLiveData<>("");
    private MutableLiveData<Integer> harga = new MutableLiveData<>(0);
    private MutableLiveData<Integer> berat = new MutableLiveData<>(0);
    private MutableLiveData<Integer> discountValue = new MutableLiveData<>(0);

    public LiveData<String> getPicture(){
        return picture;
    }

    public LiveData<String> getName(){
        return nama;
    }

    public LiveData<String> getIdVariasi(){
        return id_variasi;
    }

    public LiveData<String> getVariasi(){
        return variasi;
    }

    public LiveData<Integer> getHarga(){
        return harga;
    }

    public LiveData<Integer> getBerat(){
        return berat;
    }

    public LiveData<Integer> getDiscountValue(){
        return discountValue;
    }

    public void setDiscountValue(int discountValue){
        if(discountValue > 0){
            //jika apply diskon
            //ganti dulu discount value untuk mengikuti harga setelah didiskon promo
            //baru setelah itu, beri tahu viewmodel pjinput untuk mengganti subtotalnya
            this.discountValue.setValue(discountValue);
            pjInput.changeProductDiscount(this.harga.getValue() * cart.getJumlah(), discountValue * cart.getJumlah());
        }
        else{
            //jika cancel diskon
            //beri tahu viewmodel pjinput dulu untuk mengurangi jumlah harga hasil diskon (jika sebelumnya harga diganti karena diskon) dan ganti menjadi harga biasa
            //baru setelah itu, jadikan discount value menjadi 0
            if(this.discountValue.getValue() > 0){
                pjInput.cancelProductDiscount(this.harga.getValue() * cart.getJumlah(), this.discountValue.getValue() * cart.getJumlah());
            }
            this.discountValue.setValue(discountValue);
        }
    }

    public String getIdProduct(){
        return cart.getId_item();
    }

    public int getJumlah(){
        return cart.getJumlah();
    }

    public String getSeller(){
        return cart.getEmail_seller();
    }

    public PJInput getPjInput(){
        return pjInput;
    }

    public void setShowSeller(boolean showSeller) {
        this.showSeller = showSeller;
    }

    public void setShowOngkir(boolean showOngkir) {
        this.showOngkir = showOngkir;
    }

    public boolean isSellerShown(){
        return this.showSeller;
    }

    public boolean isOngkirShown(){
        return this.showOngkir;
    }
}
