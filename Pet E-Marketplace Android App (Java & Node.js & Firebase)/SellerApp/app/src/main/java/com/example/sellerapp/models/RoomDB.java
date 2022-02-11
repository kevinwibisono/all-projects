package com.example.sellerapp.models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Akun.class, DetailPenjual.class, Rekening.class}, version = 1)
public abstract class RoomDB extends RoomDatabase {
    public abstract AkunDAO akunDAO();
    public abstract DetailPenjualDAO detailPenjualDAO();
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
