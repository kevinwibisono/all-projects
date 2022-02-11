package my.istts.finalproject.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Adoption {
    private String id_pet;
    private String gambar;
    private String nama;
    private int umur, satuan_umur;
    private int jenis;
    private String ras;
    private int jenis_kelamin;
    private String deskripsi;
    private String email_pemilik;

    public Adoption(DocumentSnapshot doc) {
        this.id_pet = doc.getId();
        this.gambar = doc.getString("gambar");
        this.nama = doc.getString("nama");
        this.umur = doc.getLong("umur").intValue();
        this.satuan_umur = doc.getLong("satuan_umur").intValue();
        this.jenis = doc.getLong("jenis").intValue();
        this.jenis_kelamin = doc.getLong("jenis_kelamin").intValue();
        this.ras = doc.getString("ras");
        this.deskripsi = doc.getString("deskripsi");
        this.email_pemilik = doc.getString("email_pemilik");
    }

    public String getId_pet() {
        return id_pet;
    }

    public String getGambar() {
        return gambar;
    }

    public String getNama() {
        return nama;
    }

    public int getUmur() {
        return umur;
    }

    public int getSatuan_umur() {
        return satuan_umur;
    }

    public String getSatuanUmurStr() {
        String[] ageType = {"Bulan", "Tahun"};
        return ageType[satuan_umur];
    }

    public int getJenis() {
        return jenis;
    }

    public String getJenisHewan(){
        String[] jenis = {"Anjing", "Kucing", "Kelinci", "Burung", "Ikan", "Hamster", "Reptil", "Lainnya"};
        return jenis[this.jenis];
    }

    public int getJenis_kelamin() {
        return jenis_kelamin;
    }

    public String getJenisKelaminHewan(){
        String[] jenisKelamin = {"Jantan", "Betina"};
        return jenisKelamin[this.jenis_kelamin];
    }

    public String getRas() {
        return ras;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getEmail_pemilik() {
        return email_pemilik;
    }
}
