package my.istts.finalproject.models;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class RekeningDBAccess {
    private RoomDB db;
    private rekeningGotCallback getRekeningListener;
    private rekeningAddedCallback addRekeningListener;
    private rekeningUpdatedCallback updateRekeningListener;

    public void setGetRekeningListener(rekeningGotCallback getRekeningListener) {
        this.getRekeningListener = getRekeningListener;
    }

    public void setAddRekeningListener(rekeningAddedCallback addRekeningListener) {
        this.addRekeningListener = addRekeningListener;
    }

    public void setUpdateRekeningListener(rekeningUpdatedCallback updateRekeningListener) {
        this.updateRekeningListener = updateRekeningListener;
    }

    public RekeningDBAccess(Application app){
        db = RoomDB.getInstance(app);
    }

    public void getChosenRekening(String email){
        new GetChosenRekTask().execute(email);
    }

    public void getAllRekening(String email){
        new GetAllRekTask().execute(email);
    }

    public void insertRekening(Rekening newRek){
        new InsertRekTask().execute(newRek);
    }

    public void updateChosenRekening(boolean chosen, int id){
        String combined = chosen+"|"+id;
        new ChangeChosenTask().execute(combined);
    }

    private class GetChosenRekTask extends AsyncTask<String, Void, List<Rekening>> {

        @Override
        protected List<Rekening> doInBackground(String... voids) {
            return db.rekeningDAO().getChosenRekening(true, voids[0]);
        }

        @Override
        protected void onPostExecute(List<Rekening> recs) {
            super.onPostExecute(recs);
            getRekeningListener.onRekeningGot(recs);
        }
    }

    private class GetAllRekTask extends AsyncTask<String, Void, List<Rekening>> {

        @Override
        protected List<Rekening> doInBackground(String... voids) {
            return db.rekeningDAO().getAllRekening(voids[0]);
        }

        @Override
        protected void onPostExecute(List<Rekening> recs) {
            super.onPostExecute(recs);
            getRekeningListener.onRekeningGot(recs);
        }
    }

    private class InsertRekTask extends AsyncTask<Rekening, Void, Void> {

        @Override
        protected Void doInBackground(Rekening... voids) {
            db.rekeningDAO().insert(voids[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            addRekeningListener.onRekeningAdded();
        }
    }

    private class ChangeChosenTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            String[] chosenAndId = voids[0].split("\\|");
            boolean chosen = Boolean.parseBoolean(chosenAndId[0]);
            int id = Integer.parseInt(chosenAndId[1]);
            db.rekeningDAO().changeChosen(chosen, id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(updateRekeningListener != null){
                updateRekeningListener.onRekeningUpdated();
            }
        }
    }

    public interface rekeningGotCallback{
        void onRekeningGot(List<Rekening> listRek);
    }

    public interface rekeningAddedCallback{
        void onRekeningAdded();
    }

    public interface rekeningUpdatedCallback{
        void onRekeningUpdated();
    }
}
