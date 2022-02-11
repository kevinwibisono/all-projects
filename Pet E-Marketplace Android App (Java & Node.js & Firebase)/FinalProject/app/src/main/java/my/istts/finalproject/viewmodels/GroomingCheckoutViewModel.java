package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.inputclasses.PJInput;
import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Alamat;
import my.istts.finalproject.models.AlamatDBAccess;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.Pembayaran;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.ipaymu.cstore.ResultCstore;
import my.istts.finalproject.models.ipaymu.cstore.ResultSidCstore;
import my.istts.finalproject.models.ipaymu.qris.ResultQRIS;
import my.istts.finalproject.models.ipaymu.transfer.ResultTransferBank;
import my.istts.finalproject.models.ipaymu.va.ResultVA;
import my.istts.finalproject.viewmodels.itemviewmodels.AddressItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.GroomingCheckoutItemVM;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroomingCheckoutViewModel {
    private AlamatDBAccess alamatDB;
    private CartDBAccess cartDB;
    private AkunDBAccess akunDB;
    private PesananJanjitemuDBAccess orderDB;
    private Application app;

    public GroomingCheckoutViewModel(Application app) {
        this.app = app;
        this.alamatDB = new AlamatDBAccess();
        this.cartDB = new CartDBAccess(app);
        this.akunDB = new AkunDBAccess(app);
        this.orderDB = new PesananJanjitemuDBAccess();
    }

    private boolean allItemAdded, paymentIdGenerated, detailOrderAdded;
    private String paymentId = "";
    private ArrayList<PJInput> pjs = new ArrayList<>();
    private ArrayList<Integer> counterPJItemAdded = new ArrayList<>();
    private MutableLiveData<ArrayList<GroomingCheckoutItemVM>> checkoutVM = new MutableLiveData<>();
    private MutableLiveData<AddressItemViewModel> addrVM = new MutableLiveData<>();
    private MutableLiveData<Boolean> addrLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkoutLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> proceedable = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> checkoutProcessLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> checkoutDone = new MutableLiveData<>();
    private MutableLiveData<Integer> total = new MutableLiveData<>(0);
    private MutableLiveData<String> pembayaran = new MutableLiveData<>("");
    private MutableLiveData<Integer> saldo = new MutableLiveData<>(0);

    public void getSelectedAddress(){
        addrVM.setValue(null);
        addrLoading.setValue(true);
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    alamatDB.getSelectedAddress(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                DocumentSnapshot chosenDoc = queryDocumentSnapshots.getDocuments().get(0);
                                addrVM.setValue(new AddressItemViewModel(new Alamat(chosenDoc)));
                            }
                            addrLoading.setValue(false);
                            if(allDateChosen() && !pembayaran.getValue().equals("")){
                                proceedable.setValue(true);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void setSelectedAddress(String idAlamat){
        addrLoading.setValue(true);
        alamatDB.getAddressById(idAlamat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                addrVM.setValue(new AddressItemViewModel(new Alamat(documentSnapshot)));
                addrLoading.setValue(false);
                if(allDateChosen() && !pembayaran.getValue().equals("")){
                    proceedable.setValue(true);
                }
            }
        });
    }

    public void getCartItems(String selectedCarts, String selectedSellers){
        checkoutLoading.setValue(true);
        String[] sellers = selectedSellers.split("\\|");
        String[] cartsIds = selectedCarts.split("\\|");

        for (int i = 0; i < sellers.length; i++) {
            pjs.add(new PJInput(sellers[i]));
        }

        ArrayList<Integer> counters = new ArrayList<>();
        checkoutVM.setValue(new ArrayList<>());
        cartDB.setItemsGotListener(new CartDBAccess.onCartItemsGot() {
            @Override
            public void onGotItems(List<Cart> items) {
                if(items.size() > 0){
                    counters.add(1);
                    Boolean last = false;
                    if(counters.size() == cartsIds.length){
                        last = true;
                    }
                    addGroomingCheckoutItem(items.get(0), getPJForCart(items.get(0).getEmail_seller()), last);
                }
            }
        });
        for (int i = 0; i < cartsIds.length; i++) {
            cartDB.getCartWithId(Integer.parseInt(cartsIds[i]));
        }
    }

    private PJInput getPJForCart(String hp_seller){
        PJInput fittedPJ = null;
        for (PJInput pj:
             pjs) {
            if(pj.getemail_seller().equals(hp_seller)){
                fittedPJ = pj;
            }
        }
        return fittedPJ;
    }

    private void addGroomingCheckoutItem(Cart cart, PJInput pjInput, boolean last){
        ArrayList<GroomingCheckoutItemVM> vmsNow = checkoutVM.getValue();
        vmsNow.add(new GroomingCheckoutItemVM(cart, pjInput, false, false));
        checkoutVM.setValue(vmsNow);
        if(last){
            setCheckoutItemsShowSellerOngkir();
        }
    }

    private void setCheckoutItemsShowSellerOngkir(){
        ArrayList<GroomingCheckoutItemVM> vmsNow = checkoutVM.getValue();
        String lastSeller = "";
        for (int i = 0; i < vmsNow.size(); i++) {
            GroomingCheckoutItemVM checkout = vmsNow.get(i);
            if(!checkout.getSeller().equals(lastSeller)){
                checkout.setShowSeller(true);
                lastSeller = checkout.getSeller();
            }
            if(i < vmsNow.size() -1){
                if(!checkout.getSeller().equals(vmsNow.get(i+1).getSeller())){
                    //jika checkout item berikutnya tdk sama sellernya, berarti ini item terakhir dari seller ini
                    //munculkan opsi ongkir, opsi promo dan subtotal
                    checkout.setshowDatePicker(true);
                }
            }
            else{
                checkout.setshowDatePicker(true);
            }
        }
        checkoutLoading.setValue(false);
    }

    public void recountTotal(){
        Integer totalNew = 0;
        for (PJInput pj:
             pjs) {
            totalNew += pj.getSubtotal().getValue();
        }
        total.setValue(totalNew);
    }

    public void chooseGroomingDate(){
        if(allDateChosen() && !pembayaran.getValue().equals("") && addrVM.getValue() != null){
            proceedable.setValue(true);
        }
    }

    private boolean allDateChosen(){
        boolean allSelected = true;
        for (PJInput pj:
                pjs) {
            if(pj.getTglGrooming().getValue() == null){
                allSelected = false;
            }
        }
        return allSelected;
    }

    public void getMySaldo(){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    akunDB.getAccByEmail(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            int saldoDB = documentSnapshot.getLong("saldo").intValue();
                            saldo.setValue(saldoDB);
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void setPaymentMethod(String method){
        pembayaran.setValue(method);

        if(allDateChosen() && addrVM.getValue() != null){
            proceedable.setValue(true);
        }
    }

    public void makeOrder(){
        allItemAdded = false;
        paymentIdGenerated = false;
        detailOrderAdded = false;
        checkoutProcessLoading.setValue(true);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){

                    counterPJItemAdded = new ArrayList<>();
                    ArrayList<String> counterPJAdded = new ArrayList<>();
                    int status = 0;
                    if(pembayaran.getValue().contains("Saldo")){
                        status = 1;
                        paymentId = "";
                        paymentIdGenerated = true;
                    }
                    for (PJInput pj:
                         pjs) {
                        final int finalStatus = status;
                        orderDB.addPJ(pj, 1, status, accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(finalStatus == 1){
                                            akunDB.reduceSaldo(accountsGot.get(0).getEmail(), pj.getSubtotal().getValue());
                                        }
                                        addPJDetail(pj, documentSnapshot.getId());
                                        addOrderItem(pj.getemail_seller(), documentSnapshot.getId());
                                        counterPJAdded.add(documentSnapshot.getId());
                                        if(counterPJAdded.size() == pjs.size() && finalStatus == 0){
                                            addUnfinishedPayment(counterPJAdded, accountsGot.get(0).getEmail());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void addPJDetail(PJInput pjInput, String id_pj){
        orderDB.addDetailGrooming(pjInput.getTglGrooming().getValue(), id_pj, addrVM.getValue().getId(), pembayaran.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                detailOrderAdded = true;

                checkCheckoutDone();
            }
        });
    }

    private void addOrderItem(String seller, String id_pj){
        for (GroomingCheckoutItemVM checkoutItem:
             checkoutVM.getValue()) {
            if(checkoutItem.getSeller().equals(seller)){
                int harga = checkoutItem.getHarga().getValue();
                orderDB.addPJItem(checkoutItem.getJumlah(), id_pj, checkoutItem.getIdPaket(), checkoutItem.getName().getValue(), "", harga,
                        0, "", "").addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        counterPJItemAdded.add(0);
                        if(counterPJItemAdded.size() == checkoutVM.getValue().size()){
                            allItemAdded = true;
                            checkCheckoutDone();
                        }
                    }
                });
            }
        }
    }

    private void addUnfinishedPayment(ArrayList<String> ids, String hp_pembeli){
        String id_pjs = "";
        int total = 0;
        for (int i = 0; i < pjs.size(); i++) {
            if(id_pjs.equals("")){
                id_pjs += ids.get(i);
            }
            else{
                id_pjs += "|"+ids.get(i);
            }
            total += pjs.get(i).getSubtotal().getValue();
        }

        int finalTotal = total;
        orderDB.addUnfinishedPayment(id_pjs, total, pembayaran.getValue(), hp_pembeli).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String id_payment = documentSnapshot.getId();
                        paymentId = id_payment;
                        generateIpaymuPayment(id_payment, finalTotal);
                    }
                });
            }
        });
    }

    private void generateIpaymuPayment(String id_payment, int amount){
        if(pembayaran.getValue().contains("BCA")){
            orderDB.makePaymentBCATransfer(amount, id_payment).enqueue(new Callback<ResultTransferBank>() {
                @Override
                public void onResponse(Call<ResultTransferBank> call, Response<ResultTransferBank> response) {
                    ResultTransferBank result = response.body();
                    if(result != null){
                        orderDB.setUnfinishedPaymentDetail(id_payment, result.getData().getPaymentNo(), "", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                paymentIdGenerated = true;
                                checkCheckoutDone();
                            }
                        });
                    }
                    else{
                        cancelPayment(id_payment);
                        Toast.makeText(app, "Metode Pembayaran Tidak Tersedia untuk Saat Ini. Mohon pilih metode pembayaran lainnya", Toast.LENGTH_SHORT).show();
                        checkoutProcessLoading.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<ResultTransferBank> call, Throwable t) {

                }
            });
        }
        else if(pembayaran.getValue().contains("Gerai")){
            String channel = pembayaran.getValue().substring(6).toLowerCase();
            orderDB.getPaymentCstoreSessionID(amount, id_payment).enqueue(new Callback<ResultSidCstore>() {
                @Override
                public void onResponse(Call<ResultSidCstore> call, Response<ResultSidCstore> response) {
                    ResultSidCstore resultSid = response.body();
                    if(resultSid != null){
                        orderDB.makePaymentStore(amount, id_payment, channel, resultSid.getSessionID()).enqueue(new Callback<ResultCstore>() {
                            @Override
                            public void onResponse(Call<ResultCstore> call, Response<ResultCstore> response) {
                                ResultCstore result = response.body();
                                if(result != null){
                                    orderDB.setUnfinishedPaymentDetail(id_payment, result.getKode_pembayaran(), "", result.getKeterangan()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            paymentIdGenerated = true;
                                            checkCheckoutDone();
                                        }
                                    });
                                }
                                else{
                                    cancelPayment(id_payment);
                                    Toast.makeText(app, "Metode Pembayaran Tidak Tersedia untuk Saat Ini. Mohon pilih metode pembayaran lainnya", Toast.LENGTH_SHORT).show();
                                    checkoutProcessLoading.setValue(false);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultCstore> call, Throwable t) {

                            }
                        });
                    }
                    else{
                        cancelPayment(id_payment);
                        Toast.makeText(app, "Metode Pembayaran Tidak Tersedia untuk Saat Ini. Mohon pilih metode pembayaran lainnya", Toast.LENGTH_SHORT).show();
                        checkoutProcessLoading.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<ResultSidCstore> call, Throwable t) {
                }
            });
        }
        else if(pembayaran.getValue().contains("QRIS")){
            orderDB.makePaymentQRIS(amount, id_payment).enqueue(new Callback<ResultQRIS>() {
                @Override
                public void onResponse(Call<ResultQRIS> call, Response<ResultQRIS> response) {
                    ResultQRIS result = response.body();
                    if(result != null){
                        orderDB.setUnfinishedPaymentDetail(id_payment, "", result.getData().getQrImage(), "").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                paymentIdGenerated = true;
                                checkCheckoutDone();
                            }
                        });
                    }
                    else{
                        cancelPayment(id_payment);
                        Toast.makeText(app, "Metode Pembayaran Tidak Tersedia untuk Saat Ini. Mohon pilih metode pembayaran lainnya", Toast.LENGTH_SHORT).show();
                        checkoutProcessLoading.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<ResultQRIS> call, Throwable t) {

                }
            });
        }
        else{
            orderDB.makePaymentVA(amount, id_payment, pembayaran.getValue()).enqueue(new Callback<ResultVA>() {
                @Override
                public void onResponse(Call<ResultVA> call, Response<ResultVA> response) {
                    ResultVA result = response.body();
                    if(result != null){
                        orderDB.setUnfinishedPaymentDetail(id_payment, result.getVa(), "", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                paymentIdGenerated = true;
                                checkCheckoutDone();
                            }
                        });
                    }
                    else{
                        cancelPayment(id_payment);
                        Toast.makeText(app, "Metode Pembayaran Tidak Tersedia untuk Saat Ini. Mohon pilih metode pembayaran lainnya", Toast.LENGTH_SHORT).show();
                        checkoutProcessLoading.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<ResultVA> call, Throwable t) {

                }
            });
        }
    }

    private void checkCheckoutDone(){
        if(allItemAdded && paymentIdGenerated && detailOrderAdded && addrVM.getValue() != null){
            checkoutProcessLoading.setValue(false);
            checkoutDone.setValue(true);
        }
    }

    public void deleteCartsIncluded(String ids){
        String[] cart_ids = ids.split("\\|");
        for (String id:
             cart_ids) {
            cartDB.deleteCartItem(Integer.parseInt(id));
        }
    }

    public String getId_payment(){
        return paymentId;
    }

    public LiveData<AddressItemViewModel> getAddrVM(){
        return addrVM;
    }

    public LiveData<ArrayList<GroomingCheckoutItemVM>> getCheckoutVMs(){
        return checkoutVM;
    }

    public LiveData<Integer> getSaldo(){
        return saldo;
    }

    public LiveData<String> getPayment(){
        return pembayaran;
    }

    public LiveData<Boolean> isAddrLoading(){
        return addrLoading;
    }

    public LiveData<Boolean> isCheckoutLoading(){
        return checkoutLoading;
    }

    public LiveData<Boolean> canProceed(){
        return proceedable;
    }

    public LiveData<Boolean> isCheckoutProcessLoading(){
        return checkoutProcessLoading;
    }

    public LiveData<Boolean> isCheckoutDone(){
        return checkoutDone;
    }

    public LiveData<Integer> getTotal(){
        return total;
    }

    public void cancelPayment(String id_payment){
        //urutan delete
        //1. seluruh pesanan
        //2. pembayaran
        orderDB.getPayment(id_payment).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Pembayaran payment = new Pembayaran(documentSnapshot);

                    String[] id_pjs = payment.getId_pjs().split("\\|");
                    ArrayList<Integer> counterDeleted = new ArrayList<>();

                    for (int i = 0; i < id_pjs.length; i++) {
                        orderDB.getPJById(id_pjs[i]).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.getData() != null){
                                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                                    addItemQtyAgainAfterCancel(pj);

                                    orderDB.deletePJ(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            counterDeleted.add(0);
                                            if(counterDeleted.size() == id_pjs.length){
                                                //pj sdh didelete semua
                                                //delete payment
                                                orderDB.deletePayment(id_payment);
                                            }
                                        }
                                    });

                                    orderDB.deleteDetailPJ(pj.getId_pj(), pj.getJenis());
                                }

                            }
                        });

                    }
                }
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
}
