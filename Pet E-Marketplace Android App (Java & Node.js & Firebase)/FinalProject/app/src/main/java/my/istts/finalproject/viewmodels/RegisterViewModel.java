package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.inputclasses.RegisterInput;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.SendVerifResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel {
    private AkunDBAccess akunDB;
    private BackendRetrofitService backendService;
    private Storage storage;

    public RegisterInput inputs = new RegisterInput();

    private MutableLiveData<Boolean> loading;
    private MutableLiveData<Boolean> validationsSuccess;
    private MutableLiveData<Boolean> registerSuccess;
    private MutableLiveData<Bitmap> uploadedImg = new MutableLiveData<>();
    private MutableLiveData<String> loadingTitle = new MutableLiveData<>("Proses Registrasi...");
    private int focusedNumber;
    private int code;
    private Application app;
    private MutableLiveData<String[]> fieldErrors;
    private MutableLiveData<String> pictureError;

    public RegisterViewModel(Application app){
        this.app = app;
        akunDB = new AkunDBAccess(app);
        storage = new Storage();
        backendService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    public void setUploadedImg(Bitmap uploadedImg){
        this.uploadedImg.setValue(uploadedImg);
    }

    public LiveData<Bitmap> getUploadedImg() {
        return uploadedImg;
    }

    public LiveData<Boolean> isLoading(){
        if(loading == null){
            loading = new MutableLiveData<>();
        }
        return loading;
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

    public LiveData<Boolean> isUpdating(){
        if(updating == null){
            updating = new MutableLiveData<>();
        }
        return updating;
    }

    public LiveData<String[]> getFieldErrors(){
        if(fieldErrors == null){
            fieldErrors = new MutableLiveData<>();
        }
        return fieldErrors;
    }

    public LiveData<String> getPictureError(){
        if(pictureError == null){
            pictureError = new MutableLiveData<>();
        }
        return pictureError;
    }
    public int getFocusedNumber(){
        return focusedNumber;
    }

    private void setFieldErrors(String errPhone, String errName, String errPass, String errConfirm){
        pictureError.setValue("");
        String[] errorTemp = {errPhone, errName, errPass, errConfirm};
        fieldErrors.setValue(errorTemp);
    }

    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
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

    public LiveData<String> getLoadingTitle(){
        return loadingTitle;
    }

    public LiveData<Integer> getCountDown(){
        return countDown;
    }

    public void prepareUpdate(){
        loadingTitle.setValue("Proses Pengubahan...");
        updating.setValue(true);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    inputs.setEmail(accountsGot.get(0).getEmail());
                    inputs.setNama(accountsGot.get(0).getNama());
                    inputs.setPassword(accountsGot.get(0).getPassword());

                    storage.getPictureUrlFromName(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(app).asBitmap().load(uri).into(new CustomTarget<Bitmap>(){
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    uploadedImg.setValue(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    public void register(){
        //saat register, ada 3 syarat
        //1. semua field terisi
        //2. password dan confirm password sama
        //3. no hp belum dipakai
        //4. email belum dipakai
        loading.setValue(true);
        if(inputs.isEmpty()){
            loading.setValue(false);
            focusedNumber = 0;
            setFieldErrors("Semua isian wajib diisi", "Semua isian wajib diisi", "Semua isian wajib diisi", "");
        }
        else if(inputs.confirmInvalid()){
            loading.setValue(false);
            focusedNumber = 3;
            setFieldErrors("", "", "Password dan Konfirmasi harus sama", "Password dan Konfirmasi harus sama");
        }
        else if(uploadedImg.getValue() == null){
            loading.setValue(false);
            focusedNumber = -1;
            setFieldErrors("","", "", "");
            pictureError.setValue("Mohon pilih gambar profil");
        }
        else{
            if(updating.getValue()){
                uploadPicture(uploadedImg);
            }
            else{
                akunDB.checkEmail(inputs.email.getValue()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                        if(results.size() > 0){
                            focusedNumber = 0;
                            setFieldErrors("Email sudah digunakan", "", "", "");
                        }
                        else{
                            focusedNumber = -1;
                            setFieldErrors("", "", "", "");
                            validationsSuccess.setValue(true);
                            sendOTPNow();
                        }
                        loading.setValue(false);
                    }
                });
            }
        }
    }

    public void uploadPicture(MutableLiveData<Bitmap> bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Objects.requireNonNull(bitmap.getValue()).compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        storage.uploadPicture(data, inputs.email.getValue()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                akunDB.register(inputs.email.getValue(), inputs.nama.getValue(), inputs.password.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String topikEmail = inputs.email.getValue().substring(0, inputs.email.getValue().indexOf('@'));
                        FirebaseMessaging.getInstance().subscribeToTopic(topikEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loading.setValue(false);
                                registerSuccess.setValue(true);
                                akunDB.clearAccounts();
                            }
                        });
                    }
                });
            }
        });
    }

    public void verify(){
        if(verificationInputs.getValue().equals(String.valueOf(code))){
            verificationError.setValue("");
            loading.setValue(true);
            uploadPicture(uploadedImg);
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
