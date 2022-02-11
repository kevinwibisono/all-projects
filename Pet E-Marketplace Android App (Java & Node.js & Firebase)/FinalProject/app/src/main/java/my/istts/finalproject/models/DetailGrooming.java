package my.istts.finalproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class DetailGrooming {
    private String id_detail;
    private String id_pj;
    private String id_alamat;
    private Timestamp tgl_booking;
    private String[] posisi_groomer;
    private String metode_bayar;

    public DetailGrooming(DocumentSnapshot doc){
        this.id_detail = doc.getId();
        this.id_pj = doc.getString("id_pj");
        this.id_alamat = doc.getString("id_alamat");
        this.tgl_booking = doc.getTimestamp("tgl_booking");
        this.posisi_groomer = doc.getString("posisi_groomer").split(",");
        this.metode_bayar = doc.getString("metode_bayar");
    }

    public String getTglBooking(){
        Date chatDate = this.tgl_booking.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        String tanggal = chatDate.getDate()+" "+months[chatDate.getMonth()]+" "+(chatDate.getYear()+1900);
        String waktu = addZero(chatDate.getHours())+":"+addZero(chatDate.getMinutes());
        return tanggal+" "+waktu;
    }

    private String addZero(int hourMin){
        String hourMinStr = String.valueOf(hourMin);
        if(hourMinStr.length() < 2){
            hourMinStr = "0"+hourMin;
        }
        return hourMinStr;
    }

    public String getId_detail() {
        return id_detail;
    }

    public String getId_pj() {
        return id_pj;
    }

    public String getId_alamat() {
        return id_alamat;
    }

    public String[] getPosisi_groomer() {
        return posisi_groomer;
    }

    public String getMetode_bayar() {
        return metode_bayar;
    }

    public Timestamp getTgl_booking() {
        return tgl_booking;
    }
}
