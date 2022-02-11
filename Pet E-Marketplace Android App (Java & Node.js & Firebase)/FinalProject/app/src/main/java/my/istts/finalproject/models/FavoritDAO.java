package my.istts.finalproject.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoritDAO {
    @Query("SELECT * FROM favorit WHERE email_pemilik = :email_pemilik and id_item = :id_item")
    List<Favorit> getFavoritItem(String email_pemilik, String id_item);

    @Query("SELECT * FROM favorit WHERE email_pemilik = :email_pemilik")
    List<Favorit> getMyFavorites(String email_pemilik);

    @Query("SELECT * FROM favorit WHERE tipe = :tipe")
    List<Favorit> getFavoritesByType(int tipe);

    @Insert
    void insert(Favorit fav);

    @Update
    void update(Favorit fav);

    @Delete
    void delete(Favorit fav);

    @Query("DELETE FROM favorit WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM favorit")
    void deleteAll();
}
