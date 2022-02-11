package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.GiveReviewItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private ReviewDBAccess reviewDB;
    private AkunDBAccess akunDB;
    private PesananJanjitemu pesananJanjitemu;

    public ReviewViewModel(Application app){
        this.orderDB = new PesananJanjitemuDBAccess();
        this.reviewDB = new ReviewDBAccess();
        this.akunDB = new AkunDBAccess(app);
    }

    public void getOrderDetail(String id_pj){
        revItemsLoading.setValue(true);
        reviewItems.setValue(new ArrayList<>());

        orderDB.getPJById(id_pj).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                    pesananJanjitemu = pj;
                    if(pj.getJenis() % 2 == 0){
                        //shopping & hotel pj
                        ArrayList<ItemPesananJanjitemu> itemPJs = new ArrayList<>();
                        orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    for (DocumentSnapshot itemDoc:
                                         queryDocumentSnapshots.getDocuments()) {
                                        itemPJs.add(new ItemPesananJanjitemu(itemDoc));
                                    }
                                    addReviewItem("", itemPJs);
                                }
                                else{
                                    revItemsLoading.setValue(false);
                                }
                            }
                        });
                    }
                    else{
                        addReviewItem(pj.getemail_penjual(), null);
                    }
                }
            }
        });
    }

    private void addReviewItem(String email_seller, ArrayList<ItemPesananJanjitemu> itemPJs){
        if(itemPJs != null){
            ArrayList<GiveReviewItemViewModel> currentRevs = reviewItems.getValue();
            for (ItemPesananJanjitemu itemPj:
                 itemPJs) {
                currentRevs.add(new GiveReviewItemViewModel(itemPj, ""));
            }
            reviewItems.setValue(currentRevs);
        }
        else{
            ArrayList<GiveReviewItemViewModel> currentRevs = reviewItems.getValue();
            currentRevs.add(new GiveReviewItemViewModel(null, email_seller));
            reviewItems.setValue(currentRevs);
        }
        revItemsLoading.setValue(false);
    }

    public void addReview(){
        loading.setValue(true);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    String email = accountsGot.get(0).getEmail();

                    ArrayList<Integer> counterDone = new ArrayList<>();
                    for (GiveReviewItemViewModel review:
                            reviewItems.getValue()) {
                        reviewDB.addReview(review.getReviewNum().getValue(), review.reviewIsi.getValue(),
                                review.getItemId().getValue(), email).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                counterDone.add(0);
                                if(counterDone.size() == reviewItems.getValue().size()){
                                    orderDB.giveReview(pesananJanjitemu.getId_pj(), pesananJanjitemu.getJenis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loading.setValue(false);
                                            doneReview.setValue(true);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });

        akunDB.getSavedAccounts();

    }

    private MutableLiveData<ArrayList<GiveReviewItemViewModel>> reviewItems = new MutableLiveData<>();
    private MutableLiveData<Boolean> revItemsLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> doneReview = new MutableLiveData<>();

    public LiveData<ArrayList<GiveReviewItemViewModel>> getReviewItems(){
        return reviewItems;
    }

    public LiveData<Boolean> isRevItemLoading(){
        return revItemsLoading;
    }

    public LiveData<Boolean> isLoading(){
        return loading;
    }

    public LiveData<Boolean> isDoneReview(){
        return doneReview;
    }
}
