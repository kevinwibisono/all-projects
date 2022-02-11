package com.example.sellerapp.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sellerapp.models.Akun;
import com.example.sellerapp.models.AkunDBAccess;
import com.example.sellerapp.models.Chat;
import com.example.sellerapp.models.ChatConvDBAccess;
import com.example.sellerapp.models.Conversation;
import com.example.sellerapp.models.DetailJanjiTemu;
import com.example.sellerapp.models.DetailPenjual;
import com.example.sellerapp.models.DetailPenjualDBAccess;
import com.example.sellerapp.models.Hotel;
import com.example.sellerapp.models.HotelDBAccess;
import com.example.sellerapp.models.ItemPesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemu;
import com.example.sellerapp.models.PesananJanjitemuDBAccess;
import com.example.sellerapp.models.Product;
import com.example.sellerapp.models.ProductDBAccess;
import com.example.sellerapp.models.backend.BackendRetrofitService;
import com.example.sellerapp.models.backend.BackendRetrofitClient;
import com.example.sellerapp.models.Storage;
import com.example.sellerapp.viewmodels.itemviewmodels.ChatItemViewModel;
import com.example.sellerapp.viewmodels.itemviewmodels.HotelItemViewModel;
import com.example.sellerapp.viewmodels.itemviewmodels.PesananItemViewModel;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;
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
    private AkunDBAccess akunDBAccess;
    private DetailPenjualDBAccess detailDB;
    private Storage storage;
    private Application app;
    private BackendRetrofitService firebaseNotifService;

    public ChatViewModel(Application application) {
        chatDB = new ChatConvDBAccess();
        orderDB = new PesananJanjitemuDBAccess();
        akunDBAccess = new AkunDBAccess(application);
        detailDB = new DetailPenjualDBAccess(application);
        productDB = new ProductDBAccess();
        hotelDB = new HotelDBAccess();
        storage = new Storage();
        firebaseNotifService = BackendRetrofitClient.getRetrofitInstance().create(BackendRetrofitService.class);
        this.app = application;
    }

    //email adalah email seller yg sedang melihat skrg
    //email_friend adalah lawan bicara seller sekaligus menjadi email penerima utk setiap chat yang akan dikirimkan
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
    private MutableLiveData<Boolean> itemShown = new MutableLiveData<>(false);

    private MutableLiveData<Boolean> personOnline = new MutableLiveData<>(false);
    private MutableLiveData<String> personName = new MutableLiveData<>("");
    private MutableLiveData<String> personPicture = new MutableLiveData<>("");

    private MutableLiveData<Boolean> sending = new MutableLiveData<>();
    public MutableLiveData<String> chatText = new MutableLiveData<>("");
    public MutableLiveData<ChatItemViewModel> latestChat = new MutableLiveData<>();

    public MutableLiveData<String> searchKeyword = new MutableLiveData<>("");
    private MutableLiveData<Integer> sellerRole = new MutableLiveData<>();

    public String getEmail(){
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

    public LiveData<Integer> getChosenItemOrderType(){
        return chosenItemOrderType;
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

    public LiveData<Boolean> isItemShown(){
        return itemShown;
    }

    public LiveData<Boolean> isPersonOnline(){
        return personOnline;
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
                            if(email.equals(chatRoom.getemail_penerima())){
                                email_friend = chatRoom.getemail_pengirim();
                            }
                            else{
                                email_friend = chatRoom.getemail_penerima();
                            }

                            detailDB.setGetDetailCompleteListener(new DetailPenjualDBAccess.onCompleteGetDetailListener() {
                                @Override
                                public void onDetailComplete(List<DetailPenjual> detailsGot) {
                                    if(detailsGot.size() > 0){
                                        DetailPenjual detailPenjual = detailsGot.get(0);
                                        sellerRole.setValue(detailPenjual.getRole());
                                    }
                                }
                            });
                            detailDB.getLocalDetail();

                            akunDBAccess.getAccByEmail(email_friend).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.getData() != null){
                                        personOnline.setValue(documentSnapshot.getBoolean("online"));

                                        //dapatkan data lawan bicara
                                        personName.setValue(documentSnapshot.getString("nama"));
                                        storage.getPictureUrlFromName(email_friend).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                personPicture.setValue(uri.toString());
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

                    detailDB.setGetDetailCompleteListener(new DetailPenjualDBAccess.onCompleteGetDetailListener() {
                        @Override
                        public void onDetailComplete(List<DetailPenjual> detailsGot) {
                            if(detailsGot.size() > 0){
                                DetailPenjual detailPenjual = detailsGot.get(0);
                                sellerRole.setValue(detailPenjual.getRole());
                            }
                        }
                    });
                    detailDB.getLocalDetail();

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
                                                        personOnline.setValue(documentSnapshot.getBoolean("online"));

                                                        //dapatkan data lawan bicara
                                                        personName.setValue(documentSnapshot.getString("nama"));
                                                        storage.getPictureUrlFromName(email_friend).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                personPicture.setValue(uri.toString());
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
            currentChats.add(new ChatItemViewModel(c, app));
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
                            latestChat.setValue(new ChatItemViewModel(latest, app));
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
                    storage.uploadPictureUrl(picName);
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
        productDB.getAllProducts(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                prodVMs.setValue(new ArrayList<>());
                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                if(results.size() > 0){
                    for (int i = 0; i < results.size(); i++) {
                        //query nama
                        if(results.get(i).getString("nama").toLowerCase().contains(searchKeyword.getValue().toLowerCase())){
                            addNewProduct(new Product(results.get(i)));
                        }
                        if (i == results.size() - 1){
                            itemsLoading.setValue(false);
                        }
                    }
                }
                else{
                    itemsLoading.setValue(false);
                }
            }
        });
    }

    private void addNewProduct(Product p){
        ArrayList<ProductItemViewModel> currentVMs = prodVMs.getValue();
        currentVMs.add(new ProductItemViewModel(p));
        prodVMs.setValue(currentVMs);
    }

    public void setChosenProduct(String id){
        id_item = id;

        productDB.getProductById(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
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
        });
    }

    public void getHotelRooms(){
        itemsLoading.setValue(true);
        hotelDB.getHotelRooms(email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                hotelVMs.setValue(new ArrayList<>());
                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                if(results.size() > 0){
                    for (int i = 0; i < results.size(); i++) {
                        //query nama
                        if(results.get(i).getString("nama").toLowerCase().contains(searchKeyword.getValue().toLowerCase())){
                            addNewHotel(new Hotel(results.get(i)));
                        }
                        if (i == results.size() - 1){
                            itemsLoading.setValue(false);
                        }
                    }
                }
                else{
                    itemsLoading.setValue(false);
                }
            }
        });
    }

    private void addNewHotel(Hotel h){
        ArrayList<HotelItemViewModel> currentVMs = hotelVMs.getValue();
        currentVMs.add(new HotelItemViewModel(h));
        hotelVMs.setValue(currentVMs);
    }

    public void setChosenHotel(String id){
        id_item = id;

        hotelDB.getRoomById(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
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
        });
    }

    public void getConnectedOrders(){
        itemsLoading.setValue(true);
        orderDB.getConnectedOrders(email_friend, email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderVMs.setValue(new ArrayList<>());
                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                if(results.size() > 0){
                    for (int i = 0; i < results.size(); i++) {
                        //query nama
                        addNewOrder(new PesananJanjitemu(results.get(i)));
                        if (i == results.size() - 1){
                            itemsLoading.setValue(false);
                        }
                    }
                }
                else{
                    itemsLoading.setValue(false);
                }
            }
        });
    }

    private void addNewOrder(PesananJanjitemu order){
        if(order.getJenis() < 3){
            if(order.getStatus() > 0){
                ArrayList<PesananItemViewModel> currentVMs = orderVMs.getValue();
                currentVMs.add(new PesananItemViewModel(order));
                orderVMs.setValue(currentVMs);
            }
        }
        else{
            ArrayList<PesananItemViewModel> currentVMs = orderVMs.getValue();
            currentVMs.add(new PesananItemViewModel(order));
            orderVMs.setValue(currentVMs);
        }
    }

    public void setChosenOrder(String id){
        id_pj = id;

        orderDB.getPJById(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
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
        });
    }
}
