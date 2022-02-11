package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Comment;
import my.istts.finalproject.models.CommentDBAccess;
import my.istts.finalproject.models.Diskusi;
import my.istts.finalproject.models.DiskusiDBAccess;
import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.SendNotifResponse;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.CommentItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussDetailViewModel {
    private DiskusiDBAccess discussDB;
    private AkunDBAccess akunDB;
    private FavoritDBAccess favDB;
    private CommentDBAccess commentDB;
    private Application app;
    private BackendRetrofitService firebaseNotifService;

    public DiscussDetailViewModel(Application app){
        this.app = app;
        this.discussDB = new DiskusiDBAccess();
        this.akunDB = new AkunDBAccess(app);
        this.favDB = new FavoritDBAccess(app);
        this.commentDB = new CommentDBAccess();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    private String id_discuss, emailUser;
    private Favorit favorit;
    private MutableLiveData<ArrayList<CommentItemViewModel>> comments = new MutableLiveData<>();
    private MutableLiveData<Boolean> commentsLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> addCommentsLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> commentEnabled = new MutableLiveData<>(false);

    private MutableLiveData<Boolean> favoritEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> favorited = new MutableLiveData<>();

    private MutableLiveData<String> targetNotify = new MutableLiveData<>("");

    private MutableLiveData<String> penanya = new MutableLiveData<>("");
    private MutableLiveData<String> tanggal = new MutableLiveData<>("");
    private MutableLiveData<String> judul = new MutableLiveData<>("");
    private MutableLiveData<String> gambar = new MutableLiveData<>();
    private MutableLiveData<String> isi = new MutableLiveData<>("");
    private MutableLiveData<Integer> like = new MutableLiveData<>(0);

    public MutableLiveData<String> komentar = new MutableLiveData<>("");

    public void setDiscussDetail(String id_discuss){
        this.id_discuss = id_discuss;
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();

                    getDiscussComments(id_discuss);

                    discussDB.getDiscussionById(id_discuss).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                Diskusi diskusi = new Diskusi(documentSnapshot);
                                targetNotify.setValue(diskusi.getemail_penanya());
                                commentEnabled.setValue(true);

                                akunDB.getAccByEmail(diskusi.getemail_penanya()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.getData() != null){
                                            penanya.setValue(documentSnapshot.getString("nama"));
                                        }
                                    }
                                });

                                Storage storage = new Storage();
                                storage.getPictureUrlFromName(diskusi.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        gambar.setValue(uri.toString());
                                    }
                                });

                                judul.setValue(diskusi.getJudul());
                                tanggal.setValue(diskusi.getTanggal());
                                isi.setValue(diskusi.getIsi());
                                like.setValue(diskusi.getLike());

                                favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                                    @Override
                                    public void onGot(List<Favorit> favs) {
                                        if(favs.size() > 0){
                                            favorit = favs.get(0);
                                            favorited.setValue(true);
                                        }
                                        else{
                                            favorited.setValue(false);
                                        }
                                        favoritEnabled.setValue(true);
                                    }
                                });

                                favDB.getFavorit(emailUser, diskusi.getId_diskusi());
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();


    }

    public void setFavorite(){
        favoritEnabled.setValue(false);
        if(favorit != null){
            //skrg di unfavoritkan
            favDB.setFavoriteDeletedListener(new FavoritDBAccess.onFavoriteDeleted() {
                @Override
                public void onDeleted() {
                    favoritEnabled.setValue(true);
                    favorited.setValue(false);
                    favorit = null;
                    discussDB.incDiscussLike(id_discuss, -1);
                    like.setValue(like.getValue() - 1);
                }
            });

            favDB.deleteFavorite(favorit.getId());
        }
        else{
            //skrg difavoritkan
            //untuk menghindari kemungkinan addfavorit double, maka sebelum add cek apakah sudah ada di db
            favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                @Override
                public void onGot(List<Favorit> favs) {
                    if(favs.size() > 0){
                        favoritEnabled.setValue(true);
                        favorited.setValue(true);
                        favorit = favs.get(0);
                        discussDB.incDiscussLike(id_discuss, 1);
                        like.setValue(like.getValue() + 1);
                    }
                    else{
                        favorit = new Favorit(emailUser, id_discuss, 4);
                        favDB.setFavoriteAddedListener(new FavoritDBAccess.onFavoriteAdded() {
                            @Override
                            public void onAdded() {
                                favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
                                    @Override
                                    public void onGot(List<Favorit> favs) {
                                        if(favs.size() > 0){
                                            favorit = favs.get(0);
                                            favoritEnabled.setValue(true);
                                            favorited.setValue(true);
                                            discussDB.incDiscussLike(id_discuss, 1);
                                            like.setValue(like.getValue() + 1);
                                        }
                                    }
                                });

                                favDB.getFavorit(emailUser, id_discuss);
                            }
                        });

                        favDB.addFavorit(favorit);
                    }
                }
            });

            favDB.getFavorit(emailUser, id_discuss);
        }
    }

    public void sendComment(){
        commentEnabled.setValue(false);
        addCommentsLoading.setValue(true);

        firebaseNotifService.sendNotif("Diskusmu Mendapat Komentar Baru", "Pengguna Lain Memberikan Pendapatnya pad Diskusi yang kamu buat", targetNotify.getValue()).enqueue(new Callback<SendNotifResponse>() {
            @Override
            public void onResponse(Call<SendNotifResponse> call, Response<SendNotifResponse> response) {
                commentDB.addKomentar(id_discuss, emailUser, komentar.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        komentar.setValue("");
                        getDiscussComments(id_discuss);
                        addCommentsLoading.setValue(false);
                        commentEnabled.setValue(true);
                    }
                });
            }

            @Override
            public void onFailure(Call<SendNotifResponse> call, Throwable t) {

            }
        });
    }

    private void getDiscussComments(String id_discuss){
        commentsLoading.setValue(true);
        comments.setValue(new ArrayList<>());

        commentDB.getAllCommments(id_discuss).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Comment> cmnts = new ArrayList<>();
                for (DocumentSnapshot commentDoc:
                        queryDocumentSnapshots.getDocuments()) {
                    cmnts.add(new Comment(commentDoc));
                }
                addCommentToVM(cmnts);
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
        commentsLoading.setValue(false);
    }


    public LiveData<String> getPicture() {
        return gambar;
    }

    public LiveData<String> getTitle() {
        return judul;
    }

    public LiveData<String> getAsker() {
        return penanya;
    }

    public LiveData<String> getTanggal() {
        return tanggal;
    }

    public LiveData<String> getIsi() {
        return isi;
    }

    public LiveData<Integer> getLike() {
        return like;
    }

    public LiveData<ArrayList<CommentItemViewModel>> getComments() {
        return comments;
    }

    public LiveData<Boolean> isCommentsLoading() {
        return commentsLoading;
    }

    public LiveData<Boolean> isAddCommentsLoading() {
        return addCommentsLoading;
    }

    public LiveData<Boolean> isCommentEnabled(){
        return commentEnabled;
    }

    public LiveData<Boolean> isFavoriteEnabled(){
        return favoritEnabled;
    }

    public LiveData<Boolean> isFavorited(){
        return favorited;
    }
}
 