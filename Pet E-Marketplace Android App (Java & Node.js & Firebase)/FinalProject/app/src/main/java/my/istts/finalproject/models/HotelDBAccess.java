package my.istts.finalproject.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class HotelDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<DocumentSnapshot> getRoomById(String id_hotel){
        return firebaseDb.collection("kamar")
                .document(id_hotel)
                .get();
    }

    public Task<QuerySnapshot> getTenHotels(){
        return firebaseDb.collection("kamar")
                .whereEqualTo("aktif", true)
                .orderBy("tanggal_upload", Query.Direction.DESCENDING)
                .limit(10)
                .get();
    }

    public Task<QuerySnapshot> getAllHotelsOwned(String email){
        return firebaseDb.collection("kamar")
                .whereEqualTo("email_pemilik", email)
                .whereEqualTo("aktif", true)
                .get();
    }

    public Task<QuerySnapshot> getTopHotelRooms(String email){
        return firebaseDb.collection("kamar")
                .whereEqualTo("email_pemilik", email)
                .whereEqualTo("aktif", true)
                .orderBy("tersewa", Query.Direction.DESCENDING)
                .limit(5)
                .get();
    }

    public Task<QuerySnapshot> getTenHotelsOwned(String email){
        return firebaseDb.collection("kamar")
                .whereEqualTo("aktif", true)
                .whereEqualTo("email_pemilik", email)
                .limit(10)
                .get();
    }

    public Task<QuerySnapshot> getTenHotelsNotOwned(String email){
        return firebaseDb.collection("kamar")
                .whereEqualTo("aktif", true)
                .whereNotEqualTo("email_pemilik", email)
                .limit(10)
                .get();
    }

    public void incHotelReview(String id_hotel, int jumlah){
        firebaseDb.collection("kamar")
                .document(id_hotel).update("ulasan", FieldValue.increment(jumlah));
    }

    public void incHotelBooked(String id_hotel, int jumlah){
        firebaseDb.collection("kamar")
                .document(id_hotel).update("tersewa", FieldValue.increment(jumlah));
    }

    public void seeHotel(String id_hotel){
        firebaseDb.collection("kamar")
                .document(id_hotel).update("dilihat", FieldValue.increment(1));
    }

    public void addRoomOccupied(String id_item, int qty){
        firebaseDb.collection("kamar")
                .document(id_item)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Hotel hotel = new Hotel(documentSnapshot);
                    int qtyAfter = hotel.getSedang_disewa() + qty;
                    if(qtyAfter > hotel.getTotal()){
                        qtyAfter = hotel.getTotal();
                    }

                    Map<String, Object> room = new HashMap<>();
                    room.put("sedang_disewa", qtyAfter);

                    firebaseDb.collection("kamar")
                            .document(id_item)
                            .set(room, SetOptions.merge());
                }
            }
        });
    }

    public void reduceOccupiedRoomAfterCancel(String id_item, int qty){
        firebaseDb.collection("kamar")
                .document(id_item)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Hotel hotel = new Hotel(documentSnapshot);
                    int qtyAfter = hotel.getSedang_disewa() - qty;
                    if(qtyAfter < 0){
                        qtyAfter = 0;
                    }

                    Map<String, Object> room = new HashMap<>();
                    room.put("sedang_disewa", qtyAfter);

                    firebaseDb.collection("kamar")
                            .document(id_item)
                            .set(room, SetOptions.merge());
                }
            }
        });
    }

    public Task<QuerySnapshot> getHotelRoomsFiltered(String sortedField, Query.Direction sortType){
        Query queryTask = firebaseDb.collection("kamar");

        if(!sortedField.equals("")){
            queryTask = queryTask.orderBy(sortedField, sortType);
        }

        queryTask = queryTask.orderBy("tanggal_upload", Query.Direction.DESCENDING);


        return queryTask
                .get();
    }
}
