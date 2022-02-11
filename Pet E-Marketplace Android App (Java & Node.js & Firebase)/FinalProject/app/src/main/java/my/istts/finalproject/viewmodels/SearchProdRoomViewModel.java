package my.istts.finalproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchProdRoomViewModel {
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;

    public SearchProdRoomViewModel(){
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
    }

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<String> query = new MutableLiveData<>("");
    private MutableLiveData<ArrayList<ProductItemViewModel>> productsVM = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> hotelsVM = new MutableLiveData<>();

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getProducts(){
        return productsVM;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getHotels(){
        return hotelsVM;
    }

    public void search(int type, String hp_seller){
        if(type == 0){
            loading.setValue(true);
            productsVM.setValue(new ArrayList<>());

            productDB.getAllSellerProducts(hp_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ArrayList<Product> products = new ArrayList<>();
                    for (DocumentSnapshot prodDoc:
                         queryDocumentSnapshots.getDocuments()) {
                        Product product = new Product(prodDoc);
                        if(!query.getValue().equals("") && product.getNama().toLowerCase().contains(query.getValue().toLowerCase())){
                            products.add(product);
                        }
                    }
                    addProductsToVM(products);
                }
            });
        }
        else{
            loading.setValue(true);
            hotelsVM.setValue(new ArrayList<>());

            hotelDB.getAllHotelsOwned(hp_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ArrayList<Hotel> hotels = new ArrayList<>();
                    for (DocumentSnapshot hotelDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        Hotel hotel = new Hotel(hotelDoc);
                        if(!query.getValue().equals("") && hotel.getNama().toLowerCase().contains(query.getValue().toLowerCase())){
                            hotels.add(hotel);
                        }
                    }
                    addHotelsToVM(hotels);
                }
            });
        }
    }

    private void addProductsToVM(ArrayList<Product> products){
        ArrayList<ProductItemViewModel> current = productsVM.getValue();
        for (Product prod :
                products) {
            current.add(new ProductItemViewModel(prod));
        }
        productsVM.setValue(current);
        loading.setValue(false);
    }

    private void addHotelsToVM(ArrayList<Hotel> hotels){
        ArrayList<HotelItemViewModel> current = hotelsVM.getValue();
        for (Hotel hotel :
                hotels) {
            current.add(new HotelItemViewModel(hotel));
        }
        hotelsVM.setValue(current);
        loading.setValue(false);
    }
}
