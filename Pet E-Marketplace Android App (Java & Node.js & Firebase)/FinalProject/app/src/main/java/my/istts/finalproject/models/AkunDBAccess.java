package my.istts.finalproject.models;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AkunDBAccess {

    private RoomDB db;
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    private Akun saveAccount;

    private onCompleteGetListener getCompleteListener;
    private onClearedListener clearedListener;

    public AkunDBAccess(Application app) {
        db = RoomDB.getInstance(app);
    }

    public AkunDBAccess(Context ctx) {
        db = RoomDB.getInstance(ctx);
    }

    public AkunDBAccess() {
    }

    //BAGIAN LOGIN
    public void getSavedAccounts(){
        new GetAccountsTask().execute();
    }

    public Task<DocumentSnapshot> getAccByEmail(String email){
        return firebaseDb.collection("akun")
                .document(email)
                .get();
    };

    public Task<QuerySnapshot> loginPaw(String email, String password){
        return firebaseDb.collection("akun")
                .whereEqualTo("email", email)
                .whereEqualTo("password", UUID.nameUUIDFromBytes(password.getBytes()).toString())
                .get();
    };

//    public Task<QuerySnapshot> loginGoogle(String googleId){
//        return firebaseDb.collection("akun")
//                .whereEqualTo("google", googleId)
//                .get();
//    };

    public void saveLogAccount(Akun loggedInAccount){
        saveAccount = loggedInAccount;
        new ClearAkunTask().execute();
    }


    //BAGIAN REGISTER
    public Task<QuerySnapshot> checkEmail(String email){
        return firebaseDb.collection("akun")
                .whereEqualTo("email", email)
                .get();
    }

    public void addSaldo(String email, int paymentQty){
        firebaseDb.collection("akun")
                .document(email)
                .update("saldo", FieldValue.increment(paymentQty));
    }

    public Task<Void> reduceSaldo(String email, int jumlah){
        return firebaseDb.collection("akun")
                .document(email)
                .update("saldo", FieldValue.increment(jumlah*-1));
    }

    public Task<Void> register(String email, String nama, String password){
        saveAccount = new Akun(email, nama, password);
        Map<String, Object> akun = new HashMap<>();
        akun.put("email", email);
        akun.put("nama", nama);
        akun.put("password", UUID.nameUUIDFromBytes(password.getBytes()).toString());
        akun.put("saldo", 0);
        akun.put("penjual", false);

        return firebaseDb.collection("akun").document(email)
                .set(akun);
    }

    public void clearAccounts() {
        new ClearAkunTask().execute();
    }


    //TASK-TASK ROOM
    private class ClearAkunTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            db.akunDAO().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(saveAccount != null){
                new InsertTask().execute(saveAccount);
            }
            if(clearedListener != null){
                clearedListener.onCleared();
            }
        }
    }

    private class InsertTask extends AsyncTask<Akun, Void, Void>{

        @Override
        protected Void doInBackground(Akun... akuns) {
            //akun hanya selalu akan ada 1, sebelum insert akun baru, clear dulu
            db.akunDAO().insert(akuns[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class GetAccountsTask extends AsyncTask<Void, Void, List<Akun>>{

        @Override
        protected List<Akun> doInBackground(Void... voids) {
            return db.akunDAO().getAll();
        }

        @Override
        protected void onPostExecute(List<Akun> akuns) {
            super.onPostExecute(akuns);
            getCompleteListener.onComplete(akuns);
        }
    }

    public void setGetCompleteListener(onCompleteGetListener listener){
        getCompleteListener = listener;
    }

    public void setClearedListener(onClearedListener clearedListener) {
        this.clearedListener = clearedListener;
    }

    public interface onCompleteGetListener{
        void onComplete(List<Akun> accountsGot);
    }

    public interface onClearedListener{
        void onCleared();
    }
}
