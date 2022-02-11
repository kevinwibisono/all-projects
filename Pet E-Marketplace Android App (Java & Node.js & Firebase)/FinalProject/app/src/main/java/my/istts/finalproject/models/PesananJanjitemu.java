package my.istts.finalproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class PesananJanjitemu {
    private String id_pj;
    private String email_pembeli;
    private String email_penjual;
    private int jenis;
    private int status;
    private String statusStr;
    private int total;
    private Timestamp tanggal;
    private String tanggalStr;
    private String alasan_batal;
    private Timestamp selesai_otomatis;
    private String selesai_otomatisStr;
    private boolean ulasan;
    private boolean finishable;

    public PesananJanjitemu(DocumentSnapshot doc) {
        this.id_pj = doc.getId();
        this.email_pembeli = doc.getString("email_pembeli");
        this.email_penjual = doc.getString("email_penjual");
        this.alasan_batal = doc.getString("alasan_batal");
        this.jenis = doc.getLong("jenis").intValue();
        this.status = doc.getLong("status").intValue();
        this.total = doc.getLong("total").intValue();
        this.ulasan = doc.getBoolean("ulasan");
        this.finishable = false;
        setStatus(this.jenis, this.status);

        this.tanggal = doc.getTimestamp("tanggal");
        Date pjDate = doc.getTimestamp("tanggal").toDate();
        this.tanggalStr = getStringDate(pjDate);

        this.selesai_otomatis = doc.getTimestamp("selesai_otomatis");
        if(selesai_otomatis != null){
            Date selesaiDate = doc.getTimestamp("selesai_otomatis").toDate();
            this.selesai_otomatisStr = getStringDate(selesaiDate);
        }
        else{
            this.selesai_otomatisStr = "";
        }
    }

    private String getStringDate(Date date){
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        return date.getDate()+" "+months[date.getMonth()]+" "+(date.getYear()+1900)+
                " "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

    public String getId_pj() {
        return id_pj;
    }

    public String getemail_pembeli() {
        return email_pembeli;
    }

    public String getemail_penjual() {
        return email_penjual;
    }

    public int getJenis() {
        return jenis;
    }

    public int getStatus() {
        return status;
    }

    public int getTotal() {
        return total;
    }

    public Timestamp gettanggal() {
        return tanggal;
    }

    public Timestamp getSelesai_otomatis() {
        return selesai_otomatis;
    }

    public String gettanggalStr() {
        return tanggalStr;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public String getAlasan_batal() {
        return alasan_batal;
    }

    public String getSelesai_otomatisStr() {
        return selesai_otomatisStr;
    }

    private void setStatus(int jenis, int status){
        String[] statuses;
        if(jenis == 0){
            statuses = new String[]{"Menunggu Pembayaran", "Menunggu Konfirmasi", "Diproses Penjual", "Pesanan Disiapkan", "Dalam Pengiriman", "Siap Untuk Pickup", "Dikomplain", "Selesai", "Dibatalkan"};
        }
        else if(jenis == 1){
            statuses = new String[]{"Menunggu Pembayaran", "Menunggu Konfirmasi", "Menunggu Jadwal Grooming", "Jadwal Grooming Aktif", "Groomer Dalam Perjalanan", "Proses Grooming", "Selesai", "Dibatalkan"};
        }
        else if(jenis == 2){
            statuses = new String[]{"Menunggu Pembayaran", "Menunggu Konfirmasi", "Menunggu Jadwal Booking", "Dalam Penginapan", "Selesai", "Dibatalkan"};
        }
        else {
            statuses = new String[]{"Menunggu Pembayaran", "Menunggu Konfirmasi", "Menunggu Jadwal Janjitemu", "Jadwal Janjitemu Aktif", "Selesai", "Dibatalkan"};
        }
        this.statusStr = statuses[status];

        setFinishable(statusStr);
    }

    private void setFinishable(String statusStr){
        String[] finishableStatus = {"Dalam Pengiriman", "Siap Untuk Pickup", "Dikomplain", "Proses Grooming", "Dalam Penginapan", "Jadwal Janjitemu Aktif"};

        boolean finishable = false;
        for (int i = 0; i < finishableStatus.length; i++) {
            if(finishableStatus[i].equals(statusStr)){
                finishable = true;
            }
        }
        this.finishable = finishable;
    }

    public boolean isFinishable() {
        return finishable;
    }

    public boolean isReviewGiven(){
        return ulasan;
    }

}
