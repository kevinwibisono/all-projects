package my.istts.finalproject.inputclasses;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.OngkirItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class PJInput {
    private String email_seller;
    private int totalProduk;
    private MutableLiveData<Date> tglGrooming = new MutableLiveData<>();

    private MutableLiveData<Date> tglAwalBooking = new MutableLiveData<>();
    private MutableLiveData<Date> tglAkhirBooking = new MutableLiveData<>();

    public MutableLiveData<String> catatan = new MutableLiveData<>("");
    private MutableLiveData<String> sellerNames = new MutableLiveData<>("");
    private MutableLiveData<String> sellerPics = new MutableLiveData<>("");

    private MutableLiveData<Integer> subtotal = new MutableLiveData<>(0);

    private MutableLiveData<String> promoName = new MutableLiveData<>("");
    private MutableLiveData<String> courierDetail = new MutableLiveData<>("");
    private MutableLiveData<String> courier = new MutableLiveData<>("");
    private MutableLiveData<Integer> ongkir = new MutableLiveData<>(0);

    public PJInput(String email_seller){
        this.email_seller = email_seller;

        AkunDBAccess akunDB = new AkunDBAccess();
        akunDB.getAccByEmail(email_seller).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Storage storage = new Storage();
                    storage.getPictureUrlFromName(email_seller).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sellerPics.setValue(uri.toString());
                            sellerNames.setValue(documentSnapshot.getString("nama"));
                        }
                    });

                }
            }
        });
    }

    public String getemail_seller(){
        return email_seller;
    }

    public LiveData<String> getSellerName(){
        return sellerNames;
    }

    public LiveData<String> getSellerPic(){
        return sellerPics;
    }

    public LiveData<Integer> getSubtotal(){
        return subtotal;
    }

    public void addSubtotal(int jumlah){
        totalProduk += jumlah;
        Integer subtotalNow = subtotal.getValue();
        subtotalNow += jumlah;
        subtotal.setValue(subtotalNow);
    }

    public void changeProductDiscount(int jumlahHarga, int jumlahHargaDiskon){
        Integer subtotalNow = subtotal.getValue();
        subtotalNow -= jumlahHarga;
        subtotalNow += jumlahHargaDiskon;
        subtotal.setValue(subtotalNow);
    }

    public void cancelProductDiscount(int jumlahHarga, int jumlahHargaDiskon){
        Integer subtotalNow = subtotal.getValue();
        subtotalNow += jumlahHarga;
        subtotalNow -= jumlahHargaDiskon;
        subtotal.setValue(subtotalNow);
    }

    public LiveData<String> getPromoName(){
        return promoName;
    }

    public LiveData<String> getCatatan(){
        return catatan;
    }

    public void setPromoName(String promoName){
        this.promoName.setValue(promoName);
    }

    public LiveData<String> getCourier(){
        return courier;
    }

    public LiveData<String> getCourierDetail(){
        return courierDetail;
    }

    public void setTglGrooming(Date tglGrooming) {
        this.tglGrooming.setValue(tglGrooming);
    }

    private void multiplySubtotal(long times){
        subtotal.setValue(totalProduk * (int) times);
    }

    public void setTglAwalBooking(Date tglAwalBooking) {
        this.tglAwalBooking.setValue(tglAwalBooking);
        if(tglAkhirBooking.getValue() != null){
            long diffInMillis = tglAkhirBooking.getValue().getTime() - tglAwalBooking.getTime();
            long diffInDays = diffInMillis / (1000 * 3600 * 24);
            if(diffInDays <= 0){
                diffInDays = 1;
            }
            else{
                diffInDays += 2;
            }
            multiplySubtotal(diffInDays);
        }
    }

    public void setTglAkhirBooking(Date tglAkhirBooking) {
        this.tglAkhirBooking.setValue(tglAkhirBooking);
        if(tglAwalBooking.getValue() != null){
            long diffInMillis = tglAkhirBooking.getTime() - tglAwalBooking.getValue().getTime();
            long diffInDays = diffInMillis / (1000 * 3600 * 24);
            if(diffInDays <= 0){
                diffInDays = 1;
            }
            else{
                diffInDays++;
            }
            multiplySubtotal(diffInDays);
        }
    }

    public LiveData<Date> getTglGrooming() {
        return tglGrooming;
    }

    public LiveData<Date> getTglAwalBooking() {
        return tglAwalBooking;
    }

    public LiveData<Date> getTglAkhirBooking() {
        return tglAkhirBooking;
    }

    public LiveData<Integer> getOngkir(){
        return ongkir;
    }

    public void setOngkir(OngkirItemViewModel vm){
        Integer subtotalNow = subtotal.getValue();
        subtotalNow -= ongkir.getValue();
        subtotalNow += vm.getHargaInt();
        subtotal.setValue(subtotalNow);
        courier.setValue(vm.getKurir());
        courierDetail.setValue(vm.getDetail());
        ongkir.setValue(vm.getHargaInt());
    }
}
