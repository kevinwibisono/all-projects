package com.example.sellerapp.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DetailPenjualDAO {
    @Query("SELECT * FROM detail_penjual")
    List<DetailPenjual> getDetailByPhone();

    @Insert
    void insert(DetailPenjual detail);

    @Update
    void update(DetailPenjual detail);

    @Delete
    void delete(DetailPenjual detail);

    @Query("DELETE FROM detail_penjual")
    void deleteAll();
}
