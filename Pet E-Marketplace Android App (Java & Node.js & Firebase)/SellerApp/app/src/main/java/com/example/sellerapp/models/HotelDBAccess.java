package com.example.sellerapp.models;

import com.example.sellerapp.inputclasses.HotelInput;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HotelDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<DocumentSnapshot> getRoomById(String id_hotel){
        return firebaseDb.collection("kamar")
                .document(id_hotel)
                .get();
    }

    public Task<QuerySnapshot> getHotelRooms(String email){
        return firebaseDb.collection("kamar")
                .whereEqualTo("email_pemilik", email)
                .get();
    }

    public Task<QuerySnapshot> getHotelRoomsFiltered(String email, String sortedField, Query.Direction sortType, Boolean aktif){
        Query queryTask = firebaseDb.collection("kamar")
                .whereEqualTo("email_pemilik", email);

        if(aktif != null){
            queryTask = queryTask.whereEqualTo("aktif", aktif);
        }
        if(sortedField != null){
            queryTask = queryTask.orderBy(sortedField, sortType);
        }

        return queryTask.get();
    }

    public Task<Void> activateRoom(boolean active, String id){
        Map<String, Object> hotel = new HashMap<>();
        hotel.put("aktif", active);

        return firebaseDb.collection("kamar")
                .document(id)
                .set(hotel, SetOptions.merge());
    }

    public Task<Void> deleteRoom(String id_kamar){
        return firebaseDb.collection("kamar")
                .document(id_kamar)
                .delete();
    }

    public Task<DocumentReference> addKamar(HotelInput input){
        Map<String, Object> kamar = new HashMap<>();
        kamar.put("email_pemilik", input.getemailPemilik());
        kamar.put("nama", input.nama.getValue());
        kamar.put("harga", Integer.parseInt(input.harga.getValue()));
        kamar.put("panjang", Integer.parseInt(input.panjang.getValue()));
        kamar.put("lebar", Integer.parseInt(input.lebar.getValue()));
        kamar.put("total", Integer.parseInt(input.jumlah.getValue()));
        kamar.put("deskripsi", input.deskripsi.getValue());
        kamar.put("daftar_gambar", combineArrayIntoString(input.getPictureId()));
        kamar.put("fasilitas", combineBoolIntoString(input.fasilitas.getValue()));
        kamar.put("aktif", input.isAktif());
        kamar.put("tanggal_upload", new Timestamp(new Date()));
        kamar.put("sedang_disewa", 0);
        kamar.put("tersewa", 0);
        kamar.put("dilihat", 0);
        kamar.put("ulasan", 0);

        return firebaseDb.collection("kamar")
                .add(kamar);
    }

    public Task<Void> updateKamar(HotelInput input, String id){
        Map<String, Object> kamar = new HashMap<>();
        kamar.put("nama", input.nama.getValue());
        kamar.put("harga", Integer.parseInt(input.harga.getValue()));
        kamar.put("panjang", Integer.parseInt(input.panjang.getValue()));
        kamar.put("lebar", Integer.parseInt(input.lebar.getValue()));
        kamar.put("deskripsi", input.deskripsi.getValue());
        kamar.put("daftar_gambar", combineArrayIntoString(input.getPictureId()));
        kamar.put("fasilitas", combineBoolIntoString(input.fasilitas.getValue()));

        return firebaseDb.collection("kamar")
                .document(id)
                .set(kamar, SetOptions.merge());
    }

    private String combineArrayIntoString(String[] array){
        String combined = "";
        for (int i = 0; i < array.length; i++) {
            if(array[i] == null){
                array[i] = "";
            }
            if(combined.equals("")){
                combined += array[i];
            }
            else{
                combined += "|"+array[i];
            }
        }
        return combined;
    }

    private String combineBoolIntoString(boolean[] array){
        String combined = "";
        for (int i = 0; i < array.length; i++) {
            if(array[i]){
                if(combined.equals("")){
                    combined += ""+i;
                }
                else{
                    combined += "|"+i;
                }
            }
        }
        return combined;
    }
}
