package com.example.sellerapp.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PesananJanjitemuDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<QuerySnapshot> getOrdersHomePage(String email){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_penjual", email)
                .whereIn("status", Arrays.asList(1,2,3))
                .get();
    }

    public Task<QuerySnapshot> getOrdersByStatus(String email, int status){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_penjual", email)
                .whereEqualTo("status", status)
                .get();
    }

    public Task<QuerySnapshot> getActiveGrooming(String email){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_penjual", email)
                .whereEqualTo("jenis", 1)
                .whereLessThan("status", 6)
                .orderBy("status", Query.Direction.ASCENDING)
                .orderBy("tanggal", Query.Direction.ASCENDING)
                .get();
    }

    public Task<QuerySnapshot> getActiveAppos(String email){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_penjual", email)
                .whereEqualTo("jenis", 3)
                .whereLessThan("status", 4)
                .orderBy("status", Query.Direction.ASCENDING)
                .orderBy("tanggal", Query.Direction.ASCENDING)
                .get();
    }

    public Task<QuerySnapshot> getComplainedOrders(String email){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_penjual", email)
                .whereEqualTo("jenis", 0)
                .whereEqualTo("status", 6)
                .orderBy("tanggal", Query.Direction.ASCENDING)
                .get();
    }

    public Task<QuerySnapshot> getAllOrders(String email){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_penjual", email)
                .orderBy("status", Query.Direction.ASCENDING)
                .orderBy("tanggal", Query.Direction.ASCENDING)
                .get();
    }

    public Task<QuerySnapshot> getConnectedOrders(String email_pembeli, String email_penjual){
        return firebaseDb.collection("pesanan_janjitemu")
                .whereEqualTo("email_pembeli", email_pembeli)
                .whereEqualTo("email_penjual", email_penjual)
                .orderBy("status", Query.Direction.ASCENDING)
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

    public Task<Void> acceptOrder(String id_pj, Timestamp autoBatal){
        Map<String, Object> pj = new HashMap<>();
        pj.put("status", 2);
        pj.put("selesai_otomatis", new Timestamp(addDays(autoBatal, 2)));

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(pj, SetOptions.merge());
    }

    public Task<Void> acceptOrderBookingAppo(String id_pj){
        Map<String, Object> pj = new HashMap<>();
        pj.put("status", 2);
        pj.put("selesai_otomatis", null);

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(pj, SetOptions.merge());
    }

    public Task<Void> acceptOrderPickup(String id_pj, Timestamp autoBatal){
        Map<String, Object> pj = new HashMap<>();
        pj.put("status", 3);
        pj.put("selesai_otomatis", new Timestamp(addDays(autoBatal, 1)));

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(pj, SetOptions.merge());
    }

    public Task<Void> setNoResi(String no_resi, String id_detail){
        Map<String, Object> detail = new HashMap<>();
        detail.put("no_resi", no_resi);

        return firebaseDb.collection("detail_pesanan")
                .document(id_detail)
                .set(detail, SetOptions.merge());
    }

    public Task<Void> deliverOrder(String id_pj, Timestamp autoBatal){
        Map<String, Object> pj = new HashMap<>();
        pj.put("status", 4);
        pj.put("selesai_otomatis", new Timestamp(addDays(autoBatal, 7)));

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(pj, SetOptions.merge());
    }

    public Task<Void> readyForPickup(String id_pj, Timestamp autoBatal){
        Map<String, Object> pj = new HashMap<>();
        pj.put("status", 5);
        pj.put("selesai_otomatis", new Timestamp(addDays(autoBatal, 1)));

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(pj, SetOptions.merge());
    }

    public Task<Void> finishShopOrder(String id_pj){
        Map<String, Object> itemPJ = new HashMap<>();
        itemPJ.put("status", 7);
        itemPJ.put("selesai_otomatis", null);

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(itemPJ, SetOptions.merge());
    }

    public Task<Void> groomerOtw(String id_pj, double lat, double lng){
        firebaseDb.collection("detail_grooming")
                .whereEqualTo("id_pj", id_pj)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    firebaseDb.collection("detail_grooming")
                            .document(queryDocumentSnapshots.getDocuments().get(0).getId())
                            .update("posisi_groomer", lat+","+lng);
                }
            }
        });

        Map<String, Object> pj = new HashMap<>();
        pj.put("status", 4);

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(pj, SetOptions.merge());
    }

    public void updateGroomerPos(String id_detail, double lat, double lng){
        firebaseDb.collection("detail_grooming")
                .document(id_detail)
                .update("posisi_groomer", lat+","+lng);
    }

    public Task<Void> groomerArrive(String id_pj){
        Map<String, Object> pj = new HashMap<>();
        pj.put("status", 5);

        return firebaseDb.collection("pesanan_janjitemu")
                .document(id_pj)
                .set(pj, SetOptions.merge());
    }

    private Date addDays(Timestamp timestamp, int hari){
        Date date = timestamp.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, hari);
        return calendar.getTime();
    }

}
