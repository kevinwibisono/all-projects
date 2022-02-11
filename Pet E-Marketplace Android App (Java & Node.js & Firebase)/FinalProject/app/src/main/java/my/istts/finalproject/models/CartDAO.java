package my.istts.finalproject.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDAO {
    @Query("SELECT * FROM cart WHERE tipe = :tipe ORDER BY email_seller desc")
    List<Cart> getCartItems(int tipe);

    @Query("SELECT * FROM cart;")
    List<Cart> getAllCarts();

    @Query("SELECT * FROM cart WHERE id = :id")
    List<Cart> getCartWithId(int id);

    @Query("SELECT * FROM cart WHERE id_item = :id_item")
    List<Cart> getCartItemsWithId(String id_item);

    @Query("SELECT * FROM cart WHERE id_item = :id_item and id_variasi = :id_variasi")
    List<Cart> getCartItemsWithIdVariant(String id_item, String id_variasi);

    @Query("SELECT * FROM cart WHERE email_seller = :email_seller")
    List<Cart> getCartItemsWithSeller(String email_seller);

    @Insert
    void insert(Cart cart);

    @Update
    void update(Cart cart);

    @Delete
    void delete(Cart cart);

    @Query("DELETE FROM cart WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM cart")
    void deleteAll();
}
