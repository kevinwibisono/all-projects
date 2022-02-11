package com.example.sellerapp.models;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Storage {
    private com.google.firebase.storage.FirebaseStorage storage = com.google.firebase.storage.FirebaseStorage.getInstance();
    private StorageReference storageRef;

    public UploadTask uploadPicture(byte[] imageViewBytes, String pictureName){
        storageRef = storage.getReference().child(pictureName+".jpg");
        UploadTask uploadTask = storageRef.putBytes(imageViewBytes);
        return uploadTask;
    }

    public void uploadPictureUrl(String pictureName){
        storageRef = storage.getReference().child(pictureName+".jpg");
    }

    public Task<Uri> getPictureUrlFromName(String pictureName){
        storageRef = storage.getReference().child(pictureName+".jpg");
        return storageRef.getDownloadUrl();
    }

//    public void deletePicture(String pictureName){
//        firebaseDb.collection("gambar").document(pictureName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                StorageReference storageRef = storage.getReference().child(pictureName+".jpg");
//                storageRef.delete();
//            }
//        });
//    }
}
