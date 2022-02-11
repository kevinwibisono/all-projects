package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.DetailPenjual;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class LoginViewModel {

    private AkunDBAccess akunDB;
    private DetailPenjualDBAccess detailDB;

    public MutableLiveData<String> email = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    private MutableLiveData<String[]> fieldErrors;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private int savedRole;

    public LoginViewModel(Application application) {
        akunDB = new AkunDBAccess(application);
        detailDB = new DetailPenjualDBAccess(application);
    }

    public LiveData<String[]> getFieldErrors(){
        if(fieldErrors == null){
            fieldErrors = new MutableLiveData<>();
        }
        return fieldErrors;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public int getSavedRole(){
        return savedRole;
    }

    private MutableLiveData<Integer> loginState;

    public LiveData<Integer> getLoginState(){
        if(loginState == null){
            loginState = new MutableLiveData<>();
        }
        return loginState;
    }

    public void setFieldErrors(String errorEmail, String errorPass){
        String[] errorsTemp = {errorEmail, errorPass};
        fieldErrors.setValue(errorsTemp);
    }

    public void login(){
        loading.setValue(true);
        if(!email.getValue().equals("") && !password.getValue().equals("")){
            akunDB.loginPaw(email.getValue(), password.getValue()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                    if(results.size() > 0){
                        //dapat data dengan no hp dan password
                        if(results.get(0).getBoolean("penjual")){
                            String topikEmail = email.getValue().substring(0, email.getValue().indexOf('@'));
                            FirebaseMessaging.getInstance().subscribeToTopic(topikEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Akun akun = new Akun(email.getValue(), results.get(0).getString("nama"), password.getValue());
                                    setFieldErrors("", "");
                                    akunDB.saveLogAccount(akun);
                                    detailDB.getDBAkunDetail(akun.getEmail()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot doc) {
                                            loading.setValue(false);
                                            if(doc.getData() != null){
                                                DetailPenjual detail = new DetailPenjual(doc);
                                                savedRole = detail.getRole();
                                                loginState.setValue(1);
                                                detailDB.saveDetailFromLogin(detail);
                                            }
                                            else{
                                                loginState.setValue(0);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        else{
                            loading.setValue(false);
                            setFieldErrors("Anda Tidak Dapat Masuk Dengan Akun Pembeli", "Anda Tidak Dapat Masuk Dengan Akun Pembeli");
                        }
                    }
                    else{
                        loading.setValue(false);
                        setFieldErrors("Email/Password anda salah", "Email/Password anda salah");
                    }
                }
            });
        }
        else{
            loading.setValue(false);
            setFieldErrors("Email dan Password harus diisi", "Email dan Password harus diisi");
        }
    }

    public void checkAccounts(){
        loading.setValue(true);
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    //jika ada akun yang disimpan
                    detailDB.setGetDetailCompleteListener(new DetailPenjualDBAccess.onCompleteGetDetailListener() {
                        @Override
                        public void onDetailComplete(List<DetailPenjual> detailsGot) {
                            loading.setValue(false);
                            if(detailsGot.size() <= 0){
                                loginState.setValue(0);
                            }
                            else{
                                savedRole = detailsGot.get(0).getRole();
                                loginState.setValue(1);
                            }
                        }
                    });
                    detailDB.getLocalDetail();
                }
                else{
                    loading.setValue(false);
                }
            }
        });
        akunDB.getSavedAccounts();
    }

}
