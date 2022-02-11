package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Alamat;
import my.istts.finalproject.models.AlamatDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.AddressItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddressChooseViewModel {
    private AlamatDBAccess alamatDB;
    private AkunDBAccess akunDB;

    public AddressChooseViewModel(Application app) {
        this.alamatDB = new AlamatDBAccess();
        this.akunDB = new AkunDBAccess(app);
    }

    private MutableLiveData<ArrayList<AddressItemViewModel>> addrsVMs = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LiveData<ArrayList<AddressItemViewModel>> getAddrsVMs(){
        return addrsVMs;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public void getAddresses(){
        loading.setValue(true);
        addrsVMs.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    alamatDB.getAllAddresses(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<Alamat> alamatArr = new ArrayList<>();
                                for (DocumentSnapshot addrDoc:
                                     queryDocumentSnapshots.getDocuments()) {
                                    alamatArr.add(new Alamat(addrDoc));
                                }
                                addAlamatToVMs(alamatArr);
                            }
                            else{
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void deleteAddress(String id){
        alamatDB.selectAddress(id, false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                alamatDB.deleteAddress(id).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getAddresses();
                    }
                });
            }
        });
    }

    public void setSelected(String id){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    alamatDB.getSelectedAddress(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot doc:
                                 queryDocumentSnapshots.getDocuments()) {
                                alamatDB.selectAddress(doc.getId(), false);
                            }
                            alamatDB.selectAddress(id, true);
                        }
                    });

                }
            }
        });

        akunDB.getSavedAccounts();

    }

    private void addAlamatToVMs(ArrayList<Alamat> addrs){
        ArrayList<AddressItemViewModel> currentVMs = addrsVMs.getValue();
        for (Alamat addr:
             addrs) {
            currentVMs.add(new AddressItemViewModel(addr));
        }
        addrsVMs.setValue(currentVMs);
        loading.setValue(false);
    }
}
