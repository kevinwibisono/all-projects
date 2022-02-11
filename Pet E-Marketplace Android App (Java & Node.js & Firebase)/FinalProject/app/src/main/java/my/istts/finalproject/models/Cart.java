package my.istts.finalproject.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart")
public class Cart {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "email_seller")
    private String email_seller;

    @ColumnInfo(name = "id_item")
    private String id_item;

    @ColumnInfo(name = "id_variasi")
    private String id_variasi;

    @ColumnInfo(name = "jumlah")
    private int jumlah;

    @ColumnInfo(name = "tipe")
    private int tipe;

    public Cart(String email_seller, String id_item, String id_variasi, int jumlah, int tipe) {
        this.email_seller = email_seller;
        this.id_item = id_item;
        this.id_variasi = id_variasi;
        this.jumlah = jumlah;
        this.tipe = tipe;
    }

    public void addJumlah(){
        jumlah++;
    }

    public void redJumlah(){
        jumlah--;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getEmail_seller() {
        return email_seller;
    }

    public String getId_item() {
        return id_item;
    }

    public String getId_variasi() {
        return id_variasi;
    }

    public int getJumlah() {
        return jumlah;
    }

    public int getTipe() {
        return tipe;
    }
}
