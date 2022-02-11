package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.sellerapp.models.PaketGrooming;
import com.example.sellerapp.models.PaketGroomingDBAccess;
import com.example.sellerapp.inputclasses.ClinicSchedule;
import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.DetailPenjual;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.viewmodels.itemviewmodels.PaketGroomingItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellerDetailViewModel {
    private DetailPenjualDBAccess detailDB;
    private PaketGroomingDBAccess groomerDB;
    private AkunDBAccess akunDB;
    private Storage storage;
    private Application app;

    private MutableLiveData<Integer> role = new MutableLiveData<>();
    private MutableLiveData<String> loadingTitle = new MutableLiveData<>("");
    private String[] roles = {"Toko", "Groomer", "Hotel", "Klinik"};
    private String[] posters = new String[4];
    private MutableLiveData<String[]> pageTitles = new MutableLiveData<>(new String[5]);
    private MutableLiveData<String>[] picURLs = new MutableLiveData[5]; //array of string livedata
    private MutableLiveData<Bitmap>[] picturesBitmaps = new MutableLiveData[5];
    private MutableLiveData<String> alamat = new MutableLiveData<String>("");
    private String email;
    private MutableLiveData<Integer> updating = new MutableLiveData<>();
    private MutableLiveData<ArrayList<PaketGroomingItemViewModel>> paketGrooming = new MutableLiveData<>();
    public MutableLiveData<boolean[]> couriersCheck = new MutableLiveData<>(new boolean[4]);
    public MutableLiveData<boolean[]> appoTypeCheck = new MutableLiveData<>(new boolean[2]);

    public SellerDetailViewModel(Application application) {
        app = application;
        detailDB = new DetailPenjualDBAccess(application);
        akunDB = new AkunDBAccess(application);
        groomerDB = new PaketGroomingDBAccess();
        storage = new Storage();
        boolean[] defaultCouriers = {false, true, true, true};
        boolean[] defaultAppoTypes = {true, true};
        loadingTitle.setValue("Menambahkan Detail...");
        couriersCheck.setValue(defaultCouriers);
        appoTypeCheck.setValue(defaultAppoTypes);
        updating.setValue(0);
        for (int i = 0; i < 5; i++) {
            picturesBitmaps[i] = new MutableLiveData<>();
            picURLs[i] = new MutableLiveData<>();
            if(i == 0){
                picURLs[i].setValue("https://firebasestorage.googleapis.com/v0/b/pawfriends-a5086.appspot.com/o/uploadimg.jpg?alt=media&token=901043a8-9426-484b-b852-e8feb3824006");
            }
            else{
                picURLs[i].setValue("https://firebasestorage.googleapis.com/v0/b/pawfriends-a5086.appspot.com/o/uploadimghorizontal.png?alt=media&token=34c51192-383e-4f45-8597-97f53a658a0a");
            }
        }
    }

    public void updateDetail(){
        loadingTitle.setValue("Mengubah Detail...");
        updating.setValue(1);
        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    detailDB.setGetDetailCompleteListener(new DetailPenjualDBAccess.onCompleteGetDetailListener() {
                        @Override
                        public void onDetailComplete(List<DetailPenjual> detailsGot) {
                            if(detailsGot.size() > 0){
                                DetailPenjual detailSaved = detailsGot.get(0);

                                if(detailSaved.getRole() == 1){
                                    paketGrooming.setValue(new ArrayList<>());
                                    getGroomerPacks(accountsGot.get(0).getEmail());
                                }

                                storage.getPictureUrlFromName(accountsGot.get(0).getEmail()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        picURLs[0].setValue(uri.toString());
                                        Glide
                                                .with(app)
                                                .asBitmap()
                                                .load(uri.toString())
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        setPicturesBitmaps(resource, 0);
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                                    }
                                                });
                                    }
                                });

                                String[] posters = detailSaved.getPoster().split("\\|", -1);
                                for (int i = 0; i < posters.length; i++) {
                                    final int index = i;
                                    if(!posters[i].equals("")){
                                        storage.getPictureUrlFromName(posters[i]).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                picURLs[index+1].setValue(uri.toString());

                                                Glide
                                                        .with(app)
                                                        .asBitmap()
                                                        .load(uri.toString())
                                                        .into(new CustomTarget<Bitmap>() {
                                                            @Override
                                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                setPicturesBitmaps(resource, index+1);
                                                            }

                                                            @Override
                                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                }

                                sellerName.setValue(accountsGot.get(0).getNama());
                                sellerDesc.setValue(detailSaved.getDeskripsi());
                                alamat.setValue(detailSaved.getAlamat());
                                koordinatAlamat = detailSaved.getKoordinat();

                                determineTitles(detailSaved.getRole());
                                arrangeCheckboxes(detailSaved);

                            }
                        }
                    });

                    detailDB.getLocalDetail();
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private void arrangeCheckboxes(DetailPenjual detailSaved){
        if(detailSaved.getRole() == 0){
            //kurir untuk pet shop
            boolean[] savedCourierChecked = new boolean[4];
            String[] couriersChecked = detailSaved.getKurir().split("\\|", -1);
            for (int i = 0; i < 4; i++) {
                Boolean checked = Boolean.parseBoolean(couriersChecked[i]);
                savedCourierChecked[i] = checked;
            }
            couriersCheck.setValue(savedCourierChecked);
        }
        else if(detailSaved.getRole() == 3){
            //jam buka/tutup untuk pet clinic
            boolean[] savedAppoTypeChecked = new boolean[2];
            String[] savedAppoChecked = detailSaved.getJanji_temu().split("\\|", -1);
            for (int i = 0; i < 2; i++) {
                Boolean checked = Boolean.parseBoolean(savedAppoChecked[i]);
                savedAppoTypeChecked[i] = checked;
            }
            appoTypeCheck.setValue(savedAppoTypeChecked);

            ClinicSchedule[] savedSchedules = new ClinicSchedule[7];
            String[] days = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
            String[] jam_buka = detailSaved.getJam_buka().split("\\|", -1);
            String[] jam_tutup = detailSaved.getJam_tutup().split("\\|", -1);

            for (int i = 0; i < 7; i++) {
                boolean open = true;
                if(jam_buka[i].equals("")){
                    open = false;
                }
                savedSchedules[i] = new ClinicSchedule(days[i], open, jam_buka[i], jam_tutup[i]);
            }

            clinicSchedules = savedSchedules;
            updatingSchedules.setValue(savedSchedules);
        }
    }

    private void getGroomerPacks(String hp_groomer){
        groomerDB.getGroomingPackages(hp_groomer).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    for (DocumentSnapshot groomDoc:
                         queryDocumentSnapshots.getDocuments()) {
                        PaketGrooming paket = new PaketGrooming(groomDoc);
                        addGroomerPackageFromDB(groomDoc);
                    }
                }
            }
        });
    }

    public LiveData<String[]> getTitles(){
        return pageTitles;
    }

    public LiveData<String> getLoadingTitle(){
        return loadingTitle;
    }

    public LiveData<String> getPicURLs(int index) {
        return picURLs[index];
    }

    public LiveData<Bitmap> getPictureBitmaps(int index){
        return picturesBitmaps[index];
    }

    public LiveData<Integer> getRole(){
        return role;
    }

    public LiveData<Integer> isUpdating(){
        return updating;
    }

    public LiveData<String> getAlamat(){
        return alamat;
    }

    public LiveData<ArrayList<PaketGroomingItemViewModel>> getGroomingPackages(){
        return paketGrooming;
    }

    public void setAddress(String address){
        alamat.setValue(address);
    }

    public void initializeFields(int roleFromIntent){
        this.role.setValue(roleFromIntent);
        loading.setValue(true);
        paketGrooming.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();
                    initSchedules();
                    if(roleFromIntent < 3){
                        alamat.setValue("alamat");
                    }
                    if(roleFromIntent != 1){
                        addGroomerPackage(0, "");
                    }

                    if(roleFromIntent > -1){
                        determineTitles(roleFromIntent);
                    }
                    else{
                        updateDetail();
                    }
                }
            }
        });

        akunDB.getSavedAccounts();
    }



    public void determineTitles(int roleFromIntent){
        this.role.setValue(roleFromIntent);
        String[] titles = {"Profil "+roles[roleFromIntent], "Logo "+roles[roleFromIntent], "Nama "+roles[roleFromIntent],
                "Deskripsi "+roles[roleFromIntent], "Poster "+roles[roleFromIntent]};
        pageTitles.setValue(titles);
        loading.setValue(false);
    }

    public MutableLiveData<String> sellerName = new MutableLiveData<>("");
    public MutableLiveData<String> sellerDesc = new MutableLiveData<>("");
    private MutableLiveData<String[]> errors;
    private MutableLiveData<Integer> focusedNumber;
    private MutableLiveData<Boolean> loading, successAdd;

    private ClinicSchedule[] clinicSchedules = new ClinicSchedule[7];
    private MutableLiveData<ClinicSchedule[]> updatingSchedules = new MutableLiveData<>();
    private ArrayList<Bitmap> filledBitmaps;
    private String koordinatAlamat = "";

    public LiveData<String[]> getErrors(){
        if(errors == null){
            String[] initErrors = {"", "", "", "", "", ""};
            errors = new MutableLiveData<>(initErrors);
        }
        return errors;
    }

    public LiveData<Integer> getFocusedNumber(){
        if(focusedNumber == null){
            focusedNumber = new MutableLiveData<>();
        }
        return focusedNumber;
    }

    public void setCoordinate(String coor){
        koordinatAlamat = coor;
    }

    public LiveData<Boolean> isLoading() {
        if(loading == null){
            loading = new MutableLiveData<>();
        }
        return loading;
    }

    public LiveData<Boolean> isSuccessAdding() {
        if(successAdd == null){
            successAdd = new MutableLiveData<>();
        }
        return successAdd;
    }

    private void initSchedules(){
        ClinicSchedule[] schedulesNow = new ClinicSchedule[7];
        String[] days = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        for (int i=0;i<days.length;i++){
            schedulesNow[i] = new ClinicSchedule(days[i], true, "10:00", "21:00");
        }
        clinicSchedules = schedulesNow;
    }

    public LiveData<ClinicSchedule[]> getUpdatingSchedules() {
        if(updatingSchedules.getValue() == null){
            ClinicSchedule[] schedulesNow = new ClinicSchedule[7];
            String[] days = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
            for (int i=0;i<days.length;i++){
                schedulesNow[i] = new ClinicSchedule(days[i], true, "10:00", "21:00");
            }
            updatingSchedules.setValue(schedulesNow);
        }
        return updatingSchedules;
    }

    public void setClinicSchedule(ClinicSchedule schedule, int targetIndex){
        clinicSchedules[targetIndex] = schedule;
    }

    public void setPicturesBitmaps(Bitmap picture, int index){
        picturesBitmaps[index].setValue(picture);
    }

    public void deletePicturesBitmaps(int index){
        picturesBitmaps[index].setValue(null);
    }

    private int checkPosterUploaded(){
        int notNull = 0;
        for (int i=1;i<5;i++){
            if(picturesBitmaps[i].getValue() != null){
                notNull++;
            }
        }
        return notNull;
    }

    public boolean saveSellerDetail(){
        boolean noError = true;
        String[] newErrors = {"", "", "", "", "", "", "", "", ""};
        loading.setValue(true);
        if(picturesBitmaps[0].getValue() == null){
            noError = false;
            newErrors = new String[]{"", "", "", "Mohon pilih gambar/logo "+roles[role.getValue()], "", "", "", "", ""};
            focusedNumber.setValue(3);
        }
        else if(checkPosterUploaded() <= 0){
            noError = false;
            newErrors = new String[]{"", "", "", "", "Mohon pilih sedikitnya 1 gambar poster", "", "", "", ""};
            focusedNumber.setValue(4);
        }
        else if(sellerName.getValue().equals("")){
            noError = false;
            newErrors = new String[]{"Nama "+roles[role.getValue()]+" harus diisi", "", "", "", "", "", "", "", ""};
            focusedNumber.setValue(0);
        }
        else if(invalidOptions() > -1){
            //5, 6 & 7
            newErrors[invalidOptions()] = "Minimal 1 opsi harus diisi/dipilih";
            noError = false;
            focusedNumber.setValue(invalidOptions());
        }
        else if (invalidAddress() > -1) {
            //1 & 2
            newErrors[invalidAddress()] = "Alamat harus disertakan";
            noError = false;
            focusedNumber.setValue(invalidAddress());
        }
        else if(paketGrooming.getValue().size() <= 0){
            //groomer, cek paket grooming jangan kosong
            newErrors = new String[]{"", "", "", "", "", "", "", "", "Mohon sertakan setidaknya 1 paket grooming"};
            noError = false;
            focusedNumber.setValue(7);
        }
        errors.setValue(newErrors);
        loading.setValue(false);
        return noError;
    }

    private int invalidAddress(){
        if(alamat.getValue().equals("")){
            if(role.getValue() == 0 && couriersCheck.getValue()[0]){
                //jika shop mengkaktifkan self pickup & alamat kosong
                return 2;
            }
            else{
                //jika alamat klinik tidak disertakan
                return 1;
            }
        }
        else{
            return -1;
        }
    }

    private int invalidOptions(){
        int focusedNumber = -1;
        if(assembleString(couriersCheck.getValue()).equals("false|false|false|false")){
            return 5;
        }
        else if(assembleSchedulesOpens(clinicSchedules).equals("||||||")){
            return 6;
        }
        else if(assembleString(appoTypeCheck.getValue()).equals("false|false")){
            return 7;
        }
        return focusedNumber;
    }

    public void activateSelfPickup(){
        if(couriersCheck.getValue()[0]){
            //checked -> unchecked
            alamat.setValue("alamat");
        }
        else{
            alamat.setValue("");
        }
        koordinatAlamat = "";
        boolean[] currentCourierChecks = couriersCheck.getValue();
        currentCourierChecks[0] = !currentCourierChecks[0];
        couriersCheck.setValue(currentCourierChecks);
    }

    public void addGroomerPackage(int harga, String nama){
        ArrayList<PaketGroomingItemViewModel> currentPacks = paketGrooming.getValue();
        currentPacks.add(new PaketGroomingItemViewModel(new PaketGrooming(nama, harga, "")));
        paketGrooming.setValue(currentPacks);
    }

    public void updateGroomerPackage(int harga, String nama, String id_pack){
        ArrayList<PaketGroomingItemViewModel> currentPacks = paketGrooming.getValue();
        for (PaketGroomingItemViewModel  paket:
             currentPacks) {
            if(paket.getId().equals(id_pack)){
                paket.setPaketGrooming(new PaketGrooming(nama, harga, id_pack));
            }
        }
        paketGrooming.setValue(currentPacks);
    }

    public void addGroomerPackageFromDB(DocumentSnapshot doc){
        ArrayList<PaketGroomingItemViewModel> currentPacks = paketGrooming.getValue();
        currentPacks.add(new PaketGroomingItemViewModel(new PaketGrooming(doc)));
        paketGrooming.setValue(currentPacks);
    }

    public void removeGroomerPackage(int position){
        ArrayList<PaketGroomingItemViewModel> currentPacks = paketGrooming.getValue();
        if(!currentPacks.get(position).getId().equals("")){
            groomerDB.deleteGroomingPackages(currentPacks.get(position).getId());
        }
        currentPacks.remove(position);
        paketGrooming.setValue(currentPacks);
    }

    public void uploadChosenPictures(){
        if(role.getValue() == 1){
            //add juga paket-paket grooming
            if(updating.getValue() == 1){
                groomerDB.updateGroomingPackages(paketGrooming.getValue(), email);
            }
            else{
                groomerDB.addGroomingPackages(paketGrooming.getValue(), email);
            }
        }
        final DetailPenjual detailToBeAdded = new DetailPenjual(role.getValue(), sellerDesc.getValue(),
                assembleString(couriersCheck.getValue()), assembleString(appoTypeCheck.getValue()), assembleSchedulesOpens(clinicSchedules),
                assembleSchedulesCloses(clinicSchedules), alamat.getValue(), koordinatAlamat, "");
        loading.setValue(true);
        filledBitmaps = new ArrayList<>();
        for (int i=0;i<5;i++){
            if(picturesBitmaps[i].getValue() != null){
                filledBitmaps.add(picturesBitmaps[i].getValue());
            }
        }

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    for (int i=0;i<filledBitmaps.size();i++){
                        final int index = i;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        filledBitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        if(index == 0){
                            storage.uploadPicture(data, email).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    akunDB.assignName(accountsGot.get(0), sellerName.getValue());
                                    akunDB.clearAccounts();
                                }
                            });
                        }
                        else{
                            String randNames = UUID.nameUUIDFromBytes(data).toString();
                            storage.uploadPicture(data, randNames).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    posters[index-1] = randNames;
                                    if (index == filledBitmaps.size()-1){
                                        //tidak ada error dan berhasil upload, maka add detail penjual baru
                                        detailDB.addDetailPenjual(detailToBeAdded, accountsGot.get(0).getEmail(), posters).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                loading.setValue(false);
                                                detailDB.clearDetails();

                                                successAdd.setValue(true);
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    }
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private String assembleString(boolean[] arrayStrings){
        String assembled = "";
        for (int i=0;i<arrayStrings.length;i++){
            if(!assembled.equals("")){
                //data pertama tidak usah diberi divider | di depannya
                //data selanjutnya diberi
                assembled += "|";
            }
            if(arrayStrings[i]){
                assembled += "true";
            }
            else{
                assembled += "false";
            }
        }
        return assembled;
    }

    private String assembleSchedulesOpens(ClinicSchedule[] schedules){
        String opens = "";
        for (int i=0;i<schedules.length;i++){
            if(schedules[i].isOpen()){
                opens += schedules[i].getOpenHourMinute();
            }
            if(i < schedules.length - 1){
                opens += "|";
            }
        }
        return opens;
    }

    private String assembleSchedulesCloses(ClinicSchedule[] schedules){
        String closes = "";
        for (int i=0;i<schedules.length;i++){
            if(schedules[i].isOpen()){
                closes += schedules[i].getCloseHourMinute();
            }
            if(i < schedules.length - 1){
                closes += "|";
            }
        }
        return closes;
    }
}
