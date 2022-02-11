package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.DiskusiDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public class AddDiscussViewModel {
    private DiskusiDBAccess discussDB;
    private AkunDBAccess akunDB;
    private Application app;

    public AddDiscussViewModel(Application app){
        this.app = app;
        discussDB = new DiskusiDBAccess();
        akunDB = new AkunDBAccess(app);
    }

    private Bitmap picture;
    private int jenisHewan = 0;
    public MutableLiveData<String> judul = new MutableLiveData<>("");
    public MutableLiveData<String> isi = new MutableLiveData<>("");

    private MutableLiveData<Integer> focusedNum = new MutableLiveData<>();
    private MutableLiveData<String[]> errors = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> doneAdd = new MutableLiveData<>();

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public void setJenisHewan(int jenisHewan) {
        this.jenisHewan = jenisHewan;
    }

    public void beginAddDiscuss(){
        if(picture == null){
            errors.setValue(new String[]{"Mohon Pilih Gambar", "", ""});
            focusedNum.setValue(0);
        }
        else if(judul.getValue().equals("")){
            errors.setValue(new String[]{"", "Pertanyaan Tidak Dapat Kosong", ""});
            focusedNum.setValue(1);
        }
        else if(isi.getValue().equals("")){
            errors.setValue(new String[]{"", "", "Deskripsi Pertanyaan Tidak Dapat Kosong"});
            focusedNum.setValue(2);
        }
        else {
            errors.setValue(new String[]{"", "", ""});
            uploadPetDiscussPic();
        }
    }

    private void uploadPetDiscussPic(){
        loading.setValue(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        Storage storage = new Storage();
        storage.uploadPicture(data, UUID.nameUUIDFromBytes(data).toString()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //set random numbers untuk verify
                addPetDiscuss(UUID.nameUUIDFromBytes(data).toString());
            }
        });
    }

    private void addPetDiscuss(String pictureName){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    discussDB.addDiscussion(accountsGot.get(0).getEmail(), pictureName, judul.getValue(), isi.getValue(), jenisHewan).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            loading.setValue(false);
                            doneAdd.setValue(true);
                        }
                    });

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

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> isDoneAdding() {
        return doneAdd;
    }

    public Bitmap getPicture() {
        return picture;
    }
}
