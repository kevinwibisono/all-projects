package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.DetailBookingHotel;
import my.istts.finalproject.models.DetailGrooming;
import my.istts.finalproject.models.DetailPesanan;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ItemPaymentViewModel {
    private boolean showSeller;
    private boolean showDetail;

    public ItemPaymentViewModel(ItemPesananJanjitemu itemPJ, PesananJanjitemu pj) {
        this.showSeller = false;
        this.showDetail = false;
        sellerEmail = pj.getemail_penjual();
        itemId = itemPJ.getId_item();
        tipe = pj.getJenis();

        AkunDBAccess akunDB = new AkunDBAccess();
        akunDB.getAccByEmail(sellerEmail).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Storage storage = new Storage();
                    storage.getPictureUrlFromName(sellerEmail).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sellerPic.setValue(uri.toString());
                            sellerName.setValue(documentSnapshot.getString("nama"));
                        }
                    });
                }
            }
        });

        PesananJanjitemuDBAccess orderDB = new PesananJanjitemuDBAccess();
        if(tipe == 0){
            orderDB.getDetailPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                        DocumentSnapshot detailPJDoc = queryDocumentSnapshots.getDocuments().get(0);
                        DetailPesanan detailPesanan = new DetailPesanan(detailPJDoc);
                        kurir.setValue(detailPesanan.getKurir());
                        paketKurir.setValue(detailPesanan.getPaket_kurir());
                        ongkir.setValue(detailPesanan.getOngkir());
                        catatan.setValue(detailPesanan.getCatatan());
                    }
                }
            });
        }
        else if(tipe == 1){
            orderDB.getDetailGrooming(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                        DetailGrooming detailGrooming = new DetailGrooming(queryDocumentSnapshots.getDocuments().get(0));
                        tglBookingGrooming.setValue(detailGrooming.getTglBooking());
                    }
                }
            });
        }
        else{
            orderDB.getDetailHotelBooking(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                        DetailBookingHotel detailBookingHotel = new DetailBookingHotel(queryDocumentSnapshots.getDocuments().get(0));
                        tglBookingMasuk.setValue(detailBookingHotel.getTglMasuk());
                        tglBookingKeluar.setValue(detailBookingHotel.getTglAkhir());
                    }
                }
            });
        }

        nama.setValue(itemPJ.getNama());
        harga.setValue(itemPJ.getHarga());
        jumlah.setValue(itemPJ.getJumlah());
        picture.setValue(itemPJ.getGambar());
        totalBerat.setValue(itemPJ.getTotal_berat());
    }

    private String sellerEmail;
    private MutableLiveData<String> sellerName = new MutableLiveData<>("");
    private MutableLiveData<String> sellerPic = new MutableLiveData<>("");

    private String itemId;
    private int tipe = -1;
    private MutableLiveData<String> picture = new MutableLiveData<>("");
    private MutableLiveData<String> nama = new MutableLiveData<>("");
    private MutableLiveData<String> variasi = new MutableLiveData<>("");
    private MutableLiveData<Integer> harga = new MutableLiveData<>(0);
    private MutableLiveData<Integer> jumlah = new MutableLiveData<>(0);
    private MutableLiveData<Integer> totalBerat = new MutableLiveData<>(0);

    private MutableLiveData<String> tglBookingGrooming = new MutableLiveData<>("");

    private MutableLiveData<String> tglBookingMasuk = new MutableLiveData<>();
    private MutableLiveData<String> tglBookingKeluar = new MutableLiveData<>();

    private MutableLiveData<String> catatan = new MutableLiveData<>("");
    private MutableLiveData<String> kurir = new MutableLiveData<>("");
    private MutableLiveData<String> paketKurir = new MutableLiveData<>("");
    private MutableLiveData<Integer> ongkir = new MutableLiveData<>(0);

    public String getSeller(){
        return sellerEmail;
    }

    public String getItemId(){
        return itemId;
    }

    public int getTipe(){
        return tipe;
    }

    public LiveData<String> getCatatan(){
        return catatan;
    }

    public LiveData<String> getSellerName(){
        return sellerName;
    }

    public LiveData<String> getSellerPic(){
        return sellerPic;
    }

    public LiveData<String> getPicture(){
        return picture;
    }

    public LiveData<String> getName(){
        return nama;
    }

    public LiveData<String> getVariasi(){
        return variasi;
    }

    public LiveData<Integer> getHarga(){
        return harga;
    }

    public LiveData<Integer> getJumlah(){
        return jumlah;
    }

    public LiveData<Integer> getTotalBerat(){
        return totalBerat;
    }

    public LiveData<String> getKurir(){
        return kurir;
    }

    public LiveData<String> getPaketKurir(){
        return paketKurir;
    }

    public LiveData<String> getTglGrooming(){
        return tglBookingGrooming;
    }

    public LiveData<String> getTglBookingMasuk(){
        return tglBookingMasuk;
    }

    public LiveData<String> getTglBookingKeluar(){
        return tglBookingKeluar;
    }

    public LiveData<Integer> getOngkir(){
        return ongkir;
    }

    public boolean isSellerShown(){
        return showSeller;
    }

    public boolean isDetailShown(){
        return showDetail;
    }

    public void setShowSeller(boolean showSeller) {
        this.showSeller = showSeller;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }
}
