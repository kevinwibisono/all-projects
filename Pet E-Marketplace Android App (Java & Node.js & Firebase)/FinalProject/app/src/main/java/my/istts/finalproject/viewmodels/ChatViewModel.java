package my.istts.finalproject.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Adoption;
import my.istts.finalproject.models.AdoptionDBAccess;
import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Chat;
import my.istts.finalproject.models.ChatConvDBAccess;
import my.istts.finalproject.models.Conversation;
import my.istts.finalproject.models.DetailJanjiTemu;
import my.istts.finalproject.models.DetailPenjualDBAccess;
import my.istts.finalproject.models.Hotel;
import my.istts.finalproject.models.HotelDBAccess;
import my.istts.finalproject.models.ItemPesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemu;
import my.istts.finalproject.models.PesananJanjitemuDBAccess;
import my.istts.finalproject.models.Product;
import my.istts.finalproject.models.ProductDBAccess;
import my.istts.finalproject.models.backend.BackendRetrofitService;
import my.istts.finalproject.models.backend.BackendRetrofitClient;
import my.istts.finalproject.models.Storage;
import my.istts.finalproject.viewmodels.itemviewmodels.ChatItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.PesananItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatViewModel {
    private ChatConvDBAccess chatDB;
    private PesananJanjitemuDBAccess orderDB;
    private ProductDBAccess productDB;
    private HotelDBAccess hotelDB;
    private AdoptionDBAccess adoptDB;
    private DetailPenjualDBAccess detailDB;
    private AkunDBAccess akunDBAccess;
    private Storage storage;
    private BackendRetrofitService firebaseNotifService;
    private Application app;

    public ChatViewModel(Application application) {
        this.app = application;
        chatDB = new ChatConvDBAccess();
        orderDB = new PesananJanjitemuDBAccess();
        detailDB = new DetailPenjualDBAccess();
        akunDBAccess = new AkunDBAccess(application);
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        adoptDB = new AdoptionDBAccess();
        storage = new Storage();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
    }

    //nohp adalah nohp seller yg sedang melihat skrg
    //hppengirim adalah lawan bicara seller sekaligus menjadi hp penerima utk setiap chat yang akan dikirimkan
    private String email, email_friend;
    private Conversation chatRoom;
    private String id_pj = "", id_item = "", lastDate = "";
    private String[] types = {"Produk", "Paket", "Kamar", "Klinik"};
    private  ArrayList<Chat> listChats;
    private Bitmap uploadedPic;

    private MutableLiveData<String> chosenItemName = new MutableLiveData<>("");
    private MutableLiveData<Integer> chosenItemPrice = new MutableLiveData<>(0);
    private MutableLiveData<String> chosenItemPic = new MutableLiveData<>("");
    private MutableLiveData<String> chosenItemStatus = new MutableLiveData<>("");
    private MutableLiveData<String> chosenItemDate = new MutableLiveData<>("");
    private MutableLiveData<Integer> chosenItemOrderType = new MutableLiveData<>();
    private MutableLiveData<String> itemKind = new MutableLiveData<>("");
    private MutableLiveData<Integer> chosenItemQty = new MutableLiveData<>(0);

    private MutableLiveData<ArrayList<ChatItemViewModel>> chatVMs;
    private MutableLiveData<ArrayList<ProductItemViewModel>> prodVMs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HotelItemViewModel>> hotelVMs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<PesananItemViewModel>> orderVMs = new MutableLiveData<>();

    private MutableLiveData<Boolean> chatLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> itemsLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> nextItemsLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> itemShown = new MutableLiveData<>(false);

    private MutableLiveData<String> personName = new MutableLiveData<>("");
    private MutableLiveData<String> personPicture = new MutableLiveData<>("");

    private MutableLiveData<Boolean> sending = new MutableLiveData<>();
    public MutableLiveData<String> chatText = new MutableLiveData<>("");
    public MutableLiveData<ChatItemViewModel> latestChat = new MutableLiveData<>();

    public MutableLiveData<String> searchKeyword = new MutableLiveData<>("");
    private MutableLiveData<Integer> sellerRole = new MutableLiveData<>();

    public String getNoHp(){
        return email;
    }

    public LiveData<ArrayList<ChatItemViewModel>> getChatVMs(){
        if(chatVMs == null){
            chatVMs = new MutableLiveData<>(new ArrayList<>());
        }
        return chatVMs;
    }

    public LiveData<Integer> getRole(){
        return sellerRole;
    }

    public LiveData<String> getPersonName(){
        return personName;
    }

    public LiveData<String> getPersonPic(){
        return personPicture;
    }

    public LiveData<ChatItemViewModel> getLatestChat(){
        return latestChat;
    }

    public LiveData<String> getChosenItemName(){
        return chosenItemName;
    }

    public LiveData<Integer> getChosenItemPrice(){
        return chosenItemPrice;
    }

    public LiveData<Integer> getChosenItemQty(){
        return chosenItemQty;
    }

    public LiveData<String> getChosenItemStatus(){
        return chosenItemStatus;
    }

    public LiveData<String> getChosenItemPic(){
        return chosenItemPic;
    }

    public LiveData<String> getChosenItemDate(){
        return chosenItemDate;
    }

    public LiveData<Integer> getChosenItemOrderType(){
        return chosenItemOrderType;
    }

    public LiveData<String> getItemKind(){
        return itemKind;
    }

    public void setItemShown(Boolean show){
        itemShown.setValue(show);
    }

    public LiveData<Boolean> isSending(){
        return sending;
    }

    public LiveData<Boolean> isChatLoading(){
        return chatLoading;
    }

    public LiveData<Boolean> isItemsLoading(){
        return itemsLoading;
    }

    public LiveData<Boolean> isNextItemsLoading(){
        return nextItemsLoading;
    }

    public LiveData<Boolean> isItemShown(){
        return itemShown;
    }

    public LiveData<ArrayList<ProductItemViewModel>> getProductVMs(){
        return prodVMs;
    }

    public LiveData<ArrayList<HotelItemViewModel>> getHotelVMs(){
        return hotelVMs;
    }

    public LiveData<ArrayList<PesananItemViewModel>> getOrderVMs(){
        return orderVMs;
    }

    public void setUploadedPic(Bitmap b){
        uploadedPic = b;
    }

    //jika diakses dari conversation activity, sdh ada id_room nya
    public void getAllChatsInCR(String id_room){
        chatLoading.setValue(true);

        chatDB.getChatRoom(id_room).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                chatRoom = new Conversation(documentSnapshot);

                akunDBAccess.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
                    @Override
                    public void onComplete(List<Akun> accountsGot) {
                        if(accountsGot.size() > 0){
                            email = accountsGot.get(0).getEmail();
                            if(chatRoom.getemail_penerima().equals(email)){
                                email_friend = chatRoom.getemail_pengirim();
                            }
                            else{
                                email_friend = chatRoom.getemail_penerima();
                            }

                            akunDBAccess.getAccByEmail(email_friend).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.getData() != null){
                                        //dapatkan data lawan bicara
                                        personName.setValue(documentSnapshot.getString("nama"));
                                        storage.getPictureUrlFromName(email_friend).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                personPicture.setValue(uri.toString());
                                            }
                                        });

                                        detailDB.getDBAkunDetail(email_friend).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot sellerDoc) {
                                                if(sellerDoc.getData() != null){
                                                    sellerRole.setValue(sellerDoc.getLong("tipe").intValue());
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            chatDB.getChats(id_room).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    chatVMs.setValue(new ArrayList<>());
                                    List<DocumentSnapshot> chatsInDB = queryDocumentSnapshots.getDocuments();
                                    listChats = new ArrayList<>();
                                    if(chatsInDB.size() > 0){
                                        for (int i = 0; i < chatsInDB.size()-1; i++) {
                                            Chat currentChat = new Chat(chatsInDB.get(i));
                                            if(currentChat.getTanggal().equals(lastDate)){
                                                currentChat.setShowDate(false);
                                            }
                                            else{
                                                currentChat.setShowDate(true);
                                                lastDate = currentChat.getTanggal();
                                            }
                                            listChats.add(currentChat);
                                        }
                                        addChatToVM(listChats);
                                    }
                                    else{
                                        chatLoading.setValue(false);
                                    }
                                }
                            });
                        }
                    }
                });
                akunDBAccess.getSavedAccounts();
            }
        });
    }

    //jika diakses dari halaman produk/kamar/groomer/klinik
    //belum tentu ada chatroomnya
    public void getChatRoom(String lawan_bicara){
        email_friend = lawan_bicara;

        akunDBAccess.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    chatDB.findChatRoom(email, email_friend).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                //sudah pernah chat, sdh ada chatroom
                                //skrg tinggal ambil chat-chatnya
                                DocumentSnapshot crDoc = queryDocumentSnapshots.getDocuments().get(0);
                                getAllChatsInCR(crDoc.getId());
                            }
                            else{
                                chatDB.findChatRoom(email_friend, email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if(queryDocumentSnapshots.getDocuments().size() > 0){
                                            //sudah pernah chat, sdh ada chatroom
                                            //skrg tinggal ambil chat-chatnya
                                            DocumentSnapshot crDoc = queryDocumentSnapshots.getDocuments().get(0);
                                            getAllChatsInCR(crDoc.getId());
                                        }
                                        else{
                                            //belum pernah chat, jadi tidak mengambil list chat
                                            //cari tau dulu lawan bicara
                                            akunDBAccess.getAccByEmail(email_friend).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if(documentSnapshot.getData() != null){
                                                        //dapatkan data lawan bicara
                                                        personName.setValue(documentSnapshot.getString("nama"));
                                                        storage.getPictureUrlFromName(email_friend).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                personPicture.setValue(uri.toString());
                                                            }
                                                        });

                                                        detailDB.getDBAkunDetail(email_friend).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot sellerDoc) {
                                                                if(sellerDoc.getData() != null){
                                                                    sellerRole.setValue(sellerDoc.getLong("tipe").intValue());
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
                        }
                    });
                }
            }
        });

        akunDBAccess.getSavedAccounts();
    }

    private void addChatToVM(ArrayList<Chat> chats){
        ArrayList<ChatItemViewModel> currentChats = chatVMs.getValue();
        for (Chat c:
             chats) {
            currentChats.add(new ChatItemViewModel(c));
        }
        chatVMs.setValue(currentChats);
        chatLoading.setValue(false);
        contLatestChat();
    }

    private void contLatestChat(){
        //setiap kali ada chat baru, cek dulu apakah sudah ada di kumpulan itemviewmodel
        //jika benar benar baru, baru tambahkan ke list
        if(chatRoom != null){
            chatDB.getLatestChat(chatRoom.getId_chatroom()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.getDocuments().size() > 0){
                        Chat latest = new Chat(value.getDocuments().get(0));
                        boolean newChat = true;
                        for (Chat c : listChats) {
                            if(c.getId_chat().equals(latest.getId_chat())){
                                newChat = false;
                            }
                        }
                        if(newChat){
                            if(latest.getTanggal().equals(lastDate)){
                                latest.setShowDate(false);
                            }
                            else{
                                latest.setShowDate(true);
                            }
                            lastDate = latest.getTanggal();
                            listChats.add(latest);
                            latestChat.setValue(new ChatItemViewModel(latest));
                        }
                    }
                }
            });
        }
    }

    public void readMyChats(){
        if(chatRoom != null){
            chatDB.getChats(chatRoom.getId_chatroom()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> roomChats = queryDocumentSnapshots.getDocuments();
                    if(roomChats.size() > 0){
                        for (DocumentSnapshot chatDoc:
                                roomChats) {
                            if(chatDoc.getString("email_penerima").equals(email) && !chatDoc.getBoolean("dibaca")){
                                chatDB.readChat(chatDoc.getId());
                            }
                        }
                    }
                }
            });
        }
    }

    public void beginSendChat(){
        //cari tau dulu apakah ada objek chatroom yang didapat diawal
        //jika tidak ada berarti chatroom belum dibentuk
        //pada saat mengirim chat pertama kali sekaligus buat data chatroom baru
        sending.setValue(true);
        if(chatRoom == null){
            chatDB.addChatRoom(email, email_friend).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            chatRoom = new Conversation(documentSnapshot);
                            sendChat(true);
                        }
                    });
                }
            });
        }
        else{
            sendChat(false);
        }
    }

    private void sendChat(boolean first){
        Chat sentChat = new Chat(chatText.getValue(), "", id_pj, id_item, email_friend);
        String topikEmail = email_friend.substring(0, email_friend.indexOf('@'));
        firebaseNotifService.sendNotif("Kamu Mendapat Chat Baru", chatText.getValue(), topikEmail);
        chatText.setValue("");
        itemShown.setValue(false);
        if(uploadedPic != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            uploadedPic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //upload gambar
            String picName = UUID.nameUUIDFromBytes(data).toString();
            storage.uploadPicture(data, picName).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sentChat.setGambar(picName);
                    chatDB.addChat(sentChat, email, chatRoom.getId_chatroom()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if(first){
                                getAllChatsInCR(chatRoom.getId_chatroom());
                            }
                            sending.setValue(false);
                            clearChatItems();
                        }
                    });
                }
            });
        }
        else{
            chatDB.addChat(sentChat, email, chatRoom.getId_chatroom()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    if(first){
                        Toast.makeText(app, "First Chat", Toast.LENGTH_SHORT).show();
                        getAllChatsInCR(chatRoom.getId_chatroom());
                    }
                    sending.setValue(false);
                    clearChatItems();
                }
            });
        }
    }

    public void clearChatItems(){
        id_item = "";
        id_pj = "";
        uploadedPic = null;
        itemShown.setValue(false);
    }

    public void getSellerProducts(){
        itemsLoading.setValue(true);
        prodVMs.setValue(new ArrayList<>());

        productDB.getAllSellerProducts(email_friend).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Product> products = new ArrayList<>();
                for (DocumentSnapshot productDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    Product product = new Product(productDoc);
                    if(product.getNama().toLowerCase().contains(searchKeyword.getValue().toLowerCase())){
                        products.add(product);
                    }
                }
                addNewProducts(products);
            }
        });
    }


    private void addNewProducts(ArrayList<Product> products){
        ArrayList<ProductItemViewModel> currentVMs = prodVMs.getValue();
        for (Product product:
             products) {
            currentVMs.add(new ProductItemViewModel(product));
        }
        prodVMs.setValue(currentVMs);
        itemsLoading.setValue(false);
    }

    public void setChosenProduct(String id){
        id_item = id;

        productDB.getProductById(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Product p = new Product(documentSnapshot);
                    chosenItemName.setValue(p.getNama());
                    chosenItemPrice.setValue(p.getHarga());
                    storage.getPictureUrlFromName(p.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            chosenItemPic.setValue(uri.toString());
                        }
                    });
                }
            }
        });
    }

    public void getHotelRooms(){
        itemsLoading.setValue(true);
        hotelVMs.setValue(new ArrayList<>());

        hotelDB.getAllHotelsOwned(email_friend).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Hotel> hotels = new ArrayList<>();
                for (DocumentSnapshot hotelDoc:
                        queryDocumentSnapshots.getDocuments()) {
                    Hotel hotel = new Hotel(hotelDoc);
                    if(hotel.getNama().toLowerCase().contains(searchKeyword.getValue().toLowerCase())){
                        hotels.add(hotel);
                    }
                }
                addNewHotels(hotels);
            }
        });
    }


    private void addNewHotels(ArrayList<Hotel> hotels){
        ArrayList<HotelItemViewModel> currentVMs = hotelVMs.getValue();
        for (Hotel hotel:
                hotels) {
            currentVMs.add(new HotelItemViewModel(hotel));
        }
        hotelVMs.setValue(currentVMs);
        itemsLoading.setValue(false);
    }

    public void setChosenHotel(String id){
        id_item = id;

        hotelDB.getRoomById(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Hotel h = new Hotel(documentSnapshot);
                    chosenItemName.setValue(h.getNama());
                    chosenItemPrice.setValue(h.getHarga());
                    storage.getPictureUrlFromName(h.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            chosenItemPic.setValue(uri.toString());
                        }
                    });
                }
            }
        });
    }

    public void getConnectedOrders(){
        itemsLoading.setValue(true);
        orderVMs.setValue(new ArrayList<>());

        orderDB.getConnectedOrders(email, email_friend).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<PesananJanjitemu> orders = new ArrayList<>();
                for (DocumentSnapshot orderDoc:
                     queryDocumentSnapshots.getDocuments()) {
                    PesananJanjitemu pj = new PesananJanjitemu(orderDoc);
                    if(pj.getStatus() > 0){
                        orders.add(pj);
                    }
                }
                addNewOrders(orders);
            }
        });
    }

    private void addNewOrders(ArrayList<PesananJanjitemu> orders){
        ArrayList<PesananItemViewModel> currentVMs = orderVMs.getValue();
        for (PesananJanjitemu order:
             orders) {
            currentVMs.add(new PesananItemViewModel(order));
        }
        orderVMs.setValue(currentVMs);
        itemsLoading.setValue(false);
    }

    public void setChosenOrder(String id){
        id_pj = id;

        orderDB.getPJById(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    PesananJanjitemu pj = new PesananJanjitemu(documentSnapshot);
                    chosenItemStatus.setValue(pj.getStatusStr());
                    chosenItemPrice.setValue(pj.getTotal());
                    chosenItemOrderType.setValue(pj.getJenis());
                    itemKind.setValue(types[pj.getJenis()]);
                    if(pj.getJenis() < 3){
                        orderDB.getItemPJByPesanan(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                                    ItemPesananJanjitemu itemPJ = new ItemPesananJanjitemu(firstDoc);
                                    chosenItemName.setValue(itemPJ.getNama());
                                    chosenItemPic.setValue(itemPJ.getGambar());
                                    chosenItemQty.setValue(queryDocumentSnapshots.getDocuments().size());
                                }
                            }
                        });
                    }
                    else{
                        orderDB.getDetailJanjiTemu(pj.getId_pj()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() > 0){
                                    DocumentSnapshot firstDoc = queryDocumentSnapshots.getDocuments().get(0);
                                    DetailJanjiTemu janjiTemu = new DetailJanjiTemu(firstDoc);
                                    chosenItemName.setValue(janjiTemu.getJenis_janjitemu());
                                    chosenItemDate.setValue(janjiTemu.getTglJanjitemu());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void setChosenAdopt(String id){
        id_item = id;

        adoptDB.getPetAdoptById(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Adoption adoption = new Adoption(documentSnapshot);
                    chosenItemName.setValue(adoption.getNama());
                    chosenItemStatus.setValue(adoption.getJenisHewan()+" "+adoption.getJenisKelaminHewan());
                    storage.getPictureUrlFromName(adoption.getGambar()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            chosenItemPic.setValue(uri.toString());
                        }
                    });
                }
            }
        });
    }
}
