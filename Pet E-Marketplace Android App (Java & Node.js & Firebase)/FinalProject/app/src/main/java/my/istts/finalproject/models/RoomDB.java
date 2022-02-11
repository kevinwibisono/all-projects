package my.istts.finalproject.models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Akun.class, Cart.class, Favorit.class, Rekening.class}, version = 1)
public abstract class RoomDB extends RoomDatabase {
    public abstract AkunDAO akunDAO();
    public abstract CartDAO cartDAO();
    public abstract FavoritDAO favDAO();
    public abstract RekeningDAO rekeningDAO();
    private static RoomDB instance;

    public static RoomDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context,
                    RoomDB.class,
                    "localDB")
                    .build();
        }
        return instance;
    }
}
