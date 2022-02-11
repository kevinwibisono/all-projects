package com.example.sellerapp.models;

import android.app.Application;
import android.os.AsyncTask;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DetailPenjualDBAccess {
    private RoomDB db;
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    private DetailPenjual toBeAdded;
    private onCompleteGetDetailListener detailCompleteListener;
    private onDetailClearedListener detailClearedListener;

    //0 -> profile picture
    //sisanya -> poster-poster
    public DetailPenjualDBAccess(Application app) {
        db = RoomDB.getInstance(app);
    }

    public DetailPenjualDBAccess(){

    }

    public Task<Void> addDetailPenjual(DetailPenjual newDetail, String email, String[] posters){
        //dapatkan dulu no_hp dari Akun yg tersimpan di Room Database
        toBeAdded = newDetail;
        toBeAdded.setPoster(combineArrayIntoString(posters));

        Map<String, Object> detail = new HashMap<>();
        detail.put("tipe", newDetail.getRole());
        detail.put("deskripsi", newDetail.getDeskripsi());
        detail.put("poster", toBeAdded.getPoster());
        detail.put("kurir", newDetail.getKurir());
        detail.put("janji_temu", newDetail.getJanji_temu());
        detail.put("jam_buka", newDetail.getJam_buka());
        detail.put("jam_tutup", newDetail.getJam_tutup());
        detail.put("alamat", newDetail.getAlamat());
        detail.put("koordinatAlamat", newDetail.getKoordinat());

        return firebaseDb.collection("detail_penjual").document(email)
                .set(detail);
    }

    public void clearDetails(){
        new ClearTask().execute();
    }

    public void getLocalDetail(){
        new GetDetailTask().execute();
    }

    //mungkin saja akun ada, tetapi detail tdk ada
    //oleh karena itu di setiap viewmodel, dicek apakah data null
    public Task<DocumentSnapshot> getDBAkunDetail(String email){
        return firebaseDb.collection("detail_penjual")
                .document(email)
                .get();
    };

    public void saveDetailFromLogin(DetailPenjual accountDetail){
        this.toBeAdded = accountDetail;
        new ClearTask().execute();
    }

    private class ClearTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            db.detailPenjualDAO().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(toBeAdded != null){
                ///jika ada detail yang ditambahkan
                //maka setelah clear panggil insert task
                new InsertTask().execute(toBeAdded);
            }
            if(detailClearedListener != null){
                detailClearedListener.onDetailCleared();
            }
        }
    }

    private class InsertTask extends AsyncTask<DetailPenjual, Void, Void>{

        @Override
        protected Void doInBackground(DetailPenjual... detailPenjuals) {
            db.detailPenjualDAO().insert(detailPenjuals[0]);
            return null;
        }
    }

    private class GetDetailTask extends AsyncTask<Void, Void, List<DetailPenjual>>{

        @Override
        protected List<DetailPenjual> doInBackground(Void... strings) {
            return db.detailPenjualDAO().getDetailByPhone();
        }

        @Override
        protected void onPostExecute(List<DetailPenjual> detailPenjuals) {
            super.onPostExecute(detailPenjuals);
            detailCompleteListener.onDetailComplete(detailPenjuals);
        }
    }

    public void setGetDetailCompleteListener(onCompleteGetDetailListener listener){
        detailCompleteListener = listener;
    }

    public void setDetailClearedListener(onDetailClearedListener detailClearedListener) {
        this.detailClearedListener = detailClearedListener;
    }

    public interface onCompleteGetDetailListener{
        void onDetailComplete(List<DetailPenjual> detailsGot);
    }

    public interface onDetailClearedListener{
        void onDetailCleared();
    }

    private String combineArrayIntoString(String[] array){
        String combined = "";
        for (int i = 0; i < array.length; i++) {
            if(array[i] == null){
                array[i] = "";
            }
            if(combined.equals("")){
                combined += array[i];
            }
            else{
                combined += "|"+array[i];
            }
        }
        return combined;
    }
}
