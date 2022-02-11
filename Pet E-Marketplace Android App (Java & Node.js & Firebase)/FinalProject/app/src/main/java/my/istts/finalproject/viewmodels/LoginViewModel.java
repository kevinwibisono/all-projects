package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class LoginViewModel {

    private AkunDBAccess akunDB;

    public MutableLiveData<String> email = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    private MutableLiveData<String[]> fieldErrors;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> loggedOn = new MutableLiveData<>();

    public LoginViewModel(Application application) {
        akunDB = new AkunDBAccess(application);
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

    public LiveData<Boolean> isLoggedOn(){
        return loggedOn;
    }

    public void setFieldErrors(String errorHp, String errorPass){
        String[] errorsTemp = {errorHp, errorPass};
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
                            loading.setValue(false);
                            setFieldErrors("Anda Tidak Dapat Masuk Dengan Akun Penjual", "Anda Tidak Dapat Masuk Dengan Akun Penjual");
                        }
                        else{
                            String topikEmail = email.getValue().substring(0, email.getValue().indexOf('@'));
                            FirebaseMessaging.getInstance().subscribeToTopic(topikEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Akun akun = new Akun(email.getValue(), results.get(0).getString("nama"), password.getValue());
                                    setFieldErrors("", "");
                                    akunDB.saveLogAccount(akun);
                                    loading.setValue(false);
                                    loggedOn.setValue(true);
                                }
                            });
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
                    loading.setValue(false);
                    loggedOn.setValue(true);
                }
                else{
                    loading.setValue(false);
                }
            }
        });
        akunDB.getSavedAccounts();
    }

}
