package com.example.sellerapp.viewmodels.itemviewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.ChatConvDBAccess;
import com.example.sellerapp.models.Conversation;
import com.example.sellerapp.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ConversationItemViewModel extends ViewModel {
    private Conversation conv;

    public ConversationItemViewModel(Conversation conv, Application app) {
        this.conv = conv;
        Storage storage = new Storage();
        ChatConvDBAccess chatDB = new ChatConvDBAccess();
        AkunDBAccess akunDB = new AkunDBAccess(app);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    String email_friend;
                    String email = accountsGot.get(0).getEmail();
                    if(conv.getemail_pengirim().equals(email)){
                        email_friend = conv.getemail_penerima();
                    }
                    else{
                        email_friend = conv.getemail_pengirim();
                    }
                    akunDB.getAccByEmail(email_friend).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                nama.setValue(documentSnapshot.getString("nama"));
                                storage.getPictureUrlFromName(email_friend).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        picture.setValue(uri.toString());
                                    }
                                });
                            }

                        }
                    });
                    chatDB.getLatestChat(conv.getId_chatroom()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value != null  && value.getDocuments().size() > 0){
                                DocumentSnapshot latestChat = value.getDocuments().get(0);
                                if(latestChat.getString("email_penerima").equals(email) && !latestChat.getBoolean("dibaca")){
                                    //punya pembaca dan belum dibaca => urgent
                                    urgent.setValue(true);
                                }
                                else{
                                    urgent.setValue(false);
                                }
                                last_chat.setValue(latestChat.getString("teks"));
                                last_timestamp.setValue(latestChat.getTimestamp("waktu"));
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private MutableLiveData<String> picture = new MutableLiveData<>("");
    private MutableLiveData<String> last_chat = new MutableLiveData<>("");
    private MutableLiveData<Timestamp> last_timestamp = new MutableLiveData<>();
    private MutableLiveData<Boolean> urgent = new MutableLiveData<>();
    private MutableLiveData<String> nama = new MutableLiveData<>("");

    public String getID(){
        return conv.getId_chatroom();
    }

    public LiveData<String> getMessage(){
        return last_chat;
    }

    public LiveData<Boolean> isUrgent(){
        return urgent;
    }

    public LiveData<String> getPicture(){
        return picture;
    }

    public LiveData<String> getNama(){
        return nama;
    }
}
