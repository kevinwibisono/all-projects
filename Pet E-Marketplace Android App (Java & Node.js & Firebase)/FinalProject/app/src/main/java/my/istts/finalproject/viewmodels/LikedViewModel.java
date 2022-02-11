package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Artikel;
import my.istts.finalproject.models.ArtikelDBAccess;
import my.istts.finalproject.models.Diskusi;
import my.istts.finalproject.models.DiskusiDBAccess;
import my.istts.finalproject.models.Favorit;
import my.istts.finalproject.models.FavoritDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.ArticleItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.DiscussItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LikedViewModel {
    private FavoritDBAccess favDB;
    private ArtikelDBAccess artikelDB;
    private DiskusiDBAccess discussDB;

    public LikedViewModel(Application app){
        this.favDB = new FavoritDBAccess(app);
        this.artikelDB = new ArtikelDBAccess();
        this.discussDB = new DiskusiDBAccess();
    }

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Integer> itemCount = new MutableLiveData<>(0);
    private MutableLiveData<String> pageTitle = new MutableLiveData<>("");
    private MutableLiveData<String> emptyTitle = new MutableLiveData<>("");

    private MutableLiveData<ArrayList<ArticleItemViewModel>> articleVMs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<DiscussItemViewModel>> discussVMs = new MutableLiveData<>();

    public void searchLikedItems(int tipe){
        if(tipe == 3){
            pageTitle.setValue("Artikel Disukai");
            emptyTitle.setValue("Tidak Ditemukan Artikel yang Disukai");
        }
        else{
            pageTitle.setValue("Diskusi Disukai");
            emptyTitle.setValue("Tidak Ditemukan Diskusi yang Disukai");
        }
        loading.setValue(true);
        articleVMs.setValue(new ArrayList<>());
        discussVMs.setValue(new ArrayList<>());

        favDB.setFavoriteGotListener(new FavoritDBAccess.onFavoriteGot() {
            @Override
            public void onGot(List<Favorit> favs) {
                itemCount.setValue(favs.size());
                ArrayList<Integer> counter = new ArrayList<>();
                if(favs.size() > 0){
                    for (Favorit favorit:
                            favs) {
                        if(tipe == 3){
                            artikelDB.getArticleById(favorit.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.getData() != null){
                                        Artikel artikel = new Artikel(documentSnapshot);
                                        addArticleToVM(artikel);
                                    }
                                    counter.add(0);
                                    if(counter.size() == favs.size()){
                                        loading.setValue(false);
                                    }
                                }
                            });
                        }
                        else{
                            discussDB.getDiscussionById(favorit.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.getData() != null){
                                        Diskusi diskusi = new Diskusi(documentSnapshot);
                                        addDiscussToVM(diskusi);
                                    }
                                    counter.add(0);
                                    if(counter.size() == favs.size()){
                                        loading.setValue(false);
                                    }
                                }
                            });
                        }
                    }
                }
                else{
                    loading.setValue(false);
                }
            }
        });

        favDB.getFavoritTipe(tipe);
    }

    private void addArticleToVM(Artikel artikel){
        ArrayList<ArticleItemViewModel> currentArticles = articleVMs.getValue();
        currentArticles.add(new ArticleItemViewModel(artikel));
        articleVMs.setValue(currentArticles);
    }

    private void addDiscussToVM(Diskusi diskusi){
        ArrayList<DiscussItemViewModel> currentDiscuss = discussVMs.getValue();
        currentDiscuss.add(new DiscussItemViewModel(diskusi));
        discussVMs.setValue(currentDiscuss);
    }

    public LiveData<String> getPageTitle() {
        return pageTitle;
    }

    public LiveData<String> getEmptyTitle() {
        return emptyTitle;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Integer> getItemCount() {
        return itemCount;
    }

    public LiveData<ArrayList<ArticleItemViewModel>> getArticleVMs() {
        return articleVMs;
    }

    public LiveData<ArrayList<DiscussItemViewModel>> getDiscussVMs() {
        return discussVMs;
    }
}
