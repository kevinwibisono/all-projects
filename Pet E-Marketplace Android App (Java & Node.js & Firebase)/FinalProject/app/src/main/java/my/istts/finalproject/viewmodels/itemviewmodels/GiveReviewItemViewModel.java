package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.DetailPenjualDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class GiveReviewItemViewModel {

    public GiveReviewItemViewModel(ItemPesananJanjitemu itemPJ, String hp_seller){
        if(itemPJ != null){
            itemId.setValue(itemPJ.getId_item());
            itemName.setValue(itemPJ.getNama());
            itemPic.setValue(itemPJ.getGambar());
        }
        else{
            AkunDBAccess akunDB = new AkunDBAccess();
            akunDB.getAccByEmail(hp_seller).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        Storage storage = new Storage();
                        storage.getPictureUrlFromName(hp_seller).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                itemId.setValue(hp_seller);
                                itemPic.setValue(uri.toString());
                                itemName.setValue(documentSnapshot.getString("nama"));
                            }
                        });
                    }
                }
            });
        }
    }

    private MutableLiveData<String> itemId = new MutableLiveData<>("");
    private MutableLiveData<String> itemName = new MutableLiveData<>("");
    private MutableLiveData<String> itemPic = new MutableLiveData<>();

    public LiveData<String> getItemId() {
        return itemId;
    }

    public LiveData<String> getItemName() {
        return itemName;
    }

    public LiveData<String> getItemPic() {
        return itemPic;
    }

    public MutableLiveData<String> reviewIsi = new MutableLiveData<>("");
    private MutableLiveData<Integer> reviewNum = new MutableLiveData<>(5);

    public LiveData<Integer> getReviewNum(){
        return reviewNum;
    }

    public void setReviewNum(int reviewNum){
        this.reviewNum.setValue(reviewNum);
    }
}
