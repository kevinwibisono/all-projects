package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Cart;
import my.istts.finalproject.models.CartDBAccess;
import my.istts.finalproject.models.ChatConvDBAccess;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class AccountFragmentViewModel {
    private ChatConvDBAccess chatDB;
    private CartDBAccess cartDB;
    private FavoritDBAccess favDB;
    private AkunDBAccess akunDB;

    public AccountFragmentViewModel(Application app) {
        chatDB = new ChatConvDBAccess();
        cartDB = new CartDBAccess(app);
        akunDB = new AkunDBAccess(app);
        favDB = new FavoritDBAccess(app);
    }

    private String emailUser;
    private MutableLiveData<Boolean> canLogout = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> doneLogout = new MutableLiveData<>();
    private MutableLiveData<Boolean> loggingOut = new MutableLiveData<>();
    private MutableLiveData<String> userName = new MutableLiveData<>("");
    private MutableLiveData<String> userPic = new MutableLiveData<>("");
    private MutableLiveData<Integer> userSaldo = new MutableLiveData<>();
    private MutableLiveData<Boolean> itemsInCart = new MutableLiveData<>();
    private MutableLiveData<Boolean> unreadChats = new MutableLiveData<>();

    public void getAccountDetail(){
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();

                    searchForItemsInCart();
                    searchForUnreadChats(emailUser);
                    userName.setValue(accountsGot.get(0).getNama());

                    Storage storage = new Storage();
                    storage.getPictureUrlFromName(emailUser).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userPic.setValue(uri.toString());
                        }
                    });

                    akunDB.getAccByEmail(emailUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                userSaldo.setValue(documentSnapshot.getLong("saldo").intValue());
                                canLogout.setValue(true);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void searchForUnreadChats(String emailUser){
        chatDB.getUnreadChats(emailUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    unreadChats.setValue(true);
                }
                else{
                    unreadChats.setValue(false);
                }
            }
        });
    }

    private void searchForItemsInCart(){
        cartDB.setItemsGotListener(new CartDBAccess.onCartItemsGot() {
            @Override
            public void onGotItems(List<Cart> items) {
                if(items.size() > 0){
                    itemsInCart.setValue(true);
                }
                else{
                    itemsInCart.setValue(false);
                }
            }
        });

        cartDB.getAllCartItems();
    }

    public void logout(){
        canLogout.setValue(false);
        loggingOut.setValue(true);

        String topikEmail = emailUser.substring(0, emailUser.indexOf('@'));
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topikEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                akunDB.setClearedListener(new AkunDBAccess.onClearedListener() {
                    @Override
                    public void onCleared() {
                        cartDB.deleteAllCartItems();
                        favDB.deleteAllFavorite();

                        loggingOut.setValue(false);
                        doneLogout.setValue(true);
                    }
                });

                akunDB.clearAccounts();
            }
        });
    }

    public LiveData<Boolean> isAbleLogout() {
        return canLogout;
    }

    public LiveData<Boolean> isLoggingOut() {
        return loggingOut;
    }

    public LiveData<Boolean> isDoneLogout() {
        return doneLogout;
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserPic() {
        return userPic;
    }

    public LiveData<Integer> getUserSaldo() {
        return userSaldo;
    }

    public LiveData<Boolean> isItemsInCart() {
        return itemsInCart;
    }

    public LiveData<Boolean> thereisUnreadChats() {
        return unreadChats;
    }

    public String getEmailUser() {
        return emailUser;
    }
}
