package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Alamat;
import my.istts.finalproject.models.AlamatDBAccess;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.DetailPenjual;
import my.istts.finalproject.models.DetailPenjualDBAccess;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.Pembayaran;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Promo;
import my.istts.finalproject.models.PromoDBAccess;
import my.istts.finalproject.models.RajaOngkirRetrofitClient;
import my.istts.finalproject.models.RajaOngkirService;
import my.istts.finalproject.models.ipaymu.cstore.ResultCstore;
import my.istts.finalproject.models.ipaymu.cstore.ResultSidCstore;
import my.istts.finalproject.models.ipaymu.qris.ResultQRIS;
import my.istts.finalproject.models.ipaymu.transfer.ResultTransferBank;
import my.istts.finalproject.models.ipaymu.va.ResultVA;
import my.istts.finalproject.models.rajaongkirapi.PaketKurir;
import my.istts.finalproject.models.rajaongkirapi.RajaOngkir;
import my.istts.finalproject.models.rajaongkirapi.RajaOngkirInput;
import my.istts.finalproject.models.rajaongkirapi.RajaOngkirResult;
import my.istts.finalproject.inputclasses.PJInput;
import my.istts.finalproject.viewmodels.itemviewmodels.AddressItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.OngkirItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductCheckoutItemVM;
import my.istts.finalproject.viewmodels.itemviewmodels.PromoItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCheckoutViewModel {
    private DetailPenjualDBAccess detailDB;
    private AlamatDBAccess alamatDB;
    private PromoDBAccess promoDB;
    private CartDBAccess cartDB;
    private AkunDBAccess akunDB;
    private PesananJanjitemuDBAccess orderDB;
    private ProductDBAccess productDB;
    private RajaOngkirService rajaOngkirService;
    private Application app;

    public ProductCheckoutViewModel(Application app) {
        this.app = app;
        this.detailDB = new DetailPenjualDBAccess();
        this.alamatDB = new AlamatDBAccess();
        this.promoDB = new PromoDBAccess();
        this.cartDB = new CartDBAccess(app);
        this.akunDB = new AkunDBAccess(app);
        this.orderDB = new PesananJanjitemuDBAccess();
        this.productDB = new ProductDBAccess();
        rajaOngkirService = RajaOngkirRetrofitClient.getRetrofitInstance().create(RajaOngkirService.class);
    }

    private String paymentId;
    private boolean allItemAdded, paymentIdGenerated, detailOrderAdded;
    private ArrayList<PJInput> pjs = new ArrayList<>();
    private ArrayList<Integer> counterPJItemAdded = new ArrayList<>();
    private PJInput currentPJ;
    private MutableLiveData<ArrayList<OngkirItemViewModel>> ongkirVMs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<PromoItemViewModel>> promoVMs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProductCheckoutItemVM>> checkoutVM = new MutableLiveData<>();
    private MutableLiveData<AddressItemViewModel> addrVM = new MutableLiveData<>();
    private MutableLiveData<Boolean> addrLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkoutLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> promoLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> ongkirLoading = new MutableLiveData<>();
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
                            if(allCourierSelected() && !pembayaran.getValue().equals("")){
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
                if(allCourierSelected() && !pembayaran.getValue().equals("")){
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
                    boolean last = false;
                    if(counters.size() == cartsIds.length){
                        last = true;
                    }
                    addProductCheckoutToVM(items.get(0), getPJForCart(items.get(0).getEmail_seller()), last);
                }
            }
        });
        for (String cartsId : cartsIds) {
            cartDB.getCartWithId(Integer.parseInt(cartsId));
        }
    }

    private PJInput getPJForCart(String email_seller){
        PJInput fittedPJ = null;
        for (PJInput pj:
             pjs) {
            if(pj.getemail_seller().equals(email_seller)){
                fittedPJ = pj;
            }
        }
        return fittedPJ;
    }

    private void addProductCheckoutToVM(Cart cart, PJInput pjInput, boolean last){
        ArrayList<ProductCheckoutItemVM> vmsNow = checkoutVM.getValue();
        vmsNow.add(new ProductCheckoutItemVM(cart, pjInput, false, false));
        checkoutVM.setValue(vmsNow);
        if(last){
            setCheckoutItemsShowSellerOngkir();
        }
    }

    private void setCheckoutItemsShowSellerOngkir(){
        ArrayList<ProductCheckoutItemVM> vmsNow = checkoutVM.getValue();
        String lastSeller = "";
        for (int i = 0; i < vmsNow.size(); i++) {
            ProductCheckoutItemVM checkout = vmsNow.get(i);
            if(!checkout.getSeller().equals(lastSeller)){
                checkout.setShowSeller(true);
                lastSeller = checkout.getSeller();
            }
            if(i < vmsNow.size() -1){
                if(!checkout.getSeller().equals(vmsNow.get(i+1).getSeller())){
                    //jika checkout item berikutnya tdk sama sellernya, berarti ini item terakhir dari seller ini
                    //munculkan opsi ongkir, opsi promo dan subtotal
                    checkout.setShowOngkir(true);
                }
            }
            else{
                checkout.setShowOngkir(true);
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

    public void getSellerPromos(String email_seller){
        for (PJInput pj:
             pjs) {
            if(pj.getemail_seller().equals(email_seller)){
                currentPJ = pj;
            }
        }

        promoLoading.setValue(true);
        promoVMs.setValue(new ArrayList<>());
        promoDB.getShopPromos(email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Promo> promos = new ArrayList<>();
                    for (DocumentSnapshot doc:
                         queryDocumentSnapshots.getDocuments()) {
                        promos.add(new Promo(doc));
                    }
                    addPromoToVM(promos, currentPJ, checkoutVM.getValue());
                }
                else{
                    promoLoading.setValue(false);
                }
            }
        });
    }

    private void addPromoToVM(ArrayList<Promo> promos, PJInput pj, ArrayList<ProductCheckoutItemVM> checkoutItemVMS){
        ArrayList<PromoItemViewModel> currentVMs = promoVMs.getValue();
        for (Promo promo:
             promos) {
            currentVMs.add(new PromoItemViewModel(promo, pj, checkoutItemVMS));
        }
        promoVMs.setValue(currentVMs);
        promoLoading.setValue(false);
    }

    public void choosePromo(int position){
        ArrayList<PromoItemViewModel> currentVMs = promoVMs.getValue();
        if(position < currentVMs.size() && position >= 0){
            PromoItemViewModel chosenPromo = currentVMs.get(position);
            if(currentPJ != null){
                if(chosenPromo.getIdProduk().equals("")){
                    changeAllProductCheckoutsDiscount(chosenPromo);
                }
                else{
                    changeIncludedProductCheckoutsDiscount(chosenPromo);
                }
                recountTotal();
            }
        }
    }

    private void changeAllProductCheckoutsDiscount(PromoItemViewModel chosenPromo){
        for (ProductCheckoutItemVM checkout:
                checkoutVM.getValue()) {
            if(checkout.getSeller().equals(currentPJ.getemail_seller())){
                int hargaAsli = checkout.getHarga().getValue();
                double percent = chosenPromo.getPercent();
                double diskon = percent / 100;
                int jumlahDiskon = (int) (diskon * hargaAsli);

                if(jumlahDiskon > chosenPromo.getMax()){
                    jumlahDiskon = chosenPromo.getMax();
                }

                checkout.setDiscountValue(hargaAsli - jumlahDiskon);
            }
        }
        currentPJ.setPromoName(chosenPromo.getPromoName());
    }

    private void changeIncludedProductCheckoutsDiscount(PromoItemViewModel chosenPromo){
        for (ProductCheckoutItemVM checkout:
                checkoutVM.getValue()) {
            if(checkout.getSeller().equals(currentPJ.getemail_seller()) && chosenPromo.getIdProduk().contains(checkout.getIdProduct())){
                int hargaAsli = checkout.getHarga().getValue();
                double percent = chosenPromo.getPercent();
                double diskon = percent / 100;

                checkout.setDiscountValue((int) (hargaAsli - (diskon * hargaAsli)));
            }
        }
        currentPJ.setPromoName(chosenPromo.getPromoName());
    }

    public void cancelProductCheckoutsPromo(String email_seller){
        for (PJInput pj:
             pjs) {
            if(pj.getemail_seller().equals(email_seller)){
                currentPJ = pj;
            }
        }

        for (ProductCheckoutItemVM checkout:
             checkoutVM.getValue()) {
            if(checkout.getSeller().equals(email_seller)){
                checkout.setDiscountValue(0);
            }
        }
        currentPJ.setPromoName("");
    }

    public void getOngkirOptions(String seller){
        for (PJInput pj:
                pjs) {
            if(pj.getemail_seller().equals(seller)){
                currentPJ = pj;
            }
        }

        ongkirLoading.setValue(true);
        ongkirVMs.setValue(new ArrayList<>());
        detailDB.getDBAkunDetail(seller).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    DetailPenjual detailPenjual = new DetailPenjual(documentSnapshot);
                    String[] couriers = {"pickup", "jne", "pos", "tiki"};
                    String[] couriersPenjual = detailPenjual.getKurir().split("\\|");
                    for (int i = 0; i < 4; i++) {
                        boolean courierIncluded = Boolean.parseBoolean(couriersPenjual[i]);
                        if(i == 0){
                            if(courierIncluded){
                                addOngkirToVM(null, couriers[i]);
                            }
                        }
                        else{
                            final int index = i;
                            if(totalBerat(seller) > 25000){
                                ongkirLoading.setValue(false);
                                Toast.makeText(app, "Ongkir Tidak Didukung untuk Berat Diatas 25kg", Toast.LENGTH_LONG).show();
                                break;
                            }
                            else{
                                rajaOngkirService.getOngkir("73e2c36dbe7dd2f1da82882a0e2f7b3d",
                                        new RajaOngkirInput("444", "444", totalBerat(seller), couriers[i])).enqueue(new Callback<RajaOngkirResult>() {
                                    @Override
                                    public void onResponse(Call<RajaOngkirResult> call, Response<RajaOngkirResult> response) {
                                        RajaOngkir rajaOngkir = response.body().getRajaongkir();
                                        if(courierIncluded){
                                            for (PaketKurir paket:
                                                    rajaOngkir.getResults().get(0).getCosts()) {
                                                addOngkirToVM(paket, couriers[index]);
                                            }
                                        }
                                        if(index == 3){
                                            ongkirLoading.setValue(false);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<RajaOngkirResult> call, Throwable t) {
                                        Toast.makeText(app, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    }
                }
            }
        });
    }

    private int totalBerat(String seller){
        int total = 0;
        for (ProductCheckoutItemVM checkoutItem:
             checkoutVM.getValue()) {
            if(checkoutItem.getSeller().equals(seller)){
                total += checkoutItem.getBerat().getValue();
            }
        }
        return total;
    }

    private void addOngkirToVM(PaketKurir paketKurir, String kurirName){
        ArrayList<OngkirItemViewModel> currentVMs = ongkirVMs.getValue();
        currentVMs.add(new OngkirItemViewModel(paketKurir, kurirName));
        ongkirVMs.setValue(currentVMs);
    }

    public void chooseOngkir(int position){
        ArrayList<OngkirItemViewModel> currentVMs = ongkirVMs.getValue();
        if(position < currentVMs.size() && position >= 0){
            OngkirItemViewModel chosenOngkir = currentVMs.get(position);
            if(currentPJ != null){
                currentPJ.setOngkir(chosenOngkir);
                recountTotal();
            }
        }

        if(allCourierSelected() && !pembayaran.getValue().equals("") && addrVM.getValue() != null){
            proceedable.setValue(true);
        }
    }

    private boolean allCourierSelected(){
        boolean allSelected = true;
        for (PJInput pj:
                pjs) {
            if(pj.getCourierDetail().getValue().equals("")){
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

        if(allCourierSelected() && addrVM.getValue() != null){
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
                    String email = accountsGot.get(0).getEmail();

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
                        orderDB.addPJ(pj, 0, status, email).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(finalStatus == 1){
                                            akunDB.reduceSaldo(email, pj.getSubtotal().getValue());
                                        }
                                        addPJDetail(pj, documentSnapshot.getId());
                                        addOrderItem(pj.getemail_seller(), documentSnapshot.getId());
                                        counterPJAdded.add(documentSnapshot.getId());
                                        if(counterPJAdded.size() == pjs.size() && finalStatus == 0){
                                            addUnfinishedPayment(counterPJAdded, email);
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
        if(pjInput.getCourier().getValue().equals("pickup")){
            detailDB.getDBAkunDetail(pjInput.getemail_seller()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        DetailPenjual seller = new DetailPenjual(documentSnapshot);

                        orderDB.addDetailPesanan(pjInput, id_pj, seller.getAlamat(), seller.getKoordinat(), pembayaran.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                detailOrderAdded = true;

                                checkCheckoutDone();
                            }
                        });
                    }
                }
            });
        }
        else{
            orderDB.addDetailPesanan(pjInput, id_pj, addrVM.getValue().getAddressDetail(), addrVM.getValue().getCoordinate(), pembayaran.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    detailOrderAdded = true;

                    checkCheckoutDone();
                }
            });
        }

    }

    private void addOrderItem(String seller, String id_pj){
        for (ProductCheckoutItemVM checkoutItem:
             checkoutVM.getValue()) {
            if(checkoutItem.getSeller().equals(seller)){
                int harga = checkoutItem.getHarga().getValue();
                if(checkoutItem.getDiscountValue().getValue() > 0){
                    harga = checkoutItem.getDiscountValue().getValue();
                }
                orderDB.addPJItem(checkoutItem.getJumlah(), id_pj, checkoutItem.getIdProduct(), checkoutItem.getName().getValue(), checkoutItem.getPicture().getValue(), harga,
                        checkoutItem.getBerat().getValue(), checkoutItem.getIdVariasi().getValue(), checkoutItem.getVariasi().getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    private void addUnfinishedPayment(ArrayList<String> ids, String email_pembeli){
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
        orderDB.addUnfinishedPayment(id_pjs, total, pembayaran.getValue(), email_pembeli).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
            makePaymentBCA(id_payment, amount);
        }
        else if(pembayaran.getValue().contains("Gerai")){
            makePaymentCstore(id_payment, amount);
        }
        else if(pembayaran.getValue().contains("QRIS")){
            makePaymentQRIS(id_payment, amount);
        }
        else{
            makePaymentVA(id_payment, amount);
        }
    }

    private void makePaymentBCA(String id_payment, int amount){
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

    private void makePaymentCstore(String id_payment, int amount){
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

    private void makePaymentQRIS(String id_payment, int amount){
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

    private void makePaymentVA(String id_payment, int amount){
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

    private void checkCheckoutDone(){
        if(allItemAdded && paymentIdGenerated && detailOrderAdded){
            checkoutProcessLoading.setValue(false);
            checkoutDone.setValue(true);
        }
    }

    public void deleteCartsIncluded(String ids){
        String[] cart_ids = ids.split("\\|");
        for (String id:
             cart_ids) {
            cartDB.setItemsGotListener(new CartDBAccess.onCartItemsGot() {
                @Override
                public void onGotItems(List<Cart> items) {
                    if(items.size() > 0){
                        Cart cartItem = items.get(0);
                        if(cartItem.getId_variasi().equals("")){
                            productDB.reduceProductQtyAfterBuy(cartItem.getId_item(), cartItem.getJumlah());
                        }
                        else{
                            productDB.reduceVarQtyAfterBuy(cartItem.getId_item(), cartItem.getId_variasi(), cartItem.getJumlah());
                        }
                    }
                }
            });

            cartDB.getCartWithId(Integer.parseInt(id));

            cartDB.deleteCartItem(Integer.parseInt(id));
        }
    }

    public String getPaymentId(){
        return paymentId;
    }

    public LiveData<AddressItemViewModel> getAddrVM(){
        return addrVM;
    }

    public LiveData<ArrayList<OngkirItemViewModel>> getOngkirVMs(){
        return ongkirVMs;
    }

    public LiveData<ArrayList<ProductCheckoutItemVM>> getCheckoutVMs(){
        return checkoutVM;
    }

    public LiveData<ArrayList<PromoItemViewModel>> getPromoVMs(){
        return promoVMs;
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

    public LiveData<Boolean> isPromoLoading(){
        return promoLoading;
    }

    public LiveData<Boolean> isOngkirLoading(){
        return ongkirLoading;
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
