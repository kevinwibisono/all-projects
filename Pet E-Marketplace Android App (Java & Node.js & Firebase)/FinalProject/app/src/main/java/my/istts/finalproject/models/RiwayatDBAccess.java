package my.istts.finalproject.models;

import android.app.Application;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RiwayatDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();
    private AkunDBAccess akunDB;

    public RiwayatDBAccess(Application app){
        akunDB = new AkunDBAccess(app);
    }

    //hanya berlaku kepada SELLER
    public void addHistoryOrderSuccess(int jumlah, String email_penarik){
        akunDB.getAccByEmail(email_penarik).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null) {

                    Map<String, Object> riwayat = new HashMap<>();
                    riwayat.put("bukti_transfer","");
                    riwayat.put("no_rek", "");
                    riwayat.put("jenis_rek", 0);
                    riwayat.put("nama_rek", "");
                    riwayat.put("keterangan", "Hasil Penjualan");
                    riwayat.put("jenis", 0);
                    riwayat.put("jumlah", jumlah);
                    riwayat.put("email_penarik", email_penarik);
                    riwayat.put("nama", documentSnapshot.getString("nama"));
                    riwayat.put("tanggal", Timestamp.now());

                    firebaseDb.collection("riwayat_saldo")
                            .add(riwayat);

                    //berikan potongan
                    double potongan = (double) jumlah / 100;
                    Map<String, Object> charge = new HashMap<>();
                    charge.put("bukti_transfer","");
                    charge.put("no_rek", "");
                    charge.put("jenis_rek", 0);
                    charge.put("nama_rek", "");
                    charge.put("keterangan", "Potongan Aplikasi (1%)");
                    charge.put("jenis", 1);
                    charge.put("jumlah", potongan);
                    charge.put("email_penarik", email_penarik);
                    charge.put("nama", documentSnapshot.getString("nama"));
                    charge.put("tanggal", Timestamp.now());

                    firebaseDb.collection("riwayat_saldo")
                            .add(charge);

                    akunDB.addSaldo(email_penarik, (int) (jumlah - potongan));
                }
            }
        });
    }

    //hanya berlaku kepada BUYER
    public void addHistoryCancel(int jumlah, String email_penarik){
        akunDB.getAccByEmail(email_penarik).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null) {

                    Map<String, Object> riwayat = new HashMap<>();
                    riwayat.put("bukti_transfer","");
                    riwayat.put("no_rek", "");
                    riwayat.put("nama_rek", "");
                    riwayat.put("jenis_rek", 0);
                    riwayat.put("keterangan", "Pengembalian Saldo");
                    riwayat.put("jenis", 0);
                    riwayat.put("jumlah", jumlah);
                    riwayat.put("email_penarik", email_penarik);
                    riwayat.put("nama", documentSnapshot.getString("nama"));
                    riwayat.put("tanggal", Timestamp.now());

                    firebaseDb.collection("riwayat_saldo")
                            .add(riwayat);

                    akunDB.addSaldo(email_penarik, jumlah);
                }
            }
        });
    }

    public Task<QuerySnapshot> getHistories(int jenis, String email_penarik){
        Query query = firebaseDb.collection("riwayat_saldo")
                .whereEqualTo("email_penarik", email_penarik);

        if(jenis > -1){
            query = query.whereEqualTo("jenis", jenis);
        }

        return query
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .get();
    }

    public Task<DocumentReference> addPengajuanTarik(String no_rek, int jenis_rek, String nama_rek, int jumlah_tarik, String nama,
                                                     String email_penarik){
        Map<String, Object> riwayatReduce = new HashMap<>();
        riwayatReduce.put("bukti_transfer","");
        riwayatReduce.put("no_rek", "");
        riwayatReduce.put("jenis_rek", 0);
        riwayatReduce.put("nama_rek", "");
        riwayatReduce.put("keterangan", "Penarikan Saldo");
        riwayatReduce.put("jenis", 1);
        riwayatReduce.put("jumlah", jumlah_tarik);
        riwayatReduce.put("email_penarik", email_penarik);
        riwayatReduce.put("nama", nama);
        riwayatReduce.put("tanggal", Timestamp.now());

        firebaseDb.collection("riwayat_saldo")
                .add(riwayatReduce);

        Map<String, Object> riwayat = new HashMap<>();
        riwayat.put("bukti_transfer","");
        riwayat.put("no_rek", no_rek);
        riwayat.put("jenis_rek", jenis_rek);
        riwayat.put("nama_rek", nama_rek);
        riwayat.put("keterangan", "Pengajuan Penarikan");
        riwayat.put("jenis", 2);
        riwayat.put("jumlah", jumlah_tarik);
        riwayat.put("email_penarik", email_penarik);
        riwayat.put("nama", nama);
        riwayat.put("tanggal", Timestamp.now());

        return firebaseDb.collection("riwayat_saldo")
                .add(riwayat);

    }

}
