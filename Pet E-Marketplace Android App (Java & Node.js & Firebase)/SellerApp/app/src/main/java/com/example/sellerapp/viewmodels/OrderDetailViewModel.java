package com.example.sellerapp.viewmodels;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Alamat;
import com.example.sellerapp.models.AlamatDBAccess;
import com.example.sellerapp.models.DetailBookingHotel;
import com.example.sellerapp.models.DetailGrooming;
import com.example.sellerapp.models.DetailJanjiTemu;
import com.example.sellerapp.models.DetailPesanan;
import com.example.sellerapp.models.ItemPesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.backend.BackendRetrofitService;
import com.example.sellerapp.models.backend.BackendRetrofitClient;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.viewmodels.itemviewmodels.ItemPJViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderDetailViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private Storage storage;
    private PesananJanjitemu pesananJanjitemu;
    private BackendRetrofitService firebaseNotifService;

    public OrderDetailViewModel() {
        this.orderDB = new PesananJanjitemuDBAccess();
        this.storage = new Storage();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    public void getOrderDetails(String id_pj){
        itemPJsLoading.setValue(true);

        orderDB.getPJByIdSL(id_pj).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                itemVMs.setValue(new ArrayList<>());
                total.setValue(0);
                if(value != null){
                    PesananJanjitemu pj = new PesananJanjitemu(value);
                    pesananJanjitemu = pj;
                    idPJ.setValue(id_pj);

                    jenisPJ.setValue(pj.getJenis());
                    tanggalPj.setValue(pj.gettanggalStr());
                    batalOtomatis.setValue(pj.getSelesai_otomatisStr());
                    statusStr.setValue(pj.getStatusStr());
                    alasanBatal.setValue(pj.getAlasan_batal());
                    buyer.setValue(pj.getemail_pembeli());
                    finishable.setValue(pj.isFinishable());

                    orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<ItemPesananJanjitemu> itemPJs = new ArrayList<>();
                                for (DocumentSnapshot itemDoc:
                                        queryDocumentSnapshots.getDocuments()) {
                                    itemPJs.add(new ItemPesananJanjitemu(itemDoc));
                                }
                                addItemPJToVM(itemPJs, pj.getJenis());
                            }
                        }
                    });

                    getBuyerDetails(pj.getemail_pembeli());

                    if(pj.getJenis() == 0){
                        getPJDetailShop(pj.getId_pj());
                    }
                    else if(pj.getJenis() == 1){
                        getPJDetailGroom(pj.getId_pj());
                    }
                    else if(pj.getJenis() == 2){
                        getPJDetailBooking(pj.getId_pj());
                    }
                    else{
                        getPJDetailJanjiTemu(pj.getId_pj());
                    }
                }
            }
        });
    }

    private void getPJDetailShop(String id_pj){
        orderDB.getDetailPesanan(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DetailPesanan detailPesanan = new DetailPesanan(queryDocumentSnapshots.getDocuments().get(0));
                    paketKurir.setValue(detailPesanan.getPaket_kurir());
                    ongkir.setValue(detailPesanan.getOngkir());
                    metodeBayar.setValue(detailPesanan.getMetode_bayar());
                    catatan.setValue(detailPesanan.getCatatan());
                    noResi.setValue(detailPesanan.getNo_resi());
                    alamat.setValue(detailPesanan.getAlamat());
                    coordinate.setValue(detailPesanan.getKoordinat());
                    addTotal(detailPesanan.getOngkir());
                }
            }
        });
    }

    private void getPJDetailGroom(String id_pj){
        orderDB.getDetailGrooming(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DetailGrooming detailGrooming = new DetailGrooming(queryDocumentSnapshots.getDocuments().get(0));
                    getAddressDetail(detailGrooming.getId_alamat());
                    tglGrooming.setValue(detailGrooming.getTglBooking());
                    koordinatGroomer.setValue(detailGrooming.getPosisi_groomer());
                }
            }
        });
    }

    private void getPJDetailBooking(String id_pj){
        orderDB.getDetailHotelBooking(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DetailBookingHotel detailBooking = new DetailBookingHotel(queryDocumentSnapshots.getDocuments().get(0));
                    tglMulaiBooking.setValue(detailBooking.getTglMasuk());
                    tglSelesaiBooking.setValue(detailBooking.getTglKeluar());
                    metodeBayar.setValue(detailBooking.getMetode_bayar());
                    durasiBooking.setValue((int) detailBooking.getDurasiPenginapan());
                }
            }
        });
    }

    private void getPJDetailJanjiTemu(String id_pj){
        orderDB.getDetailJanjiTemu(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DetailJanjiTemu detailJT = new DetailJanjiTemu(queryDocumentSnapshots.getDocuments().get(0));
                    daftarJenisPasien.setValue(detailJT.getDaftar_jenis());
                    daftarNamaPasien.setValue(detailJT.getDaftar_nama());
                    daftarUsiaPasien.setValue(detailJT.getDaftar_usia());
                    alamat.setValue(detailJT.getAlamat());
                    coordinate.setValue(detailJT.getKoordinat());
                    jenisJanjiTemu.setValue(detailJT.getJenis_janjitemu());
                }
            }
        });
    }

    private void addItemPJToVM(ArrayList<ItemPesananJanjitemu> itemPJs, int tipe){
        int totalProdukNow = 0;
        ArrayList<ItemPJViewModel> currentVMs = itemVMs.getValue();
        for (ItemPesananJanjitemu itempj:
             itemPJs) {
            totalProdukNow += (itempj.getHarga() * itempj.getJumlah());
            currentVMs.add(new ItemPJViewModel(itempj, tipe));
        }
        totalProduk.setValue(totalProdukNow);
        addTotal(totalProdukNow);
        itemVMs.setValue(currentVMs);
        itemPJsLoading.setValue(false);
    }

    private void addTotal(int add){
        Integer totalNow = total.getValue();
        totalNow += add;
        total.setValue(totalNow);
    }

    private void getBuyerDetails(String email){
        AkunDBAccess akunDB = new AkunDBAccess();
        akunDB.getAccByEmail(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    storage.getPictureUrlFromName(email).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            buyer.setValue(email);
                            buyerPic.setValue(uri.toString());
                            buyerName.setValue(documentSnapshot.getString("nama"));
                        }
                    });
                }
            }
        });
    }

    private void getAddressDetail(String id_alamat){
        AlamatDBAccess addrDB = new AlamatDBAccess();
        addrDB.getAddressById(id_alamat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Alamat address = new Alamat(documentSnapshot);
                alamat.setValue(address.toString());
                coordinate.setValue(address.getKoordinat());
            }
        });
    }

    public void acceptOrder(){
        loading.setValue(true);
        String topikEmail = pesananJanjitemu.getemail_pembeli().substring(0, pesananJanjitemu.getemail_pembeli().indexOf('@'));
        firebaseNotifService.sendNotif("Pesanan Telah Diterima", "Pesanan Telah Diterima", topikEmail);
        orderDB.getDetailPesanan(pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    //tipe pj shopping
                    DetailPesanan detailPesanan = new DetailPesanan(queryDocumentSnapshots.getDocuments().get(0));

                    if(detailPesanan.getKurir().equals("pickup")){
                        orderDB.acceptOrderPickup(pesananJanjitemu.getId_pj(), pesananJanjitemu.getSelesai_otomatis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loading.setValue(false);
                            }
                        });
                    }
                    else{
                        orderDB.acceptOrder(pesananJanjitemu.getId_pj(), pesananJanjitemu.getSelesai_otomatis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loading.setValue(false);
                            }
                        });
                    }
                }
                else{
                    //bukan tipe pj shopping
                    orderDB.acceptOrderBookingAppo(pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loading.setValue(false);
                        }
                    });
                }
            }
        });
    }

    public void deliverOrder(String no_resi){
        loading.setValue(true);
        String topikEmail = pesananJanjitemu.getemail_pembeli().substring(0, pesananJanjitemu.getemail_pembeli().indexOf('@'));
        firebaseNotifService.sendNotif("Pesanan Telah Dikirim", "Pesanan Sedang Dalam Perjalanan", topikEmail);
        orderDB.getDetailPesanan(pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    String id = queryDocumentSnapshots.getDocuments().get(0).getId();

                    orderDB.setNoResi(no_resi, id).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            orderDB.deliverOrder(pesananJanjitemu.getId_pj(), pesananJanjitemu.getSelesai_otomatis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loading.setValue(false);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void readyOrderForPickup(){
        loading.setValue(true);

        String topikEmail = pesananJanjitemu.getemail_pembeli().substring(0, pesananJanjitemu.getemail_pembeli().indexOf('@'));
        firebaseNotifService.sendNotif("Pesanan Siap Untuk Pickup", "Pesananmu Sudah Siap Untuk Diambil", topikEmail);
        orderDB.readyForPickup(pesananJanjitemu.getId_pj(), pesananJanjitemu.getSelesai_otomatis()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.setValue(false);
            }
        });
    }

    public void groomerOtw(double lat, double lng){
        loading.setValue(true);

        String topikEmail = pesananJanjitemu.getemail_pembeli().substring(0, pesananJanjitemu.getemail_pembeli().indexOf('@'));
        firebaseNotifService.sendNotif("Groomer Dalam Perjalanan", "Groomer Dalam Perjalanan", topikEmail);
        orderDB.groomerOtw(pesananJanjitemu.getId_pj(), lat, lng).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.setValue(false);
            }
        });

    }

    public void groomerArrive(){
        loading.setValue(true);

        String topikEmail = pesananJanjitemu.getemail_pembeli().substring(0, pesananJanjitemu.getemail_pembeli().indexOf('@'));
        firebaseNotifService.sendNotif("Groomer Sampai Di Tujuan", "Groomer Sampai Di Tujuan", topikEmail);
        orderDB.groomerArrive(pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.setValue(false);
            }
        });
    }

    private MutableLiveData<Integer> jenisPJ = new MutableLiveData<>();
    private MutableLiveData<String> idPJ = new MutableLiveData<>("");

    private MutableLiveData<ArrayList<ItemPJViewModel>> itemVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> itemPJsLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> gettingCoordinates = new MutableLiveData<>();

    private MutableLiveData<String> statusStr = new MutableLiveData<>("");
    private MutableLiveData<String> tanggalPj = new MutableLiveData<>("");
    private MutableLiveData<String> batalOtomatis = new MutableLiveData<>("");
    private MutableLiveData<String> alasanBatal = new MutableLiveData<>("");
    private MutableLiveData<Boolean> finishable = new MutableLiveData<>();

    private MutableLiveData<Integer> totalProduk = new MutableLiveData<>(0);
    private MutableLiveData<Integer> ongkir = new MutableLiveData<>(0);
    private MutableLiveData<Integer> total = new MutableLiveData<>(0);

    private MutableLiveData<String> paketKurir = new MutableLiveData<>("");
    private MutableLiveData<String> noResi = new MutableLiveData<>("");
    private MutableLiveData<String> catatan = new MutableLiveData<>("");
    private MutableLiveData<String> alamat = new MutableLiveData<>("");
    private MutableLiveData<String> coordinate = new MutableLiveData<>("");

    private MutableLiveData<String> metodeBayar = new MutableLiveData<>("");

    private MutableLiveData<String> tglGrooming = new MutableLiveData<>("");
    private MutableLiveData<String[]> koordinatGroomer = new MutableLiveData<>();

    private MutableLiveData<String> tglMulaiBooking = new MutableLiveData<>("");
    private MutableLiveData<String> tglSelesaiBooking = new MutableLiveData<>("");
    private MutableLiveData<Integer> durasiBooking = new MutableLiveData<>();

    private MutableLiveData<String> buyer = new MutableLiveData<>("");
    private MutableLiveData<String> buyerName = new MutableLiveData<>("");
    private MutableLiveData<String> buyerPic = new MutableLiveData<>("");

    private MutableLiveData<String[]> daftarNamaPasien = new MutableLiveData<>();
    private MutableLiveData<String[]> daftarUsiaPasien = new MutableLiveData<>();
    private MutableLiveData<String[]> daftarJenisPasien = new MutableLiveData<>();
    private MutableLiveData<String> jenisJanjiTemu = new MutableLiveData<>("");

    public LiveData<ArrayList<ItemPJViewModel>> getItemPJs(){
        return itemVMs;
    }

    public LiveData<Boolean> isItemPJsLoading(){
        return itemPJsLoading;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<String> getStatus(){
        return statusStr;
    }

    public LiveData<String> getAlasanBatal(){
        return alasanBatal;
    }

    public LiveData<String> getTanggalPJ(){
        return tanggalPj;
    }

    public LiveData<String> getBatalOtomatis(){
        return batalOtomatis;
    }

    public LiveData<Integer> getJenisPJ(){
        return jenisPJ;
    }

    public LiveData<Integer> getTotalProduk(){
        return totalProduk;
    }

    public LiveData<Integer> getOngkir(){
        return ongkir;
    }

    public LiveData<Integer> getTotal(){
        return total;
    }

    public LiveData<String> getPaketKurir(){
        return paketKurir;
    }

    public LiveData<String> getNoResi(){
        return noResi;
    }

    public LiveData<String> getTglMulaiBooking(){
        return tglMulaiBooking;
    }

    public LiveData<String> getTglSelesaiBooking(){
        return tglSelesaiBooking;
    }

    public LiveData<Integer> getDurasiBooking(){
        return durasiBooking;
    }

    public LiveData<String> getTglGrooming(){
        return tglGrooming;
    }

    public LiveData<String> getCatatan(){
        return catatan;
    }

    public LiveData<String> getMetodeBayar(){
        return metodeBayar;
    }

    public LiveData<String> getAlamat(){
        return alamat;
    }

    public LiveData<String> getCoordinate(){
        return coordinate;
    }

    public LiveData<String> getBuyerEmail(){
        return buyer;
    }

    public LiveData<String> getBuyerName(){
        return buyerName;
    }

    public LiveData<String> getBuyerPic(){
        return buyerPic;
    }

    public LiveData<Boolean> isFinishable(){
        return finishable;
    }

    public LiveData<String> getIdPJ() {
        return idPJ;
    }

    public LiveData<Boolean> isGettingCoordinates() {
        return gettingCoordinates;
    }

    public void setGettingCoordinates(Boolean gettingCoordinates) {
        this.gettingCoordinates.setValue(gettingCoordinates);
    }
}
