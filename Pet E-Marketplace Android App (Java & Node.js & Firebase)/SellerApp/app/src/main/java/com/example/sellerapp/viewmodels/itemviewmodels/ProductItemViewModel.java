package com.example.sellerapp.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

public class ProductItemViewModel extends ViewModel {
    private Product produk;
    public MutableLiveData<Boolean> included;

    public ProductItemViewModel(Product produk) {
        included = new MutableLiveData<>(false);
        this.produk = produk;
        ProductDBAccess productDB = new ProductDBAccess();
        Storage storage = new Storage();

        storage.getPictureUrlFromName(produk.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                picture.setValue(uri.toString());
            }
        });

        productDB.getVariants(produk.getId_produk()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                jumlahVarian.setValue(queryDocumentSnapshots.getDocuments().size());
            }
        });
    }

    private MutableLiveData<Integer> jumlahVarian = new MutableLiveData<>();
    private MutableLiveData<String> picture = new MutableLiveData<>();

    public boolean isActive(){
        return produk.isAktif();
    }

    public String getId(){
        return produk.getId_produk();
    }

    public String getName(){
        return produk.getNama();
    }

    public int getSeen(){
        return produk.getDilihat();
    }

    public int getSold(){
        return produk.getTerjual();
    }

    public String getPrice(){
        return produk.getTSHarga();
    }

    public int getStok(){
        return produk.getStok();
    }

    public LiveData<Integer> getVarian(){
        return jumlahVarian;
    }

    public LiveData<String> getPicture(){
        return picture;
    }

}
