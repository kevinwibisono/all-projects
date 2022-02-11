package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.inputclasses.PJInput;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class HotelCheckoutItemVM {
    private Cart cart;
    private PJInput pjInput;
    private boolean showSeller, showDatePicker;

    public HotelCheckoutItemVM(Cart cart, PJInput pjInput, boolean showSeller, boolean showDatePicker) {
        this.cart = cart;
        this.pjInput = pjInput;
        this.showSeller = showSeller;
        this.showDatePicker = showDatePicker;

        HotelDBAccess hotelDB = new HotelDBAccess();
        hotelDB.getRoomById(cart.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Hotel hotel = new Hotel(documentSnapshot);

                    nama.setValue(hotel.getNama());
                    harga.setValue(hotel.getHarga());
                    pjInput.addSubtotal(hotel.getHarga() * cart.getJumlah());

                    Storage storage = new Storage();
                    storage.getPictureUrlFromName(hotel.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            picture.setValue(uri.toString());
                        }
                    });
                }
            }
        });
    }


    private MutableLiveData<String> nama = new MutableLiveData<>("");
    private MutableLiveData<String> picture = new MutableLiveData<>("");
    private MutableLiveData<Integer> harga = new MutableLiveData<>(0);

    public LiveData<String> getName(){
        return nama;
    }

    public LiveData<String> getPicture(){
        return picture;
    }

    public LiveData<Integer> getHarga(){
        return harga;
    }

    public String getIdHotel(){
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

    public void setBeginHotelBooking(Date date){
        pjInput.setTglAwalBooking(date);
    }

    public void setEndHotelBooking(Date date){
        pjInput.setTglAkhirBooking(date);
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
