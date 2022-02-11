package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.FollowDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.SellerItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FollowViewModel {
    private FollowDBAccess followDB;
    private AkunDBAccess akunDB;

    public FollowViewModel(Application app){
        followDB = new FollowDBAccess();
        akunDB = new AkunDBAccess(app);
    }

    private String email;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<SellerItemViewModel>> followedSellers = new MutableLiveData<>();

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<ArrayList<SellerItemViewModel>> getFollowedSellers() {
        return followedSellers;
    }

    public void getFollowed(int tipe){
        loading.setValue(true);
        followedSellers.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    followDB.getMyFollowing(accountsGot.get(0).getEmail(), tipe).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                getSellersFromDB(queryDocumentSnapshots.getDocuments());
                            }
                            else{
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void getSellersFromDB(List<DocumentSnapshot> docs){
        ArrayList<Integer> counterDone = new ArrayList<>();

        for (DocumentSnapshot followDoc:
                docs) {
            akunDB.getAccByEmail(followDoc.getString("email_penjual")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        addSellerToVM(followDoc.getString("email_penjual"), documentSnapshot.getString("nama"));
                    }
                    counterDone.add(0);
                    if(counterDone.size() == docs.size()){
                        loading.setValue(false);
                    }
                }
            });
        }
    }

    private void addSellerToVM(String email, String name){
        ArrayList<SellerItemViewModel> currentSeller = followedSellers.getValue();
        currentSeller.add(new SellerItemViewModel(email, name));
        followedSellers.setValue(currentSeller);
    }

    public void unfollowSeller(String email_seller, int tipe){
        loading.setValue(true);

        followDB.getIsFollowing(email, email_seller).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    DocumentSnapshot followDoc = queryDocumentSnapshots.getDocuments().get(0);

                    followDB.unfollowSeller(followDoc.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loading.setValue(false);
                            getFollowed(tipe);
                        }
                    });
                }
            }
        });
    }
}
