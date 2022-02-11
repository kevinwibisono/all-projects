package my.istts.finalproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Pembayaran {
    private String id_payment;
    private String id_pjs;
    private String email_pembeli;
    private int total_bayar;
    private String metode;
    private String no_bayar;
    private String gambar_qr;
    private Timestamp selesai_otomatis;
    private String selesaiStr;
    private String keterangan;

    public Pembayaran(DocumentSnapshot doc){
        this.id_payment = doc.getId();
        this.id_pjs = doc.getString("id_pjs");
        this.email_pembeli = doc.getString("email_pembeli");
        this.total_bayar = doc.getLong("total_bayar").intValue();
        this.metode = doc.getString("metode");
        this.no_bayar = doc.getString("no_bayar");
        this.gambar_qr = doc.getString("qr");
        this.keterangan = doc.getString("keterangan");
        this.selesai_otomatis = doc.getTimestamp("selesai_otomatis");
        Date selesaiDate = doc.getTimestamp("selesai_otomatis").toDate();
        setStringDate(selesaiDate);
    }

    private void setStringDate(Date date){
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        this.selesaiStr = date.getDate()+" "+months[date.getMonth()]+" "+(date.getYear()+1900)+
                " "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

    public String getId_payment() {
        return id_payment;
    }

    public String getId_pjs() {
        return id_pjs;
    }

    public String getemail_pembeli() {
        return email_pembeli;
    }

    public int getTotal_bayar() {
        return total_bayar;
    }

    public String getMetode() {
        return metode;
    }

    public String getNo_bayar() {
        return no_bayar;
    }

    public String getGambar_qr() {
        return gambar_qr;
    }

    public Timestamp getSelesai_otomatis() {
        return selesai_otomatis;
    }

    public String getSelesaiStr() {
        return selesaiStr;
    }

    public String getKeterangan() {
        return keterangan;
    }
}
