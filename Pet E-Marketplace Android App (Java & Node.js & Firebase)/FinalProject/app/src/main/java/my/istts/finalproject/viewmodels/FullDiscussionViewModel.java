package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Comment;
import my.istts.finalproject.models.CommentDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.CommentItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FullDiscussionViewModel {
    private CommentDBAccess commentDB;
    private AkunDBAccess akunDB;
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;
    private Storage storage;
    private Application app;
    private BackendRetrofitService firebaseNotifService;
    
    public FullDiscussionViewModel(Application app){
        this.app = app;
        this.akunDB = new AkunDBAccess(app);
        this.commentDB = new CommentDBAccess();
        this.storage = new Storage();
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    private String id_item;
    private boolean isSeller;
    public MutableLiveData<String> komentar = new MutableLiveData<>("");
    
    private MutableLiveData<String> itemName = new MutableLiveData<>("");
    private MutableLiveData<String> itemPic = new MutableLiveData<>("");

    private MutableLiveData<String> pengomentarPic = new MutableLiveData<>("");
    private MutableLiveData<String> pengomentarName = new MutableLiveData<>("");
    private MutableLiveData<String> tanggalKomentar = new MutableLiveData<>("");
    private MutableLiveData<String> isiKomentar = new MutableLiveData<>("");

    private MutableLiveData<String> pageTitle = new MutableLiveData<>("");
    private MutableLiveData<String> targetNotify = new MutableLiveData<>("");

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> addLoading = new MutableLiveData<>();

    private MutableLiveData<ArrayList<CommentItemViewModel>> comments = new MutableLiveData<>();

    public void setKomentar(String id_komentar){
        isSeller = false;
        this.id_item = id_komentar;
        pageTitle.setValue("Balasan Komentar");
        commentDB.getCommentById(id_komentar).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Comment comment = new Comment(documentSnapshot);
                    targetNotify.setValue(comment.getemail_pengomentar());
                    tanggalKomentar.setValue(comment.getTanggal());
                    isiKomentar.setValue(comment.getTeks());

                    akunDB.getAccByEmail(comment.getemail_pengomentar()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                pengomentarName.setValue(documentSnapshot.getString("nama"));
                            }
                        }
                    });

                    storage.getPictureUrlFromName(comment.getemail_pengomentar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pengomentarPic.setValue(uri.toString());
                        }
                    });
                }
            }
        });
    }

    public void setProduk(String id_produk){
        isSeller = true;
        pageTitle.setValue("Diskusi Produk");
        this.id_item = id_produk;
        productDB.getProductById(id_produk).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Product product = new Product(documentSnapshot);
                    targetNotify.setValue(product.getemail_penjual());

                    storage.getPictureUrlFromName(product.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            itemPic.setValue(uri.toString());
                        }
                    });
                    itemName.setValue(product.getNama());
                }
            }
        });
    }

    public void setHotel(String id_hotel){
        isSeller = true;
        pageTitle.setValue("Diskusi Pet Hotel");
        this.id_item = id_hotel;
        hotelDB.getRoomById(id_hotel).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Hotel hotel = new Hotel(documentSnapshot);
                    targetNotify.setValue(hotel.getemail_pemilik());

                    storage.getPictureUrlFromName(hotel.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            itemPic.setValue(uri.toString());
                        }
                    });
                    itemName.setValue(hotel.getNama());
                }
            }
        });
    }

    public void getCommentsOfItem(String id_item){
        loading.setValue(true);
        comments.setValue(new ArrayList<>());

        commentDB.getAllCommments(id_item).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Comment> comments = new ArrayList<>();
                for (DocumentSnapshot commentDoc:
                        queryDocumentSnapshots.getDocuments()) {
                    comments.add(new Comment(commentDoc));
                }
                addCommentToVM(comments);
            }
        });
    }

    private void addCommentToVM(ArrayList<Comment> cmnts){
        ArrayList<CommentItemViewModel> currentComments = comments.getValue();
        for (Comment cmnt:
             cmnts) {
            currentComments.add(new CommentItemViewModel(cmnt, app));
        }
        comments.setValue(currentComments);
        loading.setValue(false);
    }

    public void sendComment(){
        addLoading.setValue(true);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    if(!targetNotify.getValue().equals(accountsGot.get(0).getEmail())){
                        String judul = "Komentarmu Mendapat Respon Baru";
                        if(isSeller){
                            judul = "Terdapat Pertanyaan Diskusi Baru";
                        }

                        //beri notifikasi hanya ketika menanyakan produk/kamar
                        String topikEmail = targetNotify.getValue().substring(0, targetNotify.getValue().indexOf('@'));
                        firebaseNotifService.sendNotif(judul, komentar.getValue(), topikEmail);
                    }
                    addCommentWithHP(accountsGot.get(0).getEmail());
                }
            }
        });

        akunDB.getSavedAccounts();

    }

    private void addCommentWithHP(String email){
        commentDB.addKomentar(id_item, email, komentar.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                komentar.setValue("");
                getCommentsOfItem(id_item);
                addLoading.setValue(false);
            }
        });
    }
    
    public LiveData<ArrayList<CommentItemViewModel>> getComments(){
        return comments;
    }

    public LiveData<Boolean> isAddLoading() {
        return addLoading;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getItemName() {
        return itemName;
    }

    public LiveData<String> getItemPic() {
        return itemPic;
    }

    public LiveData<String> getPengomentarPic() {
        return pengomentarPic;
    }

    public LiveData<String> getPengomentarName() {
        return pengomentarName;
    }

    public LiveData<String> getTanggalKomentar() {
        return tanggalKomentar;
    }

    public LiveData<String> getIsiKomentar() {
        return isiKomentar;
    }

    public LiveData<String> getPageTitle() {
        return pageTitle;
    }
}
