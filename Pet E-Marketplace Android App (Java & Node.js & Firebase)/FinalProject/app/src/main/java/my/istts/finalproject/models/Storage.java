package my.istts.finalproject.models;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Storage {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    public UploadTask uploadPicture(byte[] imageViewBytes, String pictureName){
        storageRef = storage.getReference().child(pictureName+".jpg");
        UploadTask uploadTask = storageRef.putBytes(imageViewBytes);
        return uploadTask;
    }

//    public void uploadPictureUrl(String pictureName){
//        storageRef = storage.getReference().child(pictureName+".jpg");
//    }

    public Task<Uri> getPictureUrlFromName(String pictureName){
        storageRef = storage.getReference().child(pictureName+".jpg");
        return storageRef.getDownloadUrl();
    }

    public void deletePicture(String pictureName){
        firebaseDb.collection("gambar").document(pictureName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                StorageReference storageRef = storage.getReference().child(pictureName+".jpg");
                storageRef.delete();
            }
        });
    }
}
