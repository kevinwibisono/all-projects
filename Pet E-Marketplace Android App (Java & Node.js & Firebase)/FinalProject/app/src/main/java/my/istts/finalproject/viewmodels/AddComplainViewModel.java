package my.istts.finalproject.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.ComplainDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.ItemPJViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class AddComplainViewModel {
    private PesananJanjitemuDBAccess orderDB;
    private ComplainDBAccess complainDB;
    private Storage storage;
    private BackendRetrofitService firebaseNotifService;

    public AddComplainViewModel(){
        orderDB = new PesananJanjitemuDBAccess();
        complainDB = new ComplainDBAccess();
        storage = new Storage();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    private MutableLiveData<String[]> errors = new MutableLiveData<>();
    private MutableLiveData<Integer> focusedNumber = new MutableLiveData<>();
    private MutableLiveData<Bitmap[]> bukti = new MutableLiveData<>(new Bitmap[4]);
    private String[] picURLs = new String[4];
    private ArrayList<ItemPJViewModel> incProducts = new ArrayList<>();
    private int jumlahKembali;
    public MutableLiveData<String> keluhan = new MutableLiveData<>("");
    public MutableLiveData<String> video = new MutableLiveData<>("");
    public MutableLiveData<String> persen = new MutableLiveData<>("100");
    private MutableLiveData<Boolean> addLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> added = new MutableLiveData<>();
    private MutableLiveData<Boolean> pjItemsLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ItemPJViewModel>> pjItems = new MutableLiveData<>();

    public void setPictureBitmap(int index, Bitmap picture){
        Bitmap[] currentPictures = bukti.getValue();
        assert currentPictures != null;
        currentPictures[index] = picture;
        bukti.setValue(currentPictures);
    }

    public void deletePictureBitmap(int index){
        Bitmap[] currentPictures = bukti.getValue();
        assert currentPictures != null;
        currentPictures[index] = null;
        bukti.setValue(currentPictures);
    }

    public void includeProduct(int index, boolean included){
        if(pjItems.getValue() != null){
            if(included){
                incProducts.add(pjItems.getValue().get(index));
            }
            else{
                incProducts.remove(pjItems.getValue().get(index));
            }
        }
    }

    public void changePersen(String percentInput){
        if(percentInput.equals("")){
            persen.setValue("1");
        }
        else{
            if(Integer.parseInt(percentInput) > 100){
                persen.setValue("100");
            }
            else{
                persen.setValue(percentInput);
            }
        }
    }

    public void getPjListOfItems(String id_pj){
        pjItemsLoading.setValue(true);
        pjItems.setValue(new ArrayList<>());

        orderDB.getItemPJByPesanan(id_pj).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<ItemPesananJanjitemu> items = new ArrayList<>();
                for (DocumentSnapshot itemDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    items.add(new ItemPesananJanjitemu(itemDoc));
                }
                addItemsToVM(items);
            }
        });
    }

    private void addItemsToVM(ArrayList<ItemPesananJanjitemu> items){
        ArrayList<ItemPJViewModel> currentVMs = pjItems.getValue();
        for (ItemPesananJanjitemu itemPJ:
             items) {
            currentVMs.add(new ItemPJViewModel(itemPJ, 0));
        }
        pjItems.setValue(currentVMs);
        pjItemsLoading.setValue(false);
    }

    private boolean picturesEmpty(){
        boolean picturesEmpty = true;
        for (Bitmap picture:
                Objects.requireNonNull(bukti.getValue())) {
            if(picture != null){
                picturesEmpty = false;
            }
        }
        return picturesEmpty;
    }

    public boolean fieldError(){
        if(keluhan.getValue().equals("")){
            errors.setValue(new String[]{"Detail Keluhan/Komplain Tidak Dapat Kosong", "", ""});
            focusedNumber.setValue(0);
            return true;
        }
        else if(picturesEmpty()){
            errors.setValue(new String[]{"", "Bukti Gambar Harus Disertakan Minimal 1", ""});
            focusedNumber.setValue(1);
            return true;
        }
        else if(incProducts.size() < 1){
            errors.setValue(new String[]{"", "", "Pilih Produk Yang Dikomplain Minimal 1"});
            focusedNumber.setValue(2);
            return true;
        }
        else{
            errors.setValue(new String[]{"", "", ""});

            int totalHargaProduk = 0;
            for (ItemPJViewModel incItem:
                 incProducts) {
                totalHargaProduk += (incItem.getPrice()*incItem.getQty());
            }

            jumlahKembali = (totalHargaProduk * Integer.parseInt(persen.getValue()))/100;

            return false;
        }
    }

    public void beginUploadImages(String id_pj){
        addLoading.setValue(true);

        ArrayList<Bitmap> filledBitmaps = new ArrayList<>();
        for (Bitmap picture:
                Objects.requireNonNull(bukti.getValue())) {
            if(picture != null){
                filledBitmaps.add(picture);
            }
        }

        for (int i=0;i<filledBitmaps.size();i++){
            final int index = i;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            filledBitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            String picName = UUID.nameUUIDFromBytes(data).toString();
            storage.uploadPicture(data, picName).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    picURLs[index] = picName;
                    if (index == filledBitmaps.size()-1){
                        addComplain(id_pj);
                    }
                }
            });
        }
    }

    private void addComplain(String id_pj){
        orderDB.getPJById(id_pj).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                    String topikEmail = pj.getemail_penjual().substring(0, pj.getemail_penjual().indexOf('@'));
                    firebaseNotifService.sendNotif("Pelanggan Mengajukan Komplain", "Pesananmu Mendapat Komplain Dari Pelanggan", topikEmail);
                    orderDB.complainOrder(id_pj, pj.getSelesai_otomatis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            String ids = "";
                            for (ItemPJViewModel incItem:
                                    incProducts) {
                                if(ids.equals("")){
                                    ids += incItem.getIdItemPJ();
                                }
                                else{
                                    ids += "|"+incItem.getIdItemPJ();
                                }
                            }

                            complainDB.addNewComplain(id_pj, keluhan.getValue(), ids, jumlahKembali, picURLs, video.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    addLoading.setValue(false);
                                    added.setValue(true);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public LiveData<ArrayList<ItemPJViewModel>> getPjItems() {
        return pjItems;
    }

    public int getJumlahKembali() {
        return jumlahKembali;
    }

    public LiveData<String[]> getErrors() {
        return errors;
    }

    public LiveData<Integer> getFocusedNumber() {
        return focusedNumber;
    }

    public LiveData<Boolean> getAddLoading() {
        return addLoading;
    }

    public LiveData<Boolean> getPjItemsLoading() {
        return pjItemsLoading;
    }

    public LiveData<Boolean> getAdded() {
        return added;
    }

    public LiveData<Bitmap[]> getBukti() {
        return bukti;
    }
}
