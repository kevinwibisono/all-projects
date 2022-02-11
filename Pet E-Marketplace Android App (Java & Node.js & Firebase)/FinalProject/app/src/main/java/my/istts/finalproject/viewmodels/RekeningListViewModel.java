package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Rekening;
import my.istts.finalproject.models.RekeningDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.RekeningItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class RekeningListViewModel {
    private RekeningDBAccess rekeningDB;
    private AkunDBAccess akunDB;

    public RekeningListViewModel(Application application) {
        rekeningDB = new RekeningDBAccess(application);
        akunDB = new AkunDBAccess(application);
    }

    private MutableLiveData<ArrayList<RekeningItemViewModel>> rekVMs = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Boolean> rekeningLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> choseRekDone = new MutableLiveData<>();

    public LiveData<ArrayList<RekeningItemViewModel>> getRekVMs(){
        return rekVMs;
    }

    public LiveData<Boolean> isRekeningLoading(){
        return rekeningLoading;
    }

    public LiveData<Boolean> isChooseRekDone(){
        return choseRekDone;
    }

    public void getRekeningList(){
        rekeningLoading.setValue(true);
        rekVMs.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    rekeningDB.setGetRekeningListener(new RekeningDBAccess.rekeningGotCallback() {
                        @Override
                        public void onRekeningGot(List<Rekening> listRek) {
                            addRekToLiveData(listRek);
                        }
                    });

                    rekeningDB.getAllRekening(accountsGot.get(0).getEmail());
                }
            }
        });

        akunDB.getSavedAccounts();


    }

    private void addRekToLiveData(List<Rekening> reks){
        ArrayList<RekeningItemViewModel> currentVMs = rekVMs.getValue();
        for (Rekening rekening:
             reks) {
            currentVMs.add(new RekeningItemViewModel(rekening));
        }
        rekVMs.setValue(currentVMs);
        rekeningLoading.setValue(false);
    }

    public void chooseRek(int id_rek){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    rekeningDB.setGetRekeningListener(new RekeningDBAccess.rekeningGotCallback() {
                        @Override
                        public void onRekeningGot(List<Rekening> listRek) {
                            for (Rekening rek:
                                    listRek) {
                                rekeningDB.updateChosenRekening(false, rek.getId());
                            }
                            rekeningDB.setUpdateRekeningListener(new RekeningDBAccess.rekeningUpdatedCallback() {
                                @Override
                                public void onRekeningUpdated() {
                                    choseRekDone.setValue(true);
                                }
                            });


                            rekeningDB.updateChosenRekening(true, id_rek);
                        }
                    });

                    rekeningDB.getAllRekening(accountsGot.get(0).getEmail());
                }
            }
        });

        akunDB.getSavedAccounts();
    }
}
