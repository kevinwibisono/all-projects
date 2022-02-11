package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.DetailPenjual;
import my.istts.finalproject.models.ReviewDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class ClinicItemViewModel {
    private DetailPenjual clinicDetail;
    private String email;
    private String nama;

    public ClinicItemViewModel(DetailPenjual clinicDetail, String email, String nama) {
        this.clinicDetail = clinicDetail;
        this.email = email;
        this.nama = nama;

        Storage storage = new Storage();
        storage.getPictureUrlFromName(email).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                clinicPic.setValue(uri.toString());
            }
        });

        ReviewDBAccess reviewDB = new ReviewDBAccess();
        reviewDB.getAllReviewsOfItem(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int jumlahReview = queryDocumentSnapshots.getDocuments().size();
                float skorNow = 0;
                for (DocumentSnapshot reviewDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    skorNow += reviewDoc.getLong("nilai");
                }
                skorNow = skorNow/jumlahReview;
                clinicScore.setValue(String.valueOf(skorNow).substring(0, 3));
                clinicReviews.setValue(queryDocumentSnapshots.getDocuments().size());
            }
        });
    }

    private MutableLiveData<String> clinicPic = new MutableLiveData<>("");
    private MutableLiveData<String> clinicScore = new MutableLiveData<>("");
    private MutableLiveData<Integer> clinicReviews = new MutableLiveData<>(0);

    public LiveData<String> getClinicPic() {
        return clinicPic;
    }

    public LiveData<String> getClinicScore() {
        return clinicScore;
    }

    public LiveData<Integer> getClinicReviews() {
        return clinicReviews;
    }

    public String getEmailClinic(){
        return email;
    }

    public String getClinicName(){
        return nama;
    }

    public String getClinicAddres(){
        return clinicDetail.getAlamat();
    }

    public String getClinicCoors(){
        return clinicDetail.getKoordinat();
    }

    public Boolean isClinicOpen(){
        boolean open = true;
        Date today = new Date();
        int hari = today.getDay()-1; //getDay()  0->Sunday, min 1 agar pas 0 dimulai dari senin
        if(today.getDay() == 0){
            hari = 6;
        }
        int todayTotalMins = (today.getHours()*60)+ today.getMinutes();
        int todayOpenMins, todayCloseMins;

        String[] jamBuka = clinicDetail.getJamBukaSplitted();
        String[] jamTutup = clinicDetail.getJamTutupSplitted();

        if(jamBuka[hari].equals("")){
            open = false;
        }
        else{
            todayOpenMins = getMinutes(jamBuka[hari].split(":"));
            todayCloseMins = getMinutes(jamTutup[hari].split(":"));

            if(todayCloseMins > todayOpenMins){
                open = timeValidNormal(todayTotalMins, todayOpenMins, todayCloseMins);
            }
            else if(todayCloseMins < todayOpenMins){
                open = timeValidPassMidnight(todayTotalMins, todayOpenMins, todayCloseMins);
            }
        }

        return open;
    }

    private int getMinutes(String[] time){
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        return (hour*60)+minute;
    }

    //cth jam buka -> 18:00
    //cth jam tutup -> 00:00
    //range yang valid -> lebih kecil dari waktu tutup ATAU lebih besar dari waktu buka
    private boolean timeValidPassMidnight(int todayMinutes, int openMinutes, int closesMinutes){
        if(todayMinutes <= closesMinutes || todayMinutes >= openMinutes){
            return true;
        }
        else{
            return false;
        }
    }

    //cth jam buka -> 18:00
    //cth jam tutup -> 20:00
    //range yang valid -> lebih kecil dari waktu tutup DAN lebih besar dari waktu buka
    private boolean timeValidNormal(int todayMinutes, int openMinutes, int closesMinutes){
        if(todayMinutes >= openMinutes && todayMinutes <= closesMinutes){
            return true;
        }
        else{
            return false;
        }
    }
}
