package my.istts.finalproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Complain {
    private String id_complain;
    private String id_pj;
    private String keluhan;
    private String id_item;
    private int jumlah_kembali;
    private Timestamp tanggal;
    private int status; //0->blm direspon    1->diterima    2->ditolak
    private String bukti_gambar;
    private String link_video;

    public Complain(DocumentSnapshot doc){
        id_complain = doc.getId();
        id_pj = doc.getString("id_pj");
        keluhan = doc.getString("keluhan");
        id_item = doc.getString("id_item");
        jumlah_kembali = doc.getLong("jumlah_kembali").intValue();
        tanggal = doc.getTimestamp("tanggal");
        status = doc.getLong("status").intValue();
        bukti_gambar = doc.getString("bukti_gambar");
        link_video = doc.getString("link_video");
    }

    public String[] getBuktiArray(){
        return bukti_gambar.split("\\|");
    }

    public String getTanggalStr() {
        Date date = this.tanggal.toDate();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        return date.getDate()+" "+months[date.getMonth()]+" "+(date.getYear()+1900)
                + " " + addZero(date.getHours())+":"+addZero(date.getMinutes());
    }

    private String addZero(int hourMin){
        String hourMinStr = String.valueOf(hourMin);
        if(hourMinStr.length() < 2){
            hourMinStr = "0"+hourMin;
        }
        return hourMinStr;
    }

    public String getStatusStr(){
        String[] statuses = {"Belum Ditanggapi", "Diterima", "Ditolak"};
        return statuses[status];
    }

    public String[] getComplainedItems(){
        return id_item.split("\\|");
    }

    public String getId_complain() {
        return id_complain;
    }

    public String getId_pj() {
        return id_pj;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public int getJumlah_kembali() {
        return jumlah_kembali;
    }

    public int getStatus() {
        return status;
    }

    public String getLink_video() {
        return link_video;
    }
}
