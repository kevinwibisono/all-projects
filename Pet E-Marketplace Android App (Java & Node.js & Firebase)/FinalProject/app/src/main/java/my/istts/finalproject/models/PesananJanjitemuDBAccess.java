package my.istts.finalproject.models;

import my.istts.finalproject.models.ipaymu.CstoreInput;
import my.istts.finalproject.models.ipaymu.PaymentInput;
import my.istts.finalproject.models.ipaymu.cstore.ResultCstore;
import my.istts.finalproject.models.ipaymu.cstore.ResultSidCstore;
import my.istts.finalproject.models.ipaymu.qris.ResultQRIS;
import my.istts.finalproject.models.ipaymu.transfer.ResultTransferBank;
import my.istts.finalproject.models.ipaymu.va.ResultVA;
import my.istts.finalproject.inputclasses.PJInput;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class PesananJanjitemuDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    private IpaymuService ipaymuservice = IpaymuRetrofitClient.getRetrofitInstance().create(IpaymuService.class);

    public Call<ResultTransferBank> makePaymentBCATransfer(int amount, String id_payment){
        PaymentInput newPayment = new PaymentInput("9A6FC584-43C3-45C7-891E-CB8A2E197362", amount, amount,
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment,
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment,
                "name", "08812121289", "email@email.com", "", "");
        Call<ResultTransferBank> call = ipaymuservice.makePaymentTransferBCA(newPayment);
        return call;
    }

    public Call<ResultQRIS> makePaymentQRIS(int amount, String id_payment){
        PaymentInput newPayment = new PaymentInput("9A6FC584-43C3-45C7-891E-CB8A2E197362", amount, amount,
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment,
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment,
                "name", "08812121289", "email@email.com", "", "");
        Call<ResultQRIS> call = ipaymuservice.makePaymentQRIS(newPayment);
        return call;
    }

    public Call<ResultSidCstore> getPaymentCstoreSessionID(int amount, String id_payment){
        CstoreInput newCstore = new CstoreInput("9A6FC584-43C3-45C7-891E-CB8A2E197362", 1, amount, "Paw Friends",
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment, "cstore");
        Call<ResultSidCstore> call = ipaymuservice.makeSidCstore(newCstore);
        return call;
    }

    public Call<ResultCstore> makePaymentStore(int amount, String id_payment, String channel, String sid){
        PaymentInput newPayment = new PaymentInput("9A6FC584-43C3-45C7-891E-CB8A2E197362", amount, amount,
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment,
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment,
                "name", "08812121289", "email@email.com", channel, sid);
        Call<ResultCstore> call = ipaymuservice.makePaymentCstore(newPayment);
        return call;
    }

    public Call<ResultVA> makePaymentVA(int amount, String id_payment, String vaMethod){
        PaymentInput newPayment = new PaymentInput("9A6FC584-43C3-45C7-891E-CB8A2E197362", amount, amount,
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment,
                "https://pawfriends-admin.herokuapp.com/handlePayment?id_payment="+id_payment,
                "name", "08812121289", "email@email.com", "", "");

        Call<ResultVA> call = ipaymuservice.makePaymentVAMandiri(newPayment);

        if(vaMethod.contains("AGI")){
            call = ipaymuservice.makePaymentVAAGI(newPayment);
        }
        else if(vaMethod.contains("BNI")){
            call = ipaymuservice.makePaymentVABNI(newPayment);
        }
        else if(vaMethod.contains("CIMB")){
            call = ipaymuservice.makePaymentVANiaga(newPayment);
        }
        else if(vaMethod.contains("Mandiri")){
            call = ipaymuservice.makePaymentVAMandiri(newPayment);
        }

        return call;
    }

    public Task<DocumentSnapshot> getPayment(String id_payment){
        return firebaseDb.collection("pembayaran")
                .document(id_payment)
                .get();
    }

    public Task<QuerySnapshot> getAllUnfinishedPayment(String email){
        return firebaseDb.collection("pembayaran")
                .whereEqualTo("email_pembeli", email)
                .get();
    }

    public Task<QuerySnapshot> getUnfinishedPayment(String email){
        Query query = firebaseDb.collection("pembayaran")
                .whereEqualTo("email_pembeli", email);

        return query
                .get();
    }

    public Task<QuerySnapshot> getWaitingOrder(String email){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_pembeli", email)
                .whereNotEqualTo("jenis", 0)
                .whereEqualTo("status", 2)
                .get();
    }

    public Task<QuerySnapshot> getCancelableOrders(String email){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_pembeli", email)
                .whereNotEqualTo("selesai_otomatis", null)
                .get();
    }

    public Task<QuerySnapshot> getActiveOrdersByType(int jenis, String email){
        int[] typeStatusPair = {7, 6, 4, 4};
        Query query = firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_pembeli", email)
                .whereEqualTo("jenis", jenis)
                .whereLessThan("status", typeStatusPair[jenis])
                .orderBy("status", Query.Direction.ASCENDING)
                .orderBy("tanggal", Query.Direction.ASCENDING);

        return query
                .get();
    }

    public Task<QuerySnapshot> getOrdersByTypeStatus(String email, int jenis, int status){
        Query query = firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_pembeli", email)
                .whereEqualTo("jenis", jenis);

        if(status > 0){
            query = query.whereEqualTo("status", status);
        }

        query = query.orderBy("tanggal", Query.Direction.DESCENDING);

        return query
                .get();
    }

    public Task<QuerySnapshot> getConnectedOrders(String email_pembeli, String email_penjual){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_pembeli", email_pembeli)
                .whereEqualTo("email_penjual", email_penjual)
                .orderBy("status", Query.Direction.ASCENDING)
                .get();
    }

    public Query getDetailGroomingSL(String id_pj){
        return firebaseDb.collection("detail_grooming")
                .whereEqualTo("id_pj", id_pj);
    }

    public Task<QuerySnapshot> getDetailPesanan(String id_pj){
        return firebaseDb.collection("detail_pesanan")
                .whereEqualTo("id_pj", id_pj)
                .get();
    }

    public Task<QuerySnapshot> getDetailGrooming(String id_pj){
        return firebaseDb.collection("detail_grooming")
                .whereEqualTo("id_pj", id_pj)
                .get();
    }

    public Task<QuerySnapshot> getDetailHotelBooking(String id_pj){
        return firebaseDb.collection("detail_booking")
                .whereEqualTo("id_pj", id_pj)
                .get();
    }

    public Task<QuerySnapshot> getDetailJanjiTemu(String id_pj){
        return firebaseDb.collection("detail_janjitemu")
                .whereEqualTo("id_pj", id_pj)
                .get();
    }

    public DocumentReference getPJByIdSL(String id_pj){
        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj);
    }

    public Task<DocumentSnapshot> getPJById(String id_pj){
        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .get();
    }

    public Task<DocumentSnapshot> getItemPJById(String id_item){
        return firebaseDb.collection("item_pj")
                .document(id_item)
                .get();
    }

    public Task<QuerySnapshot> getItemPJByPesanan(String id_pj){
        return firebaseDb.collection("item_pj")
                .whereEqualTo("id_pj", id_pj)
                .get();
    }

    private void deleteItemPJ(String id_item){
        firebaseDb.collection("item_pj")
                .document(id_item)
                .delete();
    }

    public Task<Void> deletePJ(String id_pj){
        firebaseDb.collection("item_pj")
                .whereEqualTo("id_pj", id_pj)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot itemDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    deleteItemPJ(itemDoc.getId());
                }
            }
        });

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .delete();
    }

    public void deleteDetailPJ(String id_pj, int jenis){
        if(jenis == 0){
            firebaseDb.collection("detail_pesanan")
                    .whereEqualTo("id_pj", id_pj)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        firebaseDb.collection("detail_pesanan")
                                .document(id)
                                .delete();
                    }
                }
            });
        }
        else if(jenis == 1){
            firebaseDb.collection("detail_booking")
                    .whereEqualTo("id_pj", id_pj)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        firebaseDb.collection("detail_pesanan")
                                .document(id)
                                .delete();
                    }
                }
            });
        }
        else if(jenis == 2){
            firebaseDb.collection("detail_pesanan")
                    .whereEqualTo("id_pj", id_pj)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        firebaseDb.collection("detail_pesanan")
                                .document(id)
                                .delete();
                    }
                }
            });
        }
    }


    public Task<Void> deletePayment(String id_payment){
        return firebaseDb.collection("pembayaran")
                .document(id_payment)
                .delete();
    }

    public Task<DocumentReference> addUnfinishedPayment(String id_pjs, int total, String metode, String email_pembeli){
        Map<String, Object> payment = new HashMap<>();
        payment.put("id_pjs", id_pjs);
        payment.put("email_pembeli", email_pembeli);
        payment.put("total_bayar", total);
        payment.put("metode", metode);
        payment.put("no_bayar", "");
        payment.put("gambar_qr", "");
        payment.put("bukti_transfer", "");
        payment.put("keterangan", "");
        payment.put("selesai_otomatis", new Timestamp(addDays(Timestamp.now(), 1)));

        return firebaseDb.collection("pembayaran").add(payment);
    }

    public Task<Void> setUnfinishedPaymentDetail(String id_payment, String no_bayar, String qr, String keterangan){
        Map<String, Object> payment = new HashMap<>();
        payment.put("no_bayar", no_bayar);
        payment.put("qr", qr);
        payment.put("keterangan", keterangan);

        return firebaseDb.collection("pembayaran")
                .document(id_payment)
                .set(payment, SetOptions.merge());
    }

    public Task<DocumentReference> addPJ(PJInput pjInput, int jenis, int status, String email_pembeli){
        Map<String, Object> pj = new HashMap<>();
        pj.put("email_penjual", pjInput.getemail_seller());
        pj.put("email_pembeli", email_pembeli);
        pj.put("tanggal", Timestamp.now());
        pj.put("selesai_otomatis", null);
        pj.put("status", status);
        pj.put("jenis", jenis);
        pj.put("invoice", Timestamp.now().getSeconds());
        pj.put("total", pjInput.getSubtotal().getValue());
        pj.put("alasan_batal", "");
        pj.put("ulasan", false);

        return firebaseDb.collection("pesanan_janjitemu").add(pj);
    }

    public Task<DocumentReference> addJanjiTemuKlinik(String email_pembeli, String email_klinik){
        Map<String, Object> pj = new HashMap<>();
        pj.put("email_penjual", email_klinik);
        pj.put("email_pembeli", email_pembeli);
        pj.put("tanggal", Timestamp.now());
        pj.put("selesai_otomatis", new Timestamp(addDays(Timestamp.now(), 1)));
        pj.put("status", 1);
        pj.put("jenis", 3);
        pj.put("invoice", 0);
        pj.put("total", 0);
        pj.put("alasan_batal", "");
        pj.put("ulasan", false);

        return firebaseDb.collection("pesanan_janjitemu").add(pj);
    }

    public Task<DocumentReference> addDetailPesanan(PJInput pjInput, String id_pj, String alamat, String koordinat, String metode_bayar){
        Map<String, Object> detail = new HashMap<>();
        detail.put("id_pj", id_pj);
        detail.put("alamat", alamat);
        detail.put("koordinat", koordinat);
        detail.put("kurir", pjInput.getCourier().getValue());
        detail.put("paket_kurir", pjInput.getCourierDetail().getValue());
        detail.put("no_resi", "");
        detail.put("catatan", pjInput.getCatatan().getValue());
        detail.put("ongkir", pjInput.getOngkir().getValue());
        detail.put("metode_bayar", metode_bayar);

        return firebaseDb.collection("detail_pesanan").add(detail);
    }

    public Task<DocumentReference> addDetailGrooming(Date tglBooking, String id_pj, String id_alamat, String metode_bayar){
        Map<String, Object> detail = new HashMap<>();
        detail.put("id_pj", id_pj);
        detail.put("id_alamat", id_alamat);
        detail.put("tgl_booking", new Timestamp(tglBooking));
        detail.put("posisi_groomer", "0,0");
        detail.put("metode_bayar", metode_bayar);

        return firebaseDb.collection("detail_grooming").add(detail);
    }

    public Task<DocumentReference> addDetailHotelBooking(String id_pj, String metode_bayar, Date tglAwal, Date tglAkhir){
        Map<String, Object> detail = new HashMap<>();
        detail.put("id_pj", id_pj);
        detail.put("tgl_masuk", new Timestamp(tglAwal));
        detail.put("tgl_keluar", new Timestamp(tglAkhir));
        detail.put("metode_bayar", metode_bayar);

        return firebaseDb.collection("detail_booking").add(detail);
    }

    public Task<DocumentReference> addDetailJanjiTemu(String id_pj, Date tgl_janji, String alamat, String koordinat, String jenis_janji,
                                                      String nama_hewan, String jenis_hewan, String usia_hewan, String keluhan,
                                                      String ownerName, String ownerHP){
        Map<String, Object> detail = new HashMap<>();
        detail.put("id_pj", id_pj);
        detail.put("tgl_janjitemu", new Timestamp(tgl_janji));
        detail.put("jenis_janjitemu", jenis_janji);
        detail.put("alamat", alamat);
        detail.put("koordinat", koordinat);
        detail.put("nama_pemilik", ownerName);
        detail.put("hp_pemilik", ownerHP);
        detail.put("daftar_nama", nama_hewan);
        detail.put("daftar_usia", usia_hewan);
        detail.put("daftar_jenis", jenis_hewan);
        detail.put("keluhan", keluhan);

        return firebaseDb.collection("detail_janjitemu").add(detail);
    }

    public Task<DocumentReference> addPJItem(int jumlah, String id_pj, String id_item, String nama, String gambar, int harga, int totalBerat,
                                             String id_variasi, String variasi){
        Map<String, Object> itemPJ = new HashMap<>();
        itemPJ.put("jumlah", jumlah);
        itemPJ.put("id_pj", id_pj);
        itemPJ.put("id_item", id_item);
        itemPJ.put("nama", nama);
        itemPJ.put("gambar", gambar);
        itemPJ.put("harga", harga);
        itemPJ.put("total_berat", totalBerat);
        itemPJ.put("id_variasi", id_variasi);
        itemPJ.put("variasi", variasi);

        return firebaseDb.collection("item_pj").add(itemPJ);
    }

    public void activateOrder(int status, String id_pj, Timestamp lastDate){
        Map<String, Object> itemPJ = new HashMap<>();
        itemPJ.put("status", status);
        itemPJ.put("selesai_otomatis", new Timestamp(addDays(lastDate, 2)));

        firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(itemPJ, SetOptions.merge());
    }

    public Task<Void> cancelOrder(int status, String id_pj, String alasan){
        Map<String, Object> itemPJ = new HashMap<>();
        itemPJ.put("status", status);
        itemPJ.put("selesai_otomatis", null);
        itemPJ.put("alasan_batal", alasan);

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(itemPJ, SetOptions.merge());
    }

    public Task<Void> finishOrder(int status, String id_pj){
        Map<String, Object> itemPJ = new HashMap<>();
        itemPJ.put("status", status);
        itemPJ.put("selesai_otomatis", null);

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(itemPJ, SetOptions.merge());
    }

    public Task<Void> complainOrder(String id_pj, Timestamp lastDate){
        Map<String, Object> itemPJ = new HashMap<>();
        itemPJ.put("status", 6);
        itemPJ.put("selesai_otomatis", new Timestamp(addDays(lastDate, 2)));

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(itemPJ, SetOptions.merge());
    }

    public Task<Void> giveReview(String id_pj, int jenis_pj){
        if(jenis_pj % 2 == 0){
            getItemPJByPesanan(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot itemPJDoc:
                         queryDocumentSnapshots.getDocuments()) {
                        ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(itemPJDoc);
                        if(jenis_pj == 0){
                            ProductDBAccess productDB = new ProductDBAccess();
                            productDB.incProductReview(itemPJ.getId_item(), 1);
                        }
                        else{
                            HotelDBAccess hotelDB = new HotelDBAccess();
                            hotelDB.incHotelReview(itemPJ.getId_item(), 1);
                        }
                    }
                }
            });
        }
        Map<String, Object> itemPJ = new HashMap<>();
        itemPJ.put("ulasan", true);

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(itemPJ, SetOptions.merge());
    }

    private Date addDays(Timestamp timestamp, int hari){
        Date date = timestamp.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, hari);
        return calendar.getTime();
    }
}
