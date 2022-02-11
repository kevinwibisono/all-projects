package com.example.sellerapp.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.DetailBookingHotel;
import com.example.sellerapp.models.DetailGrooming;
import com.example.sellerapp.models.DetailJanjiTemu;
import com.example.sellerapp.models.DetailPesanan;
import com.example.sellerapp.models.ItemPesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PesananItemViewModel {
    private PesananJanjitemu pj;
    private PesananJanjitemuDBAccess orderDB;

    public PesananItemViewModel(PesananJanjitemu pj) {
        orderDB = new PesananJanjitemuDBAccess();
        this.pj = pj;
        this.jenis = pj.getJenis();

        if(jenis == 3){
            jenisItem = "Klinik";
            getJanjiTemuDetail(pj.getId_pj());
        }
        else if(jenis == 1){
            jenisItem = "Paket";
            getGroomingDetail(pj.getId_pj());
        }
        else if(jenis == 2){
            jenisItem = "Kamar";
            getHotelBookingDetail(pj.getId_pj());
        }
        else{
            jenisItem = "Produk";
            getShoppingDetail(pj.getId_pj());
        }
        String[] types = {"Pet Shopping", "Pet Grooming", "Pet Hotel", "Pet Clinic"};
        this.jenisStr = types[jenis];

        Storage storage = new Storage();
        AkunDBAccess akunDB = new AkunDBAccess();
        akunDB.getAccByEmail(pj.getemail_pembeli()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                storage.getPictureUrlFromName(pj.getemail_pembeli()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        buyerPicture.setValue(uri.toString());
                        buyerName.setValue(documentSnapshot.getString("nama"));
                    }
                });
            }
        });

        orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    jumItem.setValue(queryDocumentSnapshots.getDocuments().size());
                    DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                    ItemPesananJanjitemu firstItem = new ItemPesananJanjitemu(firstDoc);
                    itemPicture.setValue(firstItem.getGambar());
                    itemName.setValue(firstItem.getNama());
                    itemPrice.setValue(firstItem.getHarga());
                    itemVariasi.setValue(firstItem.getVariasi());
                    itemJumlah.setValue(firstItem.getJumlah());
                }
            }
        });
    }

    private void getShoppingDetail(String id_pj){
        orderDB.getDetailPesanan(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                    DetailPesanan pesanan = new DetailPesanan(firstDoc);
                    catatan.setValue(pesanan.getCatatan());
                }
            }
        });
    }

    private void getJanjiTemuDetail(String id_pj){
        orderDB.getDetailJanjiTemu(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                    DetailJanjiTemu janjiTemu = new DetailJanjiTemu(firstDoc);
                    appoType.setValue(janjiTemu.getJenis_janjitemu());
                    tglJanjitemu.setValue(janjiTemu.getTglJanjitemu());
                }
            }
        });
    }

    private void getGroomingDetail(String id_pj){
        orderDB.getDetailGrooming(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                    DetailGrooming grooming = new DetailGrooming(firstDoc);
                    tglBooking.setValue(grooming.getTglBooking());
                }
            }
        });
    }

    private void getHotelBookingDetail(String id_pj){
        orderDB.getDetailHotelBooking(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                    DetailBookingHotel booking = new DetailBookingHotel(firstDoc);
                    tglAwal.setValue(booking.getTglMasuk());
                    tglAkhir.setValue(booking.getTglKeluar());
                    durasiHotelBooking.setValue(String.valueOf(booking.getDurasiPenginapan()));
                }
            }
        });
    }

    private int jenis;
    private String jenisStr;
    private String jenisItem;

    private MutableLiveData<String> buyerPicture = new MutableLiveData<>("");
    private MutableLiveData<String> buyerName = new MutableLiveData<>("");

    private MutableLiveData<String> tglBooking = new MutableLiveData<>("");

    private MutableLiveData<String> tglJanjitemu = new MutableLiveData<>("");
    private MutableLiveData<String> appoType = new MutableLiveData<>("");

    private MutableLiveData<String> tglAwal = new MutableLiveData<>("");
    private MutableLiveData<String> tglAkhir = new MutableLiveData<>("");
    private MutableLiveData<String> durasiHotelBooking = new MutableLiveData<>("");

    private MutableLiveData<String> catatan = new MutableLiveData<>("");
    private MutableLiveData<String> itemPicture = new MutableLiveData<>("");
    private MutableLiveData<String> itemName = new MutableLiveData<>("");
    private MutableLiveData<Integer> itemPrice = new MutableLiveData<>(0);
    private MutableLiveData<String> itemVariasi = new MutableLiveData<>("");
    private MutableLiveData<Integer> itemJumlah = new MutableLiveData<>(0);
    private MutableLiveData<Integer> jumItem = new MutableLiveData<>(0);

    public Integer getJenis(){
        return jenis;
    }

    public String getJenisStr(){
        return jenisStr;
    }

    public String getJenisItem(){
        return jenisItem;
    }

    public Integer getTotal(){
        return pj.getTotal();
    }

    public String getId(){
        return pj.getId_pj();
    }

    public String getStatusStr(){
        return pj.getStatusStr();
    }

    public int getStatus(){
        return pj.getStatus();
    }

    public boolean isFinishable(){
        return pj.isFinishable();
    }

    public String getTanggalPesanan(){
        return pj.gettanggalStr();
    }

    public String getSelesaiOtomatis(){
        return pj.getSelesai_otomatisStr();
    }

    public LiveData<String> getCatatan(){
        return catatan;
    }

    public LiveData<String> getBuyerPicture(){
        return buyerPicture;
    }

    public LiveData<String> getBuyerName(){
        return buyerName;
    }

    public LiveData<String> getAppoType(){
        return appoType;
    }

    public LiveData<String> getTglBooking(){
        return tglBooking;
    }

    public LiveData<String> getTglAwal(){
        return tglAwal;
    }

    public LiveData<String> getTglAkhir(){
        return tglAkhir;
    }

    public LiveData<String> getTglJanjitemu(){
        return tglJanjitemu;
    }

    public LiveData<String> getDurasiBooking(){
        return durasiHotelBooking;
    }

    public LiveData<String> getItemPicture(){
        return itemPicture;
    }

    public LiveData<String> getItemName(){
        return itemName;
    }

    public LiveData<String> getItemVariant(){
        return itemVariasi;
    }

    public LiveData<Integer> getItemPrice(){
        return itemPrice;
    }

    public LiveData<Integer> getItemQty(){
        return itemJumlah;
    }

    public LiveData<Integer> getJumlahItem(){
        return jumItem;
    }
}
