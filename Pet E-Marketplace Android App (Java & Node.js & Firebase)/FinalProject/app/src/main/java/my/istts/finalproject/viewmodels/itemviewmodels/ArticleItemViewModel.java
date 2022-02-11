package my.istts.finalproject.viewmodels.itemviewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Artikel;
import my.istts.finalproject.models.CommentDBAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

public class ArticleItemViewModel {
    private Artikel artikel;

    public ArticleItemViewModel(Artikel artikel){
        this.artikel = artikel;

        CommentDBAccess commentDB = new CommentDBAccess();
        commentDB.getAllCommments(artikel.getId_artikel()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                comments.setValue(queryDocumentSnapshots.getDocuments().size());
            }
        });
    }

    private MutableLiveData<Integer> comments = new MutableLiveData<>();

    public LiveData<Integer> getComments(){
        return comments;
    }

    public int getLike(){
        return artikel.getLike();
    }

    public String getTitle(){
        return artikel.getJudul();
    }

    public String getText(){
        return artikel.getIsi();
    }

    public String getId(){
        return artikel.getId_artikel();
    }

    public String getPicture(){
        return artikel.getGambar();
    }
}
