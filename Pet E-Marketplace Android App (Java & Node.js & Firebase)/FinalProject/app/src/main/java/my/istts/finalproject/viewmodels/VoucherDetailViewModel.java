package my.istts.finalproject.viewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Promo;
import my.istts.finalproject.models.PromoDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VoucherDetailViewModel {
    private PromoDBAccess promoDB;
    private ProductDBAccess productDB;

    public VoucherDetailViewModel(){
        promoDB = new PromoDBAccess();
        productDB = new ProductDBAccess();
    }

    private MutableLiveData<ArrayList<ProductItemViewModel>> promoProducts = new MutableLiveData<>();
    private MutableLiveData<Boolean> productsLoading = new MutableLiveData<>();
    private MutableLiveData<String> promoPic = new MutableLiveData<>("");
    private MutableLiveData<String> promoName = new MutableLiveData<>("");
    private MutableLiveData<String> promoDate = new MutableLiveData<>("");
    private MutableLiveData<Integer> promoMax = new MutableLiveData<>();
    private MutableLiveData<Integer> promoMinReq = new MutableLiveData<>();
    private MutableLiveData<Integer> promoPercent = new MutableLiveData<>();

    public void setPromo(String id_promo){
        productsLoading.setValue(true);
        promoDB.getPromoDetail(id_promo).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Promo promo = new Promo(documentSnapshot);

                    Storage storage = new Storage();
                    storage.getPictureUrlFromName(promo.getemail_penjual()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            promoPic.setValue(uri.toString());
                        }
                    });

                    searchPromoProducts(promo.getId_produk(), promo.getemail_penjual());
                    promoName.setValue(promo.getJudul());
                    promoDate.setValue(promo.getValidString());
                    promoMax.setValue(promo.getMaximum_diskon());
                    promoMinReq.setValue(promo.getMinimum_pembelian());
                    promoPercent.setValue(promo.getPersentase());

                }
            }
        });
    }

    private void searchPromoProducts(String id_product, String email){
        productsLoading.setValue(true);
        promoProducts.setValue(new ArrayList<>());

        if(id_product.equals("")){
            productDB.getAllSellerProducts(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        addProductToVM(new Product(queryDocumentSnapshots.getDocuments().get(i)));
                        if(i == queryDocumentSnapshots.getDocuments().size()-1){
                            productsLoading.setValue(false);
                        }
                    }
                }
            });
        }
        else{
            String[] prodIDs = id_product.split("\\|");
            for (int i = 0; i < prodIDs.length; i++) {
                ArrayList<Integer> ctr = new ArrayList<>();
                productDB.getProductById(prodIDs[i]).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getData() != null){
                            addProductToVM(new Product(documentSnapshot));
                        }
                        ctr.add(0);
                        if(ctr.size() == prodIDs.length){
                            productsLoading.setValue(false);
                        }
                    }
                });
            }
        }
    }

    private void addProductToVM(Product product){
        ArrayList<ProductItemViewModel> currentVMs = promoProducts.getValue();
        currentVMs.add(new ProductItemViewModel(product));
        promoProducts.setValue(currentVMs);
    }

    public LiveData<ArrayList<ProductItemViewModel>> getPromoProducts() {
        return promoProducts;
    }

    public LiveData<Boolean> getProductsLoading() {
        return productsLoading;
    }

    public LiveData<String> getPromoPic() {
        return promoPic;
    }

    public LiveData<String> getPromoName() {
        return promoName;
    }

    public LiveData<String> getPromoDate() {
        return promoDate;
    }

    public LiveData<Integer> getPromoMax() {
        return promoMax;
    }

    public LiveData<Integer> getPromoMinReq() {
        return promoMinReq;
    }

    public LiveData<Integer> getPromoPercent() {
        return promoPercent;
    }
}
