package my.istts.finalproject.viewmodels.itemviewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import my.istts.finalproject.models.Adoption;
import my.istts.finalproject.models.AdoptionDBAccess;
import my.istts.finalproject.models.Chat;
import my.istts.finalproject.models.DetailJanjiTemu;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ChatItemViewModel extends ViewModel {
    private Chat chat;
    private String[] itemTypes = {"Produk", "Paket", "Kamar", "Klinik"};

    public ChatItemViewModel(Chat chat) {
        this.chat = chat;

        ProductDBAccess productDB = new ProductDBAccess();
        HotelDBAccess hotelDB = new HotelDBAccess();
        AdoptionDBAccess adoptDB = new AdoptionDBAccess();
        PesananJanjitemuDBAccess orderDB = new PesananJanjitemuDBAccess();
        Storage storage = new Storage();

        if(!chat.getId_item().equals("")){
            chatItemVisible.setValue(true);
            //terdapat produk/hotel yang dibicarakan
            productDB.getProductById(chat.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot productDoc) {
                    if(productDoc.getData() != null){
                        //ditemukan product dengan id tsb, yang dibicarkan adalah produk
                        Product p = new Product(productDoc);
                        storage.getPictureUrlFromName(p.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                productPic.setValue(uri.toString());
                                productPrice.setValue(p.getHarga());
                                productName.setValue(p.getNama());
                                itemName.setValue(p.getNama());
                            }
                        });
                    }
                    else{
                        //item adalah hotel
                        hotelDB.getRoomById(chat.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot hotelDoc) {
                                if(hotelDoc.getData() != null){
                                    Hotel h = new Hotel(hotelDoc);
                                    storage.getPictureUrlFromName(h.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            hotelPic.setValue(uri.toString());
                                            hotelPrice.setValue(h.getHarga());
                                            hotelName.setValue(h.getNama());
                                            itemName.setValue(h.getNama());
                                        }
                                    });
                                }
                                else{
                                    //item adalah pet adopt
                                    adoptDB.getPetAdoptById(chat.getId_item()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.getData() != null){
                                                Adoption adoption = new Adoption(documentSnapshot);
                                                storage.getPictureUrlFromName(adoption.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        adoptPic.setValue(uri.toString());
                                                        adoptDesc.setValue(adoption.getJenisHewan()+" "+adoption.getJenisKelaminHewan());
                                                        adoptName.setValue(adoption.getNama());
                                                        itemName.setValue(adoption.getNama());
                                                    }
                                                });
                                            }
                                            else{
                                                chatItemVisible.setValue(false);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
        else if(!chat.getGambar().equals("")){
            //terdapat gambar
            storage.getPictureUrlFromName(chat.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    chatPic.setValue(uri.toString());
                }
            });
        }
        else if(!chat.getId_pj().equals("")){
            //terdapat pesanan/janjitemu
            orderDB.getPJById(chat.getId_pj()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getData() != null){
                        PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                        orderTotal.setValue(pj.getTotal());
                        orderStatus.setValue(pj.getStatusStr());
                        orderType.setValue(pj.getJenis());
                        itemKind.setValue(itemTypes[pj.getJenis()]);
                        if(pj.getJenis() < 3){
                            orderDB.getItemPJByPesanan(chat.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                                        DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                                        ItemPesananJanjitemu itemPj = new ItemPesananJanjitemu(firstDoc);
                                        orderItemName.setValue(itemPj.getNama());
                                        orderItemPic.setValue(itemPj.getGambar());
                                        orderQty.setValue(queryDocumentSnapshots.getDocuments().size());
                                        itemName.setValue(itemPj.getNama());
                                    }
                                }
                            });
                        }
                        else{
                            orderDB.getDetailJanjiTemu(chat.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                                        DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                                        DetailJanjiTemu janjiTemu = new DetailJanjiTemu(firstDoc);
                                        orderDate.setValue(janjiTemu.getTglJanjitemu());
                                        orderItemName.setValue(janjiTemu.getJenis_janjitemu());
                                        itemName.setValue(janjiTemu.getJenis_janjitemu());
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public String getReceiver(){
        return chat.getEmail_penerima();
    }

    public String getMessage(){
        return chat.getTeks();
    }

    public String getJam(){
        return chat.getJam();
    }

    public String getTanggal(){
        return chat.getTanggal();
    }

    public boolean isShown(){
        return chat.isShowDate();
    }

    public String getIdItem(){
        return chat.getId_item();
    }

    public String getIdPJ(){
        return chat.getId_pj();
    }

    public String getId(){
        return chat.getId_chat();
    }

    private MutableLiveData<Boolean> chatItemVisible = new MutableLiveData<>();

    private MutableLiveData<String> chatPic = new MutableLiveData<>("");
    private MutableLiveData<String> productName = new MutableLiveData<>("");
    private MutableLiveData<String> productPic = new MutableLiveData<>("");
    private MutableLiveData<Integer> productPrice = new MutableLiveData<>();

    private MutableLiveData<String> itemName = new MutableLiveData<>("");

    private MutableLiveData<String> adoptName = new MutableLiveData<>("");
    private MutableLiveData<String> adoptDesc = new MutableLiveData<>("");
    private MutableLiveData<String> adoptPic = new MutableLiveData<>();

    private MutableLiveData<String> hotelName = new MutableLiveData<>("");
    private MutableLiveData<String> hotelPic = new MutableLiveData<>("");
    private MutableLiveData<Integer> hotelPrice = new MutableLiveData<>();

    private MutableLiveData<Integer> orderTotal = new MutableLiveData<>();
    private MutableLiveData<Integer> orderQty = new MutableLiveData<>(0);
    private MutableLiveData<String> orderItemPic = new MutableLiveData<>("");
    private MutableLiveData<String> orderStatus = new MutableLiveData<>("");
    private MutableLiveData<String> orderItemName = new MutableLiveData<>("");
    private MutableLiveData<Integer> orderType = new MutableLiveData<>();
    private MutableLiveData<String> orderDate = new MutableLiveData<>("");
    private MutableLiveData<String> itemKind = new MutableLiveData<>("");

    public LiveData<String> getChatPic(){
        return chatPic;
    }

    public LiveData<String> getProductName(){
        return productName;
    }

    public LiveData<String> getProductPic(){
        return productPic;
    }

    public LiveData<Integer> getProductPrice(){
        return productPrice;
    }

    public LiveData<String> getAdoptName(){
        return adoptName;
    }

    public LiveData<String> getAdoptDesc(){
        return adoptDesc;
    }

    public LiveData<String> getAdoptPic(){
        return adoptPic;
    }

    public LiveData<String> getHotelName(){
        return hotelName;
    }

    public LiveData<String> getHotelPic(){
        return hotelPic;
    }

    public LiveData<Integer> getHotelPrice(){
        return hotelPrice;
    }

    public LiveData<Integer> getOrderTotal() {
        return orderTotal;
    }

    public LiveData<Integer> getOrderQty() {
        return orderQty;
    }

    public LiveData<String> getOrderItemPic() {
        return orderItemPic;
    }

    public LiveData<String> getOrderStatus() {
        return orderStatus;
    }

    public LiveData<String> getOrderItemName() {
        return orderItemName;
    }

    public LiveData<String> getOrderItemDate() {
        return orderDate;
    }

    public LiveData<String> getItemKind() {
        return itemKind;
    }

    public LiveData<Integer> getOrderType() {
        return orderType;
    }

    public LiveData<String> getItemName(){
        return itemName;
    }

    public LiveData<Boolean> getChatItemVisible() {
        return chatItemVisible;
    }
}
