package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class DetailPenjual {
    private int id;
    private int role;
    private String deskripsi;
    private String poster;
    private String kurir;
    private String janji_temu;
    private String jam_buka;
    private String jam_tutup;
    private String alamat;
    private String koordinat;

    public DetailPenjual(DocumentSnapshot dataFromDB){
        this.role = dataFromDB.getLong("tipe").intValue();
        this.poster = dataFromDB.getString("poster");
        this.deskripsi = dataFromDB.getString("deskripsi");
        this.kurir = dataFromDB.getString("kurir");
        this.janji_temu = dataFromDB.getString("janji_temu");
        this.jam_buka = dataFromDB.getString("jam_buka");
        this.jam_tutup = dataFromDB.getString("jam_tutup");
        this.alamat = dataFromDB.getString("alamat");
        this.koordinat = dataFromDB.getString("koordinatAlamat");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getKurir() {
        return kurir;
    }

    public void setKurir(String kurir) {
        this.kurir = kurir;
    }

    public String getJanji_temu() {
        return janji_temu;
    }

    public void setJanji_temu(String janji_temu) {
        this.janji_temu = janji_temu;
    }

    public String getJam_buka() {
        return jam_buka;
    }

    public void setJam_buka(String jam_buka) {
        this.jam_buka = jam_buka;
    }

    public String getJam_tutup() {
        return jam_tutup;
    }

    public void setJam_tutup(String jam_tutup) {
        this.jam_tutup = jam_tutup;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    public String[] getPosterSplitted(){
        return poster.split("\\|");
    }

    public String[] getJamBukaSplitted(){
        return jam_buka.split("\\|", -1);
    }

    public String[] getJamTutupSplitted(){
        return jam_tutup.split("\\|", -1);
    }
}
