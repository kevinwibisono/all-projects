package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Alamat;
import my.istts.finalproject.models.AlamatDBAccess;
import my.istts.finalproject.inputclasses.AlamatInput;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AddAddressViewModel {
    private AlamatDBAccess alamatDB;
    private AkunDBAccess akunDB;

    public AddAddressViewModel(Application app) {
        akunDB = new AkunDBAccess(app);
        alamatDB = new AlamatDBAccess();
    }

    private boolean updating = false;

    public boolean isUpdating(){
        return updating;
    }

    private String idAddr;
    public AlamatInput inputs = new AlamatInput();
    private MutableLiveData<String[]> errors = new MutableLiveData<>(new String[4]);
    private MutableLiveData<Integer> focusedField = new MutableLiveData<>();
    private MutableLiveData<String> loadingTitle = new MutableLiveData<>("Menambahkan Alamat...");
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> done = new MutableLiveData<>();

    public LiveData<String[]> getErrors(){
        return errors;
    }

    public LiveData<Integer> getFocusedField(){
        return focusedField;
    }

    public LiveData<String> getLoadingTitle(){
        return loadingTitle;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<Boolean> isDone(){
        return done;
    }

    public void setAddress(String id){
        updating = true;
        idAddr = id;
        loadingTitle.setValue("Mengubah Alamat...");
        alamatDB.getAddressById(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Alamat alamat = new Alamat(documentSnapshot);
                inputs.nama.setValue(alamat.getNama());
                inputs.hp.setValue(alamat.getNo_hp());
                inputs.alamat.setValue(alamat.getAlamat_lengkap());
                inputs.kelurahan.setValue(alamat.getKelurahan());
                inputs.kecamatan.setValue(alamat.getKecamatan());
                inputs.kota.setValue(alamat.getKota());
                inputs.kodepos.setValue(alamat.getKodepos());
                inputs.catatan.setValue(alamat.getCatatan());
            }
        });
    }

    public void getHereAddress(String fullAddress, String city, String postalCode, String sub, String district, String koor){
        inputs.alamat.setValue(fullAddress);
        inputs.kodepos.setValue(postalCode);
        inputs.kota.setValue(city);
        inputs.kelurahan.setValue(sub);
        inputs.kecamatan.setValue(district);
        inputs.setKoordinat(koor);
    }

    public void doChecking(){
        if(inputs.emptyField() > -1){
            String[] error = {"", "", ""};
            error[inputs.emptyField()] = "Isian Tidak Boleh Kosong";
            errors.setValue(error);
            focusedField.setValue(inputs.emptyField());
        }
        else if(inputs.phoneInvalid()){
            String[] error = {"", "No HP Tidak Valid", ""};
            errors.setValue(error);
            focusedField.setValue(1);
        }
        else{
            if(updating){
                updateAddress();
            }
            else{
                addAddress();
            }
        }
    }

    private void addAddress(){
        loading.setValue(true);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    alamatDB.insertAddress(accountsGot.get(0).getEmail(), inputs).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            loading.setValue(false);
                            done.setValue(true);
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void updateAddress(){
        loading.setValue(true);

        alamatDB.updateAddress(idAddr, inputs).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.setValue(false);
                done.setValue(true);
            }
        });
    }
}
