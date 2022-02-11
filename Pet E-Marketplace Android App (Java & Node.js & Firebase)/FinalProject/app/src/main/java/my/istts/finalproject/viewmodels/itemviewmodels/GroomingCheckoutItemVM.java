package my.istts.finalproject.viewmodels.itemviewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.inputclasses.PJInput;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.PaketGrooming;
import my.istts.finalproject.models.PaketGroomingDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class GroomingCheckoutItemVM {
    private Cart cart;
    private PJInput pjInput;
    private boolean showSeller, showDatePicker;

    public GroomingCheckoutItemVM(Cart cart, PJInput pjInput, boolean showSeller, boolean showDatePicker) {
        this.cart = cart;
        this.pjInput = pjInput;
        this.showSeller = showSeller;
        this.showDatePicker = showDatePicker;

        PaketGroomingDBAccess groomingDB = new PaketGroomingDBAccess();
        groomingDB.getPackageById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    PaketGrooming paketGrooming = new PaketGrooming(documentSnapshot);
                    nama.setValue(paketGrooming.getNama());
                    harga.setValue(paketGrooming.getHarga());
                    pjInput.addSubtotal(paketGrooming.getHarga() * cart.getJumlah());
                }
            }
        });
    }


    private MutableLiveData<String> nama = new MutableLiveData<>("");
    private MutableLiveData<Integer> harga = new MutableLiveData<>(0);

    public LiveData<String> getName(){
        return nama;
    }

    public LiveData<Integer> getHarga(){
        return harga;
    }

    public String getIdPaket(){
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

    public void setGroomingDate(Date date){
        pjInput.setTglGrooming(date);
    }

    public void setShowSeller(boolean showSeller) {
        this.showSeller = showSeller;
    }

    public void setshowDatePicker(boolean showDatePicker) {
        this.showDatePicker = showDatePicker;
    }

    public boolean isSellerShown(){
        return this.showSeller;
    }

    public boolean isDatePickerShown(){
        return this.showDatePicker;
    }
}
