package my.istts.finalproject.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ChatConvDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<DocumentSnapshot> getChatRoom(String id_room){
        return firebaseDb.collection("obrolan")
                .document(id_room)
                .get();
    }

    public Task<QuerySnapshot> findChatRoom(String email_pengirim, String email_penerima){
        return firebaseDb.collection("obrolan")
                .whereEqualTo("email_pengirim", email_pengirim)
                .whereEqualTo("email_penerima", email_penerima)
                .get();
    }

    public Task<QuerySnapshot> getUnreadChats(String email){
        return firebaseDb.collection("pesan")
                .whereEqualTo("email_penerima", email)
                .whereEqualTo("dibaca", false)
                .get();
    }

    public Query getAllConversations(){
        return firebaseDb.collection("obrolan");
    }

    public Query getLatestChat(String id_room){
        return firebaseDb.collection("pesan")
                .whereEqualTo("id_obrolan", id_room)
                .orderBy("waktu", Query.Direction.DESCENDING)
                .limit(1);
    }

    public Task<QuerySnapshot> getChats(String id_room){
        return firebaseDb.collection("pesan")
                .whereEqualTo("id_obrolan", id_room)
                .orderBy("waktu", Query.Direction.ASCENDING)
                .get();
    }

    public Task<DocumentReference> addChat(Chat chat, String email, String id_chatroom){
        Map<String, Object> chatInput = new HashMap<>();
        chatInput.put("dibaca", false);
        chatInput.put("gambar", chat.getGambar());
        chatInput.put("email_penerima", chat.getEmail_penerima());
        chatInput.put("email_pengirim", email);
        chatInput.put("id_obrolan", id_chatroom);
        chatInput.put("id_item", chat.getId_item());
        chatInput.put("id_pj", chat.getId_pj());
        chatInput.put("teks", chat.getTeks());
        chatInput.put("waktu", Timestamp.now());

        return firebaseDb.collection("pesan").add(chatInput);
    }

    public Task<DocumentReference> addChatRoom(String email_pengirim, String email_penerima){
        Map<String, Object> chatroom = new HashMap<>();
        chatroom.put("email_penerima", email_penerima);
        chatroom.put("email_pengirim", email_pengirim);

        return firebaseDb.collection("obrolan").add(chatroom);
    }

    public void readChat(String id){
        Map<String, Object> chatInput = new HashMap<>();
        chatInput.put("dibaca", true);

        firebaseDb.collection("pesan")
                .document(id)
                .set(chatInput, SetOptions.merge());
    }
}
