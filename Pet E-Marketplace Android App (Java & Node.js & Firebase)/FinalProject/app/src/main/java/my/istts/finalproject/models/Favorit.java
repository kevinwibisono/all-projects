package my.istts.finalproject.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorit")
public class Favorit {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "email_pemilik")
    private String email_pemilik;

    @ColumnInfo(name = "id_item")
    private String id_item;

    @ColumnInfo(name = "tipe")
    private int tipe;
    //0->product    1->kamar     2->hewan adopsi
    //3->artikel    4->pet discuss

    public Favorit(String email_pemilik, String id_item, int tipe) {
        this.email_pemilik = email_pemilik;
        this.id_item = id_item;
        this.tipe = tipe;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getEmail_pemilik() {
        return email_pemilik;
    }

    public String getId_item() {
        return id_item;
    }

    public int getTipe() {
        return tipe;
    }
}
