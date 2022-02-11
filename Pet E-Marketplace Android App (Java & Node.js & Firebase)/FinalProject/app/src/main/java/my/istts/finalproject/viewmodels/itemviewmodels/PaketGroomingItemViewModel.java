package my.istts.finalproject.viewmodels.itemviewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.PaketGrooming;

import java.util.List;

public class PaketGroomingItemViewModel {
    private PaketGrooming paketGrooming;
    private CartDBAccess cartDB;

    public PaketGroomingItemViewModel(PaketGrooming paketGrooming, Application app) {
        this.paketGrooming = paketGrooming;

        cartDB = new CartDBAccess(app);
        searchPackageInCart();
    }

    private MutableLiveData<Integer> jumPaketInCart = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> paketAddEnabled = new MutableLiveData<>(false);

    public LiveData<Integer> getPaketInCart(){
        return jumPaketInCart;
    }

    public LiveData<Boolean> isPaketAddEnabled(){
        return paketAddEnabled;
    }
    public String getPackageName(){
        return paketGrooming.getNama();
    }

    public String getPackId(){
        return paketGrooming.getId_paket();
    }

    public Integer getPackagePrice(){
        return paketGrooming.getHarga();
    }

    private void searchPackageInCart(){
        cartDB.setFoundListener(new CartDBAccess.onCartFound() {
            @Override
            public void onFound(List<Cart> cartItems) {
                if(cartItems.size() > 0){
                    jumPaketInCart.setValue(cartItems.get(0).getJumlah());
                }
                else{
                    jumPaketInCart.setValue(0);
                }
                paketAddEnabled.setValue(true);
            }
        });

        cartDB.searchCartItem(paketGrooming.getId_paket());
    }

    public void addCart(){
        paketAddEnabled.setValue(false);

        //callback dari insert atau update
        cartDB.setItemChangedListener(new CartDBAccess.onCartItemsChanged() {
            @Override
            public void onChangedItems() {
                paketAddEnabled.setValue(true);
                searchPackageInCart();
            }
        });

        cartDB.setFoundListener(new CartDBAccess.onCartFound() {
            @Override
            public void onFound(List<Cart> cartItems) {
                Cart paketCartItem = null;
                for (Cart cartItem:
                        cartItems) {
                    paketCartItem = cartItem;
                }
                if (paketCartItem != null) {
                    paketCartItem.addJumlah();
                    cartDB.updateCartItem(paketCartItem);
                }
                else{
                    Cart inserttedCart = new Cart(paketGrooming.getemail_groomer(), paketGrooming.getId_paket(), "", 1, 1);
                    cartDB.insertCartItem(inserttedCart);
                }
            }
        });

        cartDB.searchCartItem(paketGrooming.getId_paket());
    }

    public void redCart(boolean makeZero){
        //jika makeZero berarti tombol delete ditekan
        paketAddEnabled.setValue(false);

        //callback dari insert atau update
        cartDB.setItemChangedListener(new CartDBAccess.onCartItemsChanged() {
            @Override
            public void onChangedItems() {
                searchPackageInCart();
            }
        });

        cartDB.setDeletedListener(new CartDBAccess.onCartDeleted() {
            @Override
            public void onDeleted() {
                searchPackageInCart();
            }
        });

        cartDB.setFoundListener(new CartDBAccess.onCartFound() {
            @Override
            public void onFound(List<Cart> cartItems) {
                Cart paketCartItem = null;
                for (Cart cartItem:
                        cartItems) {
                    paketCartItem = cartItem;
                }
                if (paketCartItem != null) {
                    paketCartItem.redJumlah();
                    if(paketCartItem.getJumlah() <= 0 || makeZero){
                        cartDB.deleteCartItem(paketCartItem.getId());
                    }
                    else{
                        cartDB.updateCartItem(paketCartItem);
                    }
                }
            }
        });

        cartDB.searchCartItem(paketGrooming.getId_paket());
    }
}
