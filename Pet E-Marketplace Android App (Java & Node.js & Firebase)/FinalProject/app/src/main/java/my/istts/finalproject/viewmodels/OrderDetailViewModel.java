package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Alamat;
import my.istts.finalproject.models.AlamatDBAccess;
import my.istts.finalproject.models.DetailBookingHotel;
import my.istts.finalproject.models.DetailGrooming;
import my.istts.finalproject.models.DetailJanjiTemu;
import my.istts.finalproject.models.DetailPesanan;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.RiwayatDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.SendNotifResponse;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.ItemPJViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private RiwayatDBAccess riwayatDB;
    private Storage storage;
    private PesananJanjitemu pesananJanjitemu;
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;
    private BackendRetrofitService firebaseNotifService;

    public OrderDetailViewModel(Application app) {
        this.orderDB = new PesananJanjitemuDBAccess();
        this.riwayatDB = new RiwayatDBAccess(app);
        this.storage = new Storage();
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    public void getOrderDetails(String id_pj){
        itemPJsLoading.setValue(true);

        orderDB.getPJByIdSL(id_pj).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                itemVMs.setValue(new ArrayList<>());
                total.setValue(0);
                if(value.getData() != null){
                    PesananJanjitemu pj = new PesananJanjitemu(value);
                    pesananJanjitemu = pj;
                    jenisPJ.setValue(pj.getJenis());
                    tanggalPj.setValue(pj.gettanggalStr());
                    batalOtomatis.setValue(pj.getSelesai_otomatisStr());
                    statusStr.setValue(pj.getStatusStr());
                    finishable.setValue(pj.isFinishable());
                    alasanBatal.setValue(pj.getAlasan_batal());
                    reviewAdded.setValue(pj.isReviewGiven());
                    checkKomplainEnabled(pj);

                    orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            itemVMs.setValue(new ArrayList<>());
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<ItemPesananJanjitemu> itemPJs = new ArrayList<>();
                                for (DocumentSnapshot itemDoc:
                                        queryDocumentSnapshots.getDocuments()) {
                                    itemPJs.add(new ItemPesananJanjitemu(itemDoc));
                                }
                                addItemPJToVM(itemPJs, pj.getJenis());
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

                    getSellerDetails(pj.getemail_penjual());


                }
            }
        });
    }

    private void checkKomplainEnabled(PesananJanjitemu pj){
        if(pj.getJenis() == 0){
            if(pj.getStatus() == 4 || pj.getStatus() == 6){
                komplainEnabled.setValue(true);
            }
        }
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
                    koordinat.setValue(detailPesanan.getKoordinat());
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
                    int totalNow = total.getValue();
                    total.setValue((int) (totalNow * detailBooking.getDurasiPenginapan()));
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
                    koordinat.setValue(detailJT.getKoordinat());
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

    private void getSellerDetails(String email_seller){
        AkunDBAccess akunDB = new AkunDBAccess();
        akunDB.getAccByEmail(email_seller).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    storage.getPictureUrlFromName(email_seller).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            seller.setValue(email_seller);
                            sellerPic.setValue(uri.toString());
                            sellerName.setValue(documentSnapshot.getString("nama"));
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
            }
        });
    }

    public void finishOrder(){
        loading.setValue(true);

        int statusSelesai;
        if(pesananJanjitemu.getJenis() == 0){
            orderDB.getItemPJByPesanan(pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot itemPJDoc:
                         queryDocumentSnapshots.getDocuments()) {
                        ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(itemPJDoc);
                        productDB.incProductSold(itemPJ.getId_item(), itemPJ.getJumlah());
                    }
                }
            });
            statusSelesai = 7;
        }
        else if(pesananJanjitemu.getJenis() == 1){
            statusSelesai = 6;
        }
        else{
            orderDB.getItemPJByPesanan(pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot itemPJDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(itemPJDoc);
                        hotelDB.incHotelBooked(itemPJ.getId_item(), itemPJ.getJumlah());
                        hotelDB.reduceOccupiedRoomAfterCancel(itemPJ.getId_item(), itemPJ.getJumlah());
                    }
                }
            });
            statusSelesai = 4;
        }
        String topikEmail = pesananJanjitemu.getemail_penjual().substring(0, pesananJanjitemu.getemail_penjual().indexOf('@'));
        firebaseNotifService.sendNotif("Pesanan Telah Diselesaikan", "Pesanan Telah Diselesaikan Oleh Pelanggan", topikEmail);
        orderDB.finishOrder(statusSelesai, pesananJanjitemu.getId_pj()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                riwayatDB.addHistoryOrderSuccess(pesananJanjitemu.getTotal(), pesananJanjitemu.getemail_penjual());
                loading.setValue(false);
            }
        });
    }

    public void cancelOrder(String alasan){
        loading.setValue(true);

        int statusBatal;
        if(pesananJanjitemu.getJenis() == 0){
            statusBatal = 8;
        }
        else if(pesananJanjitemu.getJenis() == 1){
            statusBatal = 7;
        }
        else{
            statusBatal = 5;
        }
        String topikEmail = pesananJanjitemu.getemail_penjual().substring(0, pesananJanjitemu.getemail_penjual().indexOf('@'));
        firebaseNotifService.sendNotif("Pesanan Telah Dibatalkan", "Pesanan Telah Dibatalkan Oleh Pelanggan", topikEmail);
        addItemQtyAgainAfterCancel(pesananJanjitemu);
        orderDB.cancelOrder(statusBatal, pesananJanjitemu.getId_pj(), alasan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                riwayatDB.addHistoryCancel(pesananJanjitemu.getTotal(), pesananJanjitemu.getemail_pembeli());
                loading.setValue(false);
            }
        });
    }

    private void addItemQtyAgainAfterCancel(PesananJanjitemu pj){
        orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    for (DocumentSnapshot doc:
                            queryDocumentSnapshots.getDocuments()) {
                        ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(doc);
                        if(pj.getJenis() == 0){
                            ProductDBAccess productDB = new ProductDBAccess();
                            if(itemPJ.getId_variasi().equals("")){
                                productDB.addProductQtyAfterCancel(itemPJ.getId_item(), itemPJ.getJumlah());
                            }
                            else{
                                productDB.addVarQtyAfterCancel(itemPJ.getId_item(), itemPJ.getId_variasi(), itemPJ.getJumlah());
                            }
                        }
                        else if(pj.getJenis() == 2){
                            HotelDBAccess hotelDB = new HotelDBAccess();
                            hotelDB.reduceOccupiedRoomAfterCancel(itemPJ.getId_item(), itemPJ.getJumlah());
                        }
                    }
                }
            }
        });
    }

    private MutableLiveData<Integer> jenisPJ = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private MutableLiveData<ArrayList<ItemPJViewModel>> itemVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> itemPJsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> finishable = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> reviewAdded = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> komplainEnabled = new MutableLiveData<>(false);

    private MutableLiveData<String> statusStr = new MutableLiveData<>("");
    private MutableLiveData<String> tanggalPj = new MutableLiveData<>("");
    private MutableLiveData<String> batalOtomatis = new MutableLiveData<>("");
    private MutableLiveData<String> alasanBatal = new MutableLiveData<>("");

    private MutableLiveData<Integer> totalProduk = new MutableLiveData<>(0);
    private MutableLiveData<Integer> ongkir = new MutableLiveData<>(0);
    private MutableLiveData<Integer> total = new MutableLiveData<>(0);

    private MutableLiveData<String> paketKurir = new MutableLiveData<>("");
    private MutableLiveData<String> noResi = new MutableLiveData<>("");
    private MutableLiveData<String> catatan = new MutableLiveData<>("");
    private MutableLiveData<String> alamat = new MutableLiveData<>("");
    private MutableLiveData<String> koordinat = new MutableLiveData<>("");

    private MutableLiveData<String> metodeBayar = new MutableLiveData<>("");

    private MutableLiveData<String> tglGrooming = new MutableLiveData<>("");
    private MutableLiveData<String[]> koordinatGroomer = new MutableLiveData<>();

    private MutableLiveData<String> tglMulaiBooking = new MutableLiveData<>("");
    private MutableLiveData<String> tglSelesaiBooking = new MutableLiveData<>("");
    private MutableLiveData<Integer> durasiBooking = new MutableLiveData<>();

    private MutableLiveData<String> seller = new MutableLiveData<>("");
    private MutableLiveData<String> sellerName = new MutableLiveData<>("");
    private MutableLiveData<String> sellerPic = new MutableLiveData<>("");

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

    public LiveData<Boolean> isFinishable(){
        return finishable;
    }

    public LiveData<Boolean> isReviewAdded(){
        return reviewAdded;
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

    public LiveData<String> getSellerPhone(){
        return seller;
    }

    public LiveData<String> getSellerName(){
        return sellerName;
    }

    public LiveData<String> getSellerPic(){
        return sellerPic;
    }

    public LiveData<String> getKoordinat() {
        return koordinat;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> getComplainEnabled() {
        return komplainEnabled;
    }
}
