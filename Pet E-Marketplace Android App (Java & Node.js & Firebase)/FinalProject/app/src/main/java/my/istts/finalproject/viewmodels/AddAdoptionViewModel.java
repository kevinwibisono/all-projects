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
import my.istts.finalproject.models.Adoption;
import my.istts.finalproject.models.AdoptionDBAccess;
import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public class AddAdoptionViewModel {
    private AdoptionDBAccess adoptDB;
    private AkunDBAccess akunDB;
    private Application app;

    public AddAdoptionViewModel(Application app){
        this.app = app;
        adoptDB = new AdoptionDBAccess();
        akunDB = new AkunDBAccess(app);
    }

    private MutableLiveData<Bitmap> picture = new MutableLiveData<>();
    public MutableLiveData<String> nama = new MutableLiveData<>("");
    public MutableLiveData<String> umur = new MutableLiveData<>("");
    private MutableLiveData<Integer> jenis_hewan = new MutableLiveData<>(0);
    private MutableLiveData<Integer> satuan_umur = new MutableLiveData<>(0);
    public MutableLiveData<String> ras = new MutableLiveData<>("");
    private MutableLiveData<Integer> jenis_kelamin = new MutableLiveData<>(0);
    public MutableLiveData<String> deskripsi = new MutableLiveData<>("");

    public MutableLiveData<String> updatingAdoption = new MutableLiveData<>("");
    public MutableLiveData<String> loadingTitle = new MutableLiveData<>("Proses Menambahkan...");

    private MutableLiveData<Integer> focusedNum = new MutableLiveData<>();
    private MutableLiveData<String[]> errors = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> doneAdd = new MutableLiveData<>();

    public void setPicture(Bitmap picture) {
        this.picture.setValue(picture);
    }

    public void setSatuan_umur(int satuan_umur) {
        this.satuan_umur.setValue(satuan_umur);
    }

    public void setJenis_hewan(int jenis_hewan) {
        this.jenis_hewan.setValue(jenis_hewan);
    }

    public void setJenis_kelamin(int jenis_kelamin) {
        this.jenis_kelamin.setValue(jenis_kelamin);
    }

    public void getUpdatingAdoption(String id_adopsi){
        updatingAdoption.setValue(id_adopsi);
        loadingTitle.setValue("Proses Mengubah...");

        adoptDB.getPetAdoptById(id_adopsi).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Adoption adoption = new Adoption(documentSnapshot);
                nama.setValue(adoption.getNama());
                umur.setValue(String.valueOf(adoption.getUmur()));
                satuan_umur.setValue(adoption.getSatuan_umur());
                jenis_hewan.setValue(adoption.getJenis());
                ras.setValue(adoption.getRas());
                jenis_kelamin.setValue(adoption.getJenis_kelamin());
                deskripsi.setValue(adoption.getDeskripsi());

                Storage storage = new Storage();
                storage.getPictureUrlFromName(adoption.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(app)
                                .asBitmap()
                                .load(uri.toString())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        picture.setValue(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                });


            }
        });
    }

    public void beginAddPetAdopt(){
        if(picture.getValue() == null){
            errors.setValue(new String[]{"Mohon Pilih Gambar", "", "", "", ""});
            focusedNum.setValue(0);
        }
        else if(nama.getValue().equals("")){
            errors.setValue(new String[]{"", "Nama Tidak Dapat Kosong", "", "", ""});
            focusedNum.setValue(1);
        }
        else if(umur.getValue().equals("") || Integer.parseInt(umur.getValue()) < 1){
            errors.setValue(new String[]{"", "", "Umur Minimal Bernilai 1", "", ""});
            focusedNum.setValue(2);
        }
        else if(ras.getValue().equals("")){
            errors.setValue(new String[]{"", "", "", "Ras/Jenis Hewan Tidak Dapat Kosong", ""});
            focusedNum.setValue(3);
        }
        else {
            errors.setValue(new String[]{"", "", "", "", ""});
            uploadPetPicture();
        }
    }

    private void uploadPetPicture(){
        loading.setValue(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.getValue().compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        Storage storage = new Storage();
        storage.uploadPicture(data, UUID.nameUUIDFromBytes(data).toString()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //set random numbers untuk verify
                addPetAdopt(UUID.nameUUIDFromBytes(data).toString());
            }
        });
    }

    private void addPetAdopt(String pictureName){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    if(updatingAdoption.getValue().equals("")){
                        adoptDB.addPetAdoption(accountsGot.get(0).getEmail(), pictureName, nama.getValue(), umur.getValue(), satuan_umur.getValue(), jenis_hewan.getValue(),
                                ras.getValue(), jenis_kelamin.getValue(), deskripsi.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(app, "Berhasil Menambahkan Hewan Adopsi", Toast.LENGTH_SHORT).show();
                                doneAdd.setValue(true);
                                loading.setValue(false);
                            }
                        });
                    }
                    else{
                        adoptDB.updatePetAdoption(updatingAdoption.getValue(), accountsGot.get(0).getEmail(), pictureName, nama.getValue(), umur.getValue(), satuan_umur.getValue(), jenis_hewan.getValue(),
                                ras.getValue(), jenis_kelamin.getValue(), deskripsi.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(app, "Berhasil Merubah Hewan Adopsi", Toast.LENGTH_SHORT).show();
                                doneAdd.setValue(true);
                                loading.setValue(false);
                            }
                        });
                    }

                }
            }
        });

        akunDB.getSavedAccounts();

    }

    public LiveData<Integer> getFocusedNum() {
        return focusedNum;
    }

    public LiveData<String[]> getErrors() {
        return errors;
    }

    public LiveData<String> getLoadingTitle() {
        return loadingTitle;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> isDoneAdding() {
        return doneAdd;
    }

    public LiveData<Bitmap> getPicture() {
        return picture;
    }

    public LiveData<Integer> getSatuan_umur() {
        return satuan_umur;
    }

    public LiveData<Integer> getJenis_hewan() {
        return jenis_hewan;
    }

    public LiveData<Integer> getJenis_kelamin() {
        return jenis_kelamin;
    }
}
