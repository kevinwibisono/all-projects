package my.istts.finalproject.models;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class FavoritDBAccess {
    private RoomDB db;
    private onFavoriteAdded favoriteAddedListener;
    private onFavoriteDeleted favoriteDeletedListener;
    private onFavoriteGot favoriteGotListener;

    public FavoritDBAccess(Application app) {
        db = RoomDB.getInstance(app);
    }

    public void setFavoriteAddedListener(onFavoriteAdded favoriteAddedListener) {
        this.favoriteAddedListener = favoriteAddedListener;
    }

    public void setFavoriteDeletedListener(onFavoriteDeleted favoriteDeletedListener) {
        this.favoriteDeletedListener = favoriteDeletedListener;
    }

    public void setFavoriteGotListener(onFavoriteGot favoriteGotListener) {
        this.favoriteGotListener = favoriteGotListener;
    }

    public void getFavorit(String email_pemilik, String id_item){
        new GetFavoriteTask().execute(email_pemilik+"|"+id_item);
    }

    public void getFavoritTipe(int tipe){
        new GetFavoritesByTypeTask().execute(tipe);
    }

    public void addFavorit(Favorit fav){
        new InsertFavoriteTask().execute(fav);
    }

    public void deleteFavorite(int id){
        new DeleteFavoriteTask().execute(id);
    }

    public void deleteAllFavorite(){
        new DeleteAllFavoriteTask().execute();
    }

    private class InsertFavoriteTask extends AsyncTask<Favorit, Void, Void> {

        @Override
        protected Void doInBackground(Favorit... favs) {
            db.favDAO().insert(favs[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            favoriteAddedListener.onAdded();
        }
    }

    private class DeleteFavoriteTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... ints) {
            db.favDAO().deleteById(ints[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(favoriteDeletedListener != null){
                favoriteDeletedListener.onDeleted();
            }
        }
    }

    private class DeleteAllFavoriteTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... ints) {
            db.favDAO().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(favoriteDeletedListener != null){
                favoriteDeletedListener.onDeleted();
            }
        }
    }

    private class GetFavoriteTask extends AsyncTask<String, Void, List<Favorit>> {

        @Override
        protected List<Favorit> doInBackground(String... strings) {
            String[] ids = strings[0].split("\\|");
            return db.favDAO().getFavoritItem(ids[0], ids[1]);
        }

        @Override
        protected void onPostExecute(List<Favorit> favs) {
            super.onPostExecute(favs);
            favoriteGotListener.onGot(favs);
        }
    }

    private class GetFavoritesByTypeTask extends AsyncTask<Integer, Void, List<Favorit>> {

        @Override
        protected List<Favorit> doInBackground(Integer... strings) {
            return db.favDAO().getFavoritesByType(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Favorit> favs) {
            super.onPostExecute(favs);
            favoriteGotListener.onGot(favs);
        }
    }

    public interface onFavoriteAdded{
        void onAdded();
    }

    public interface onFavoriteGot{
        void onGot(List<Favorit> favs);
    }

    public interface onFavoriteDeleted{
        void onDeleted();
    }

//    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();
//
//    public Task<QuerySnapshot> getFavorit(String email_pemilik, String id_item){
//        return firebaseDb.collection("favorit")
//                .whereEqualTo("email_pemilik", email_pemilik)
//                .whereEqualTo("id_item", id_item)
//                .get();
//    }
//
//    public Task<DocumentReference> addfavorit(String no_hp, String id_item){
//        Map<String, Object> favorit = new HashMap<>();
//        favorit.put("id_item", id_item);
//        favorit.put("email_pemilik", no_hp);
//
//        return firebaseDb.collection("favorit").add(favorit);
//    }
//
//    public Task<Void> deleteFavorit(String id_favorit){
//        return firebaseDb.collection("favorit")
//                .document(id_favorit)
//                .delete();
//    }
}
