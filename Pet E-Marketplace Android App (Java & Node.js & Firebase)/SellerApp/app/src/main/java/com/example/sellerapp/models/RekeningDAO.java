package com.example.sellerapp.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RekeningDAO {
    @Query("SELECT * FROM rekening WHERE email_pemilik = :email")
    List<Rekening> getAllRekening(String email);

    @Query("SELECT * FROM rekening WHERE dipilih = :dipilih and email_pemilik = :email")
    List<Rekening> getChosenRekening(boolean dipilih, String email);

    @Query("SELECT * FROM rekening WHERE id = :id")
    List<Rekening> getRekeningById(int id);

    @Insert
    void insert(Rekening rekening);

    @Query("UPDATE rekening SET dipilih = :dipilih WHERE id = :id")
    void changeChosen(boolean dipilih, int id);

    @Delete
    void delete(Rekening rekening);

    @Query("DELETE FROM rekening")
    void deleteAll();
}
