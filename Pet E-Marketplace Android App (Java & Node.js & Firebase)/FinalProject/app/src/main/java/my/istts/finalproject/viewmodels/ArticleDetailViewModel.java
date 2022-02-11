package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Artikel;
import my.istts.finalproject.models.ArtikelDBAccess;
import my.istts.finalproject.models.Comment;
import my.istts.finalproject.models.CommentDBAccess;
import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.CommentItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ArticleDetailViewModel {
    private ArtikelDBAccess artikelDB;
    private AkunDBAccess akunDB;
    private CommentDBAccess commentDB;
    private FavoritDBAccess favDB;
    private Application app;
    
    public ArticleDetailViewModel(Application app){
        this.app = app;
        this.artikelDB = new ArtikelDBAccess();
        this.akunDB = new AkunDBAccess(app);
        this.favDB = new FavoritDBAccess(app);
        this.commentDB = new CommentDBAccess();
    }

    private String id_artikel, emailUser;
    private Favorit favorit;
    private MutableLiveData<ArrayList<CommentItemViewModel>> comments = new MutableLiveData<>();
    private MutableLiveData<Boolean> commentsLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> addCommentsLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> commentEnabled = new MutableLiveData<>(false);

    private MutableLiveData<Boolean> favoritEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> favorited = new MutableLiveData<>();

    private MutableLiveData<String> picture = new MutableLiveData<>("");
    private MutableLiveData<String> title = new MutableLiveData<>("");
    private MutableLiveData<String> penulis = new MutableLiveData<>("");
    private MutableLiveData<String> tanggal = new MutableLiveData<>("");
    private MutableLiveData<String> isi = new MutableLiveData<>("");
    private MutableLiveData<String> link = new MutableLiveData<>("");
    private MutableLiveData<Integer> like = new MutableLiveData<>(0);

    public MutableLiveData<String> komentar = new MutableLiveData<>("");

    public void setArticleDetail(String id_artikel){
        this.id_artikel = id_artikel;
        favoritEnabled.setValue(false);

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    emailUser = accountsGot.get(0).getEmail();
                    commentEnabled.setValue(true);

                    artikelDB.getArticleById(id_artikel).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData() != null){
                                Artikel artikel = new Artikel(documentSnapshot);

                                title.setValue(artikel.getJudul());
                                penulis.setValue(artikel.getNama_penulis());
                                tanggal.setValue(artikel.getStringDate());
                                isi.setValue(artikel.getIsi());
                                link.setValue(artikel.getLink());
                                like.setValue(artikel.getLike());
                                picture.setValue(artikel.getGambar());

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

                                favDB.getFavorit(emailUser, artikel.getId_artikel());
                            }
                        }
                    });

                    getArticleComments(id_artikel);
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
                    artikelDB.incArticleLike(id_artikel, -1);
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
                        artikelDB.incArticleLike(id_artikel, 1);
                        like.setValue(like.getValue() + 1);
                    }
                    else{
                        favorit = new Favorit(emailUser, id_artikel, 3);
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
                                            artikelDB.incArticleLike(id_artikel, 1);
                                            like.setValue(like.getValue() + 1);
                                        }
                                    }
                                });

                                favDB.getFavorit(emailUser, id_artikel);
                            }
                        });

                        favDB.addFavorit(favorit);
                    }
                }
            });

            favDB.getFavorit(emailUser, id_artikel);
        }
    }


    public void sendComment(){
        addCommentsLoading.setValue(true);
        commentEnabled.setValue(false);

        commentDB.addKomentar(id_artikel, emailUser, komentar.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                komentar.setValue("");
                getArticleComments(id_artikel);
                addCommentsLoading.setValue(false);
                commentEnabled.setValue(true);
            }
        });
    }

    private void getArticleComments(String id_artikel){
        commentsLoading.setValue(true);
        comments.setValue(new ArrayList<>());

        commentDB.getAllCommments(id_artikel).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
        return picture;
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public LiveData<String> getPenulis() {
        return penulis;
    }

    public LiveData<String> getTanggal() {
        return tanggal;
    }

    public LiveData<String> getIsi() {
        return isi;
    }

    public LiveData<String> getLink() {
        return link;
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
 