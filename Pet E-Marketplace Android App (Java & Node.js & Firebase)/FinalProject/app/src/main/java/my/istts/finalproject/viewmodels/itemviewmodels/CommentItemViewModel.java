package my.istts.finalproject.viewmodels.itemviewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Comment;
import my.istts.finalproject.models.CommentDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CommentItemViewModel {
    private Comment comment;


    public CommentItemViewModel(Comment comment, Application app){
        this.comment = comment;

        setPengomentarName(app, comment.getemail_pengomentar(), false);
        setPengomentarPic(comment.getemail_pengomentar(), false);

        CommentDBAccess commentDB = new CommentDBAccess();
        commentDB.getCommentOfComment(comment.getId_komentar()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    Comment firstComment = new Comment(queryDocumentSnapshots.getDocuments().get(0));
                    replyTanggal.setValue(firstComment.getTanggal());
                    replyTeks.setValue(firstComment.getTeks());
                    setPengomentarName(app, firstComment.getemail_pengomentar(), true);
                    setPengomentarPic(firstComment.getemail_pengomentar(), true);
                }
            }
        });
    }
    
    private void setPengomentarName(Application app, String no_hp, boolean reply){
        AkunDBAccess akunDB = new AkunDBAccess(app);
        akunDB.getAccByEmail(no_hp).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    if(reply){
                        replyPengomentar.setValue(documentSnapshot.getString("nama"));
                    }
                    else{
                        pengomentar.setValue(documentSnapshot.getString("nama"));
                    }
                }
            }
        });
    }
    
    private void setPengomentarPic(String no_hp, boolean reply){
        Storage storage = new Storage();
        storage.getPictureUrlFromName(no_hp).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(reply){
                    replyPengomentarPic.setValue(uri.toString());
                }
                else{
                    pengomentarPic.setValue(uri.toString());
                }
            }
        });
    }

    private MutableLiveData<String> replyPengomentarPic = new MutableLiveData<>();
    private MutableLiveData<String> replyPengomentar = new MutableLiveData<>("");
    private MutableLiveData<String> replyTanggal = new MutableLiveData<>("");
    private MutableLiveData<String> replyTeks = new MutableLiveData<>("");
    private MutableLiveData<Boolean> replyIsSeller = new MutableLiveData<>(false);

    private MutableLiveData<String> pengomentarPic = new MutableLiveData<>();
    private MutableLiveData<String> pengomentar = new MutableLiveData<>("");

    public LiveData<String> getPengomentar(){
        return pengomentar;
    }

    public LiveData<String> getReplyPengomentarPic() {
        return replyPengomentarPic;
    }

    public LiveData<String> getReplyPengomentar() {
        return replyPengomentar;
    }

    public LiveData<String> getReplyTanggal() {
        return replyTanggal;
    }

    public LiveData<String> getReplyTeks() {
        return replyTeks;
    }

    public LiveData<Boolean> isReplyFromSeller() {
        return replyIsSeller;
    }

    public LiveData<String> getPengomentarPic() {
        return pengomentarPic;
    }

    public String getTanggal(){
        return comment.getTanggal();
    }

    public String getTeks(){
        return comment.getTeks();
    }

    public String getId(){
        return comment.getId_komentar();
    }
}
