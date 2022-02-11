package my.istts.finalproject.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rekening")
public class Rekening {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "jenis_rek")
    private int jenis_rek;

    @ColumnInfo(name = "no_rek")
    private String no_rek;

    @ColumnInfo(name = "nama")
    private String nama;

    @ColumnInfo(name = "email_pemilik")
    private String email_pemilik;

    @ColumnInfo(name = "dipilih")
    private Boolean dipilih;

    public Rekening(int jenis_rek, String nama, String no_rek, String email_pemilik, Boolean dipilih) {
        this.jenis_rek = jenis_rek;
        this.no_rek = no_rek;
        this.nama = nama;
        this.email_pemilik = email_pemilik;
        this.dipilih = dipilih;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJenis_rek() {
        return jenis_rek;
    }

    public void setJenis_rek(int jenis_rek) {
        this.jenis_rek = jenis_rek;
    }

    public String getNo_rek() {
        return no_rek;
    }

    public void setNo_rek(String no_rek) {
        this.no_rek = no_rek;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Boolean getDipilih() {
        return dipilih;
    }

    public void setDipilih(Boolean dipilih) {
        this.dipilih = dipilih;
    }

    public String getEmail_pemilik() {
        return email_pemilik;
    }
}
