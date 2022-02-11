package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.inputclasses.RegisterInput;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.backend.BackendRetrofitClient;
import com.example.sellerapp.models.backend.BackendRetrofitService;
import com.example.sellerapp.models.backend.SendVerifResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel {
    private AkunDBAccess model;
    private BackendRetrofitService backendService;

    public RegisterInput inputs = new RegisterInput();

    private MutableLiveData<Boolean> loading;
    private MutableLiveData<Boolean> validationsSuccess;
    private MutableLiveData<Boolean> registerSuccess;
    private MutableLiveData<Boolean> resendable;
    private int focusedNumber;
    private int code;
    private Application app;
    private MutableLiveData<String[]> fieldErrors;

    public RegisterViewModel(Application app){
        this.app = app;
        model = new AkunDBAccess(app);
        backendService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    public LiveData<Boolean> isLoading(){
        if(loading == null){
            loading = new MutableLiveData<>();
        }
        return loading;
    }

    public LiveData<Boolean> canResend(){
        if(resendable == null){
            resendable = new MutableLiveData<>(true);
        }
        return resendable;
    }

    public LiveData<Boolean> isValid(){
        if(validationsSuccess == null){
            validationsSuccess = new MutableLiveData<>();
        }
        return validationsSuccess;
    }

    public LiveData<Boolean> isRegistered(){
        if(registerSuccess == null){
            registerSuccess = new MutableLiveData<>();
        }
        return registerSuccess;
    }

    public LiveData<String[]> getFieldErrors(){
        if(fieldErrors == null){
            fieldErrors = new MutableLiveData<>();
        }
        return fieldErrors;
    }

    public int getFocusedNumber(){
        return focusedNumber;
    }

    private MutableLiveData<Integer> countDown = new MutableLiveData<>(59);
    private MutableLiveData<String> verificationError;
    public MutableLiveData<String> verificationInputs = new MutableLiveData<>("");

    public void reduceCountDown(){
        Integer current = countDown.getValue();
        if(current >= 1){
            countDown.setValue(current - 1);
        }
    }

    public LiveData<String> verError(){
        if(verificationError == null){
            verificationError = new MutableLiveData<>();
        }
        return verificationError;
    }

    public LiveData<Integer> getCountDown(){
        return countDown;
    }

    public void register(){
        //saat register, ada 3 syarat
        loading.setValue(true);
        if(inputs.isEmpty()){
            focusedNumber = 0;
            fieldErrors.setValue(new String[]{"Semua Isian Wajib Diisi", "Semua Isian Wajib Diisi", ""});
            loading.setValue(false);
        }
        else if(inputs.confirmInvalid()){
            focusedNumber = 2;
            fieldErrors.setValue(new String[]{"", "Password & Konfirmasi Harus Sama", "Password & Konfirmasi Harus Sama"});
            loading.setValue(false);
        }
        else{
            model.checkEmail(inputs.email.getValue()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                        //sudah dipakai
                        focusedNumber = 0;
                        fieldErrors.setValue(new String[]{"Email Sudah Digunakan", "", ""});
                        loading.setValue(false);
                    }
                    else{
                        fieldErrors.setValue(new String[]{"", "", ""});
                        sendOTPNow();
                        loading.setValue(false);
                        validationsSuccess.setValue(true);
                    }
                }
            });
        }
    }

    public void verify(){
        if(verificationInputs.getValue().equals(String.valueOf(code))){
            verificationError.setValue("");
            loading.setValue(true);
            model.register(inputs.email.getValue(), inputs.password.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String topikEmail = inputs.email.getValue().substring(0, inputs.email.getValue().indexOf('@'));
                    FirebaseMessaging.getInstance().subscribeToTopic(topikEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loading.setValue(false);
                            registerSuccess.setValue(true);
                            model.clearAccounts();
                        }
                    });
                }
            });
        }
        else{
            verificationError.setValue("Nomor Verifikasi Salah");
        }
    }

    public void sendOTPNow(){
        code = (int) Math.round(Math.random()*1000000);
        backendService.sendVerification(inputs.email.getValue(), String.valueOf(code)).enqueue(new Callback<SendVerifResponse>() {
            @Override
            public void onResponse(Call<SendVerifResponse> call, Response<SendVerifResponse> response) {

            }

            @Override
            public void onFailure(Call<SendVerifResponse> call, Throwable t) {

            }
        });
        countDown.setValue(59);
    }
}
