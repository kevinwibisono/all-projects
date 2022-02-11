package my.istts.finalproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class DetailBookingHotel {
    private String id_detail;
    private String id_pj;
    private String metode_bayar;
    private Timestamp tgl_masuk;
    private Timestamp tgl_keluar;

    public DetailBookingHotel(DocumentSnapshot doc) {
        this.id_detail = doc.getId();
        this.id_pj = doc.getString("id_pj");
        this.metode_bayar = doc.getString("metode_bayar");
        this.tgl_masuk = doc.getTimestamp("tgl_masuk");
        this.tgl_keluar = doc.getTimestamp("tgl_keluar");
    }

    public String getTglMasuk(){
        Date bookDate = this.tgl_masuk.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        String tanggal = bookDate.getDate()+" "+months[bookDate.getMonth()]+" "+(bookDate.getYear()+1900);
        String waktu = addZero(bookDate.getHours())+":"+addZero(bookDate.getMinutes());
        return tanggal+" "+waktu;
    }

    public String getTglAkhir(){
        Date endDate = this.tgl_keluar.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        String tanggal = endDate.getDate()+" "+months[endDate.getMonth()]+" "+(endDate.getYear()+1900);
        String waktu = addZero(endDate.getHours())+":"+addZero(endDate.getMinutes());
        return tanggal+" "+waktu;
    }

    public String getTglKeluar(){
        long dateMillis = this.tgl_keluar.toDate().getTime();
        long oneDayMillis = 24 * 3600 * 1000;
        Date endDate = new Date(dateMillis + oneDayMillis);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        String tanggal = endDate.getDate()+" "+months[endDate.getMonth()]+" "+(endDate.getYear()+1900);
        String waktu = addZero(endDate.getHours())+":"+addZero(endDate.getMinutes());
        return tanggal+" "+waktu;
    }

    private String addZero(int hourMin){
        String hourMinStr = String.valueOf(hourMin);
        if(hourMinStr.length() < 2){
            hourMinStr = "0"+hourMin;
        }
        return hourMinStr;
    }

    public long getDurasiPenginapan(){
        long durationMillis = tgl_keluar.toDate().getTime() - tgl_masuk.toDate().getTime();
        long durationDays = durationMillis / (1000 * 3600 * 24);
        durationDays++;
        return durationDays;
    }

    public String getId_detail() {
        return id_detail;
    }

    public String getId_pj() {
        return id_pj;
    }

    public String getMetode_bayar() {
        return metode_bayar;
    }

    public Timestamp getTgl_keluar() {
        return tgl_keluar;
    }

    public Timestamp getTgl_masuk() {
        return tgl_masuk;
    }
}

