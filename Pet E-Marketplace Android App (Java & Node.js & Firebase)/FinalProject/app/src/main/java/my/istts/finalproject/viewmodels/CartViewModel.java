package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.CartItemViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartViewModel {
    private CartDBAccess cartDB;
    private Application app;

    public CartViewModel(Application app) {
        this.app = app;
        cartDB = new CartDBAccess(app);
    }

    public void getCartItems(int tipe){
        if(tipe == 0){
            loadingTitle.setValue("Mendapatkan Produk...");
        }
        else if(tipe == 1){
            loadingTitle.setValue("Mendapatkan Paket Grooming...");
        }
        else if(tipe == 2){
            loadingTitle.setValue("Mendapatkan Kamar...");
        }
        loading.setValue(true);
        total.setValue(0);
        allChecked.setValue(false);
        cartVMs.setValue(new ArrayList<>());
        cartDB.setItemsGotListener(new CartDBAccess.onCartItemsGot() {
            @Override
            public void onGotItems(List<Cart> items) {
                addCartVMs(items);
            }
        });

        cartDB.getAllCartItemsByType(tipe);
    }

    private void addCartVMs(List<Cart> carts){
        ArrayList<CartItemViewModel> currentVMs = cartVMs.getValue();
        for (Cart c:
             carts) {
            currentVMs.add(new CartItemViewModel(c, false, app));
        }
        cartVMs.setValue(currentVMs);
        determineCartsShowSeller();
        loading.setValue(false);
    }

    private void determineCartsShowSeller(){
        ArrayList<CartItemViewModel> currentVMs = cartVMs.getValue();
        String lastSeller = "";
        for (CartItemViewModel cartVM:
             currentVMs) {
            if(!cartVM.getEmailSeller().equals(lastSeller)){
                cartVM.setShowSeller(true);
            }
            lastSeller = cartVM.getEmailSeller();
        }
        cartVMs.setValue(currentVMs);
    }

    public void deleteCart(int position){
        ArrayList<CartItemViewModel> currentVMs = cartVMs.getValue();
        CartItemViewModel deletedCart = currentVMs.get(position);

        if(deletedCart.isChecked().getValue()){
            totalCheckedItems--;
            int totalNow = total.getValue();
            totalNow -= (deletedCart.getItemQty().getValue() * deletedCart.getPrice().getValue());
            if(totalNow <= 0){
                totalNow = 0;
            }
            total.setValue(totalNow);
        }

        cartDB.setDeletedListener(new CartDBAccess.onCartDeleted() {
            @Override
            public void onDeleted() {
                currentVMs.remove(position);
                cartVMs.setValue(currentVMs);
                determineCartsShowSeller();
                determineSellerCheck(deletedCart.getEmailSeller());
                determineAllChecked();
            }
        });

        cartDB.deleteCartItem(deletedCart.getId());
    }

    public void deleteAllCheckedCarts(){
        loadingTitle.setValue("Proses Menghapus...");
        loading.setValue(true);
        ArrayList<CartItemViewModel> selectedCart = new ArrayList<>();

        for (CartItemViewModel cart:
             cartVMs.getValue()) {
            if(cart.isChecked().getValue()){
                selectedCart.add(cart);
            }
        }

        if(selectedCart.size() > 0){
            for (int i = 0; i < selectedCart.size(); i++) {
                final int index = i;
                CartItemViewModel cart = selectedCart.get(i);

                cartDB.setDeletedListener(new CartDBAccess.onCartDeleted() {
                    @Override
                    public void onDeleted() {
                        totalCheckedItems--;
                        if(index == selectedCart.size()-1){
                            deleteCartsLocalVariable(selectedCart);
                        }
                    }
                });

                cartDB.deleteCartItem(cart.getId());
            }
        }
        else{
            loading.setValue(false);
        }
    }

    private void deleteCartsLocalVariable(ArrayList<CartItemViewModel> deletedCarts){
        ArrayList<CartItemViewModel> currentVMs = cartVMs.getValue();
        for (CartItemViewModel deleted:
             deletedCarts) {
            currentVMs.remove(deleted);
        }
        cartVMs.setValue(currentVMs);
        determineCartsShowSeller();
        for (CartItemViewModel cart:
             cartVMs.getValue()) {
            determineSellerCheck(cart.getEmailSeller());
        }
        determineAllChecked();
        total.setValue(0);
        loading.setValue(false);
    }

    public void addReduceCart(CartItemViewModel cartVM){
        if(cartVM.isChecked().getValue()){
            //jika sedang dicentang, baru diperhitungkan ke total;
            int totalNow = total.getValue();
            totalNow -= (cartVM.getItemQtyBeforeChange() * cartVM.getPrice().getValue());
            if(totalNow < 0){
                totalNow = 0;
            }
            totalNow += (cartVM.getItemQty().getValue() * cartVM.getPrice().getValue());
            total.setValue(totalNow);
        }
    }

    public void clickCBSeller(CartItemViewModel cartVM){
        //jika seller dicheck/diuncheck, maka seluruh item jualannya dicheck semua
        //saat seller dicentang, maka item yang sedang dalam kondisi dicentang (dipehitungkan dalam total) tidak perlu dihitung juga
        //     harga ini agar menghindari penambahan total 2x untuk item yang sudah diperhitungkan sebelumnya
        int totalNow = total.getValue();
        Boolean checked = cartVM.isSellerChecked().getValue();
        for (CartItemViewModel cart:
             cartVMs.getValue()) {
            if(cart.getEmailSeller().equals(cartVM.getEmailSeller()) && Objects.equals(cart.isChecked().getValue(), !checked)){
                if(checked){
                    totalCheckedItems++;
                    totalNow += (cart.getPrice().getValue() * cart.getItemQty().getValue());
                }
                else{
                    totalCheckedItems--;
                    totalNow -= (cart.getPrice().getValue() * cart.getItemQty().getValue());
                    if(totalNow < 0){
                        totalNow = 0;
                    }
                }
                cart.setChecked(cartVM.isSellerChecked().getValue());
            }
        }
        determineAllChecked();
        total.setValue(totalNow);
    }

    public void clickCBAll(){
        int totalNow = total.getValue();
        Boolean checked = allChecked.getValue();
        for (CartItemViewModel cart:
                cartVMs.getValue()) {
            if(Objects.equals(cart.isChecked().getValue(), !checked)){
                if(checked){
                    totalCheckedItems++;
                    totalNow += (cart.getPrice().getValue() * cart.getItemQty().getValue());
                }
                else{
                    totalCheckedItems--;
                    totalNow -= (cart.getPrice().getValue() * cart.getItemQty().getValue());
                    if(totalNow < 0){
                        totalNow = 0;
                    }
                }
            }
            cart.setChecked(checked);
            cart.setSellerChecked(checked);
        }
        total.setValue(totalNow);
    }

    public void checkCartItem(CartItemViewModel cartItemViewModel){
        Boolean checked = cartItemViewModel.isChecked().getValue();
        Integer totalNow = total.getValue();
        int subtotal = cartItemViewModel.getPrice().getValue() * cartItemViewModel.getItemQty().getValue();
        if(checked) {
            totalCheckedItems++;
            //jika sebelumnya belum dicheck kemudian menjadi dicheck barulah tambah totalnya
            //dicheck, tambah subtotal ke total harga pesanan
            totalNow += subtotal;
        }
        else{
            //diuncheck, kurangi total dan uncheck seller
            totalCheckedItems--;
            totalNow -= subtotal;
            if(totalNow < 0){
                totalNow = 0;
            }
        }
        determineSellerCheck(cartItemViewModel.getEmailSeller());
        determineAllChecked();
        total.setValue(totalNow);
    }

    private void determineSellerCheck(String hp_seller){
        boolean allSellerChecked = true;
        for (CartItemViewModel cartVM:
                cartVMs.getValue()) {
            //jika ada satu saja cart item (dengan seller yang sama) yang uncheck, maka tidak boleh dicheck sellernya
            if(cartVM.getEmailSeller().equals(hp_seller) && !cartVM.isChecked().getValue()){
                allSellerChecked = false;
            }

        }
        changeSellerCheck(hp_seller, allSellerChecked);
    }

    private void determineAllChecked(){
        boolean allCartChecked = true;
        for (CartItemViewModel cartVM:
                cartVMs.getValue()) {
            //jika ada satu saja cart item yang uncheck, maka tidak boleh dicheck all checkbox
            if(!cartVM.isChecked().getValue()){
                allCartChecked = false;
            }
        }
        allChecked.setValue(allCartChecked);
    }

    private void changeSellerCheck(String hp_seller, boolean checked){
        for (CartItemViewModel cartVM:
                cartVMs.getValue()) {
            if(cartVM.getEmailSeller().equals(hp_seller)){
                cartVM.setSellerChecked(checked);
            }
        }
    }

    public String getSelectedIds(){
        String selectedIds = "";

        for (CartItemViewModel cart:
                cartVMs.getValue()) {
            if(cart.isChecked().getValue() && cart.getReason().getValue().equals("") && !cart.isDeleted().getValue()){
                selectedIds += cart.getId()+"|";
            }
        }

        return selectedIds;
    }

    public String getSelectedSeller(){
        String selectedSellers = "";

        for (CartItemViewModel cart:
                cartVMs.getValue()) {
            if(cart.isChecked().getValue() && cart.getReason().getValue().equals("") && !cart.isDeleted().getValue()){
                if(!selectedSellers.contains(cart.getEmailSeller()+"|")){
                    selectedSellers += cart.getEmailSeller()+"|";
                }
            }
        }

        return selectedSellers;
    }

    private int totalCheckedItems = 0;
    private MutableLiveData<ArrayList<CartItemViewModel>> cartVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>(true);
    private MutableLiveData<String> loadingTitle = new MutableLiveData<>();
    private MutableLiveData<Boolean> allChecked = new MutableLiveData<>(false);
    private MutableLiveData<Integer> total = new MutableLiveData<>(0);

    public LiveData<ArrayList<CartItemViewModel>> getCartVMs(){
        return cartVMs;
    }

    public LiveData<String> getLoadingTitle(){
        return loadingTitle;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<Boolean> isAllChecked(){
        return allChecked;
    }

    public void switchAllChecked(){
        Boolean checked = allChecked.getValue();
        allChecked.setValue(!checked);
    }

    public LiveData<Integer> getTotal(){
        return total;
    }

    public void setTotal(int total){
        this.total.setValue(total);
    }

    public int getTotalCheckedItems(){
        return totalCheckedItems;
    }
}
