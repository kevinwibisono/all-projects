package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Rekening;
import my.istts.finalproject.models.RekeningDBAccess;
import my.istts.finalproject.models.RiwayatDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class TarikSaldoViewModel {
    private AkunDBAccess akunDB;
    private RekeningDBAccess rekDB;
    private RiwayatDBAccess hisDB;

    public TarikSaldoViewModel(Application application) {
        akunDB = new AkunDBAccess(application);
        hisDB = new RiwayatDBAccess(application);
        rekDB = new RekeningDBAccess(application);
    }

    private MutableLiveData<Integer> rekType = new MutableLiveData<>(0);
    private MutableLiveData<String> rekNum = new MutableLiveData<>("");
    private MutableLiveData<String> rekName = new MutableLiveData<>("");
    private MutableLiveData<Boolean> rekLoading = new MutableLiveData<>();

    private MutableLiveData<Integer> saldo = new MutableLiveData<>();
    public MutableLiveData<String> tarikSaldo = new MutableLiveData<>("");
    private MutableLiveData<Boolean> valid = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LiveData<Integer> getSaldo(){
        return saldo;
    }

    public LiveData<Boolean> isValid(){
        return valid;
    }

    public LiveData<Boolean> isRekeningLoading(){
        return rekLoading;
    }

    public LiveData<Integer> getRekType() {
        return rekType;
    }

    public LiveData<String> getRekName() {
        return rekName;
    }

    public LiveData<String> getRekNum() {
        return rekNum;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public void getSaldoRek(){
        rekLoading.setValue(true);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){

                    akunDB.getAccByEmail(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                saldo.setValue(documentSnapshot.getLong("saldo").intValue());
                            }
                        }
                    });

                    rekDB.setGetRekeningListener(new RekeningDBAccess.rekeningGotCallback() {
                        @Override
                        public void onRekeningGot(List<Rekening> listRek) {
                            if(listRek.size() > 0){
                                Rekening firstRek = listRek.get(0);

                                rekName.setValue(firstRek.getNama());
                                rekNum.setValue(firstRek.getNo_rek());
                                rekType.setValue(firstRek.getJenis_rek());
                            }
                            rekLoading.setValue(false);
                        }
                    });

                    rekDB.getChosenRekening(accountsGot.get(0).getEmail());
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void tarikSaldo(){
        if((Integer.parseInt(tarikSaldo.getValue()) <= saldo.getValue() && Integer.parseInt(tarikSaldo.getValue()) > 0)  && !rekNum.getValue().equals("")){
            loading.setValue(true);

            akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
                @Override
                public void onComplete(List<Akun> accountsGot) {
                    if(accountsGot.size() > 0){
                        String email = accountsGot.get(0).getEmail();
                        String nama = accountsGot.get(0).getNama();

                        hisDB.addPengajuanTarik(rekNum.getValue(), rekType.getValue(), rekName.getValue(), Integer.parseInt(tarikSaldo.getValue()),
                                nama, email).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                akunDB.reduceSaldo(email, Integer.parseInt(tarikSaldo.getValue())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loading.setValue(false);
                                        valid.setValue(true);
                                    }
                                });
                            }
                        });
                    }
                }
            });

            akunDB.getSavedAccounts();
        }
        else if(Integer.parseInt(tarikSaldo.getValue()) > saldo.getValue()){
            valid.setValue(false);
        }
    }


}
