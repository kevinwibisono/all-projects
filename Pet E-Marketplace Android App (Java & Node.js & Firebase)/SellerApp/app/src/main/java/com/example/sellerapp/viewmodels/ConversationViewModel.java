package com.example.sellerapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.ChatConvDBAccess;
import com.example.sellerapp.models.Conversation;
import com.example.sellerapp.viewmodels.itemviewmodels.ConversationItemViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConversationViewModel extends AndroidViewModel {

    private AkunDBAccess akunRepos;
    private ChatConvDBAccess chatDB;
    private Application app;

    public ConversationViewModel(@NonNull Application application) {
        super(application);
        app = application;
        akunRepos = new AkunDBAccess(application);
        chatDB = new ChatConvDBAccess();
    }

    private MutableLiveData<ArrayList<ConversationItemViewModel>> convsVM;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LiveData<ArrayList<ConversationItemViewModel>> getConvsVM(){
        if(convsVM == null){
            convsVM = new MutableLiveData<>(new ArrayList<>());
        }
        return convsVM;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public void getConversations(){
        loading.setValue(true);
        akunRepos.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    chatDB.getAllConversations().addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            convsVM.setValue(new ArrayList<>());
                            ArrayList<Conversation> dbConvs = new ArrayList<>();
                            List<DocumentSnapshot> convs = value.getDocuments();
                            if(convs.size() > 0){
                                for (int i = 0; i < convs.size(); i++){
                                    Conversation conv = new Conversation(convs.get(i));
                                    if(conv.getemail_penerima().equals(accountsGot.get(0).getEmail()) || conv.getemail_pengirim().equals(accountsGot.get(0).getEmail())){
                                        dbConvs.add(conv);
                                    }
                                }
                                addConversationToLiveData(dbConvs);
                            }
                            else{
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }
        });

        akunRepos.getSavedAccounts();
    }

    private void addConversationToLiveData(ArrayList<Conversation> convs){
        ArrayList<ConversationItemViewModel> currentVMs = convsVM.getValue();
        for (Conversation conv:
             convs) {
            currentVMs.add(new ConversationItemViewModel(conv, app));
        }
        convsVM.setValue(currentVMs);
        loading.setValue(false);
    }

}
