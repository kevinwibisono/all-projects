package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.CommentDBAccess;
import my.istts.finalproject.models.Diskusi;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

public class DiscussItemViewModel {
    private Diskusi diskusi;

    public DiscussItemViewModel(Diskusi diskusi) {
        this.diskusi = diskusi;

        Storage storage = new Storage();
        storage.getPictureUrlFromName(diskusi.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                picture.setValue(uri.toString());
            }
        });

        CommentDBAccess commentDB = new CommentDBAccess();
        commentDB.getAllCommments(diskusi.getId_diskusi()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                comment.setValue(queryDocumentSnapshots.getDocuments().size());
            }
        });
    }

    private MutableLiveData<String> picture = new MutableLiveData<>();
    private MutableLiveData<Integer> comment = new MutableLiveData<>(0);

    public LiveData<String> getPicture(){
        return picture;
    }

    public Integer getLike(){
        return diskusi.getLike();
    }

    public LiveData<Integer> getComment(){
        return comment;
    }

    public String getJudul(){
        return diskusi.getJudul();
    }

    public String getIsi(){
        return diskusi.getIsi();
    }

    public String getId(){
        return diskusi.getId_diskusi();
    }
}
