package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Alamat {
    private String id;
    private String nama, no_hp, alamat_lengkap;
    private String kelurahan, kecamatan, kota, kodepos;
    private String catatan, koordinat;
    private boolean dipilih, dihapus;

    public Alamat(DocumentSnapshot doc){
        this.id = doc.getId();
        this.nama = doc.getString("nama");
        this.no_hp = doc.getString("no_hp");
        this.alamat_lengkap = doc.getString("alamat_lengkap");
        this.kelurahan = doc.getString("kelurahan");
        this.kecamatan = doc.getString("kecamatan");
        this.kota = doc.getString("kota");
        this.kodepos = doc.getString("kodepos");
        this.catatan = doc.getString("catatan");
        this.koordinat = doc.getString("koordinat");
        this.dipilih = doc.getBoolean("dipilih");
        this.dihapus = doc.getBoolean("dihapus");
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public String getAlamat_lengkap() {
        return alamat_lengkap;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public String getKota() {
        return kota;
    }

    public String getKodepos() {
        return kodepos;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public String[] getKoordinatArr(){
        return koordinat.split(",");
    }

    public boolean isDipilih() {
        return dipilih;
    }

    public boolean isDihapus() {
        return dihapus;
    }

    @Override
    public String toString() {
        String details = nama+"\n"+
                no_hp+"\n"+
                alamat_lengkap+"\n";

        if(!kelurahan.equals("")){
            details += kelurahan+", ";
        }
        if(!kecamatan.equals("")){
            details += kecamatan+", ";
        }
        if(!kota.equals("")){
            details += kota+" ";
        }
        if(!kodepos.equals("")){
            details += kodepos;
        }
        if(!catatan.equals("")){
            details += "\n"+catatan;
        }
        return details;
    }
}
