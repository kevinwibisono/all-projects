package my.istts.finalproject.models;


import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class CartDBAccess {
    private RoomDB db;
    private onCartItemsGot itemsGotListener;
    private onCartItemsChanged itemChangedListener;
    private onCartFound foundListener;
    private onCartDeleted deletedListener;

    public CartDBAccess(Application app) {
        db = RoomDB.getInstance(app);
    }

    public void setItemsGotListener(onCartItemsGot itemsGotListener) {
        this.itemsGotListener = itemsGotListener;
    }

    public void setItemChangedListener(onCartItemsChanged itemChangedListener) {
        this.itemChangedListener = itemChangedListener;
    }

    public void setFoundListener(onCartFound foundListener) {
        this.foundListener = foundListener;
    }

    public void setDeletedListener(onCartDeleted deletedListener) {
        this.deletedListener = deletedListener;
    }

    public void searchCartItem(String id_item){
        new SearchCartItemTask().execute(id_item);
    }

    public void searchCartItemVariant(String id_item, String id_varian){
        new SearchCartItemVariantTask().execute(id_item+"|"+id_varian);
    }

    public void insertCartItem(Cart cart){
        new InsertCartItemTask().execute(cart);
    }

    public void updateCartItem(Cart cart){
        new UpdateCartItemTask().execute(cart);
    }

    public void deleteCartItem(int id){
        new DeleteCartItemTask().execute(id);
    }

    public void deleteAllCartItems(){
        new DeleteAllCartItemTask().execute();
    }

    public void getAllCartItemsByType(int tipe){
        new GetCartItemsTask().execute(tipe);
    }

    public void getAllCartItems(){
        new GetAllCartTask().execute();
    }

    public void getCartWithId(int id){
        new GetCartTask().execute(id);
    }

    private class InsertCartItemTask extends AsyncTask<Cart, Void, Void>{

        @Override
        protected Void doInBackground(Cart... carts) {
            db.cartDAO().insert(carts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            itemChangedListener.onChangedItems();
        }
    }

    private class GetAllCartTask extends AsyncTask<Void, Void, List<Cart>>{

        @Override
        protected List<Cart> doInBackground(Void... voids) {
            return db.cartDAO().getAllCarts();
        }

        @Override
        protected void onPostExecute(List<Cart> cartItems) {
            super.onPostExecute(cartItems);
            itemsGotListener.onGotItems(cartItems);
        }
    }

    private class GetCartTask extends AsyncTask<Integer, Void, List<Cart>>{

        @Override
        protected List<Cart> doInBackground(Integer... integers) {
            return db.cartDAO().getCartWithId(integers[0]);
        }

        @Override
        protected void onPostExecute(List<Cart> cartItems) {
            super.onPostExecute(cartItems);
            itemsGotListener.onGotItems(cartItems);
        }
    }

    private class GetCartItemsTask extends AsyncTask<Integer, Void, List<Cart>>{

        @Override
        protected List<Cart> doInBackground(Integer... integers) {
            return db.cartDAO().getCartItems(integers[0]);
        }

        @Override
        protected void onPostExecute(List<Cart> cartItems) {
            super.onPostExecute(cartItems);
            itemsGotListener.onGotItems(cartItems);
        }
    }

    private class SearchCartItemTask extends AsyncTask<String, Void, List<Cart>>{

        @Override
        protected List<Cart> doInBackground(String... strings) {
            return db.cartDAO().getCartItemsWithId(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Cart> cartItems) {
            super.onPostExecute(cartItems);
            foundListener.onFound(cartItems);
        }
    }

    private class SearchCartItemVariantTask extends AsyncTask<String, Void, List<Cart>>{

        @Override
        protected List<Cart> doInBackground(String... strings) {
            String[] ids = strings[0].split("\\|");
            return db.cartDAO().getCartItemsWithIdVariant(ids[0], ids[1]);
        }

        @Override
        protected void onPostExecute(List<Cart> cartItems) {
            super.onPostExecute(cartItems);
            foundListener.onFound(cartItems);
        }
    }

    private class UpdateCartItemTask extends AsyncTask<Cart, Void, Void>{

        @Override
        protected Void doInBackground(Cart... carts) {
            db.cartDAO().update(carts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            itemChangedListener.onChangedItems();
        }
    }

    private class DeleteCartItemTask extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... ints) {
            db.cartDAO().deleteById(ints[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(deletedListener != null){
                deletedListener.onDeleted();
            }
        }
    }

    private class DeleteAllCartItemTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... ints) {
            db.cartDAO().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(deletedListener != null){
                deletedListener.onDeleted();
            }
        }
    }

    public interface onCartItemsGot{
        void onGotItems(List<Cart> items);
    }

    public interface onCartItemsChanged{
        void onChangedItems();
    }

    public interface onCartFound{
        void onFound(List<Cart> cartItems);
    }

    public interface onCartDeleted{
        void onDeleted();
    }
}
