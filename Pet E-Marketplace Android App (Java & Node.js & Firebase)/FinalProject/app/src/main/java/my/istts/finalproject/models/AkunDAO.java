package my.istts.finalproject.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AkunDAO {
    @Query("SELECT * FROM akun")
    List<Akun> getAll();

    @Insert
    void insert(Akun akun);

    @Update
    void update(Akun akun);

    @Delete
    void delete(Akun akun);

    @Query("DELETE FROM akun")
    void deleteAll();
}
