package com.example.sellerapp.models;

import com.example.sellerapp.inputclasses.ProductInput;
import com.example.sellerapp.viewmodels.itemviewmodels.VariantProductViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<DocumentSnapshot> getProductById(String id_produk){
        return firebaseDb.collection("produk")
                .document(id_produk)
                .get();
    }

    public Task<QuerySnapshot> getAllProducts(String email){
        return firebaseDb.collection("produk")
                .whereEqualTo("email_penjual", email)
                .get();
    }

    public Task<QuerySnapshot> getProducts(String email, String sortedField, Query.Direction sortType, Boolean aktif){
        Query queryTask = firebaseDb.collection("produk")
                .whereEqualTo("email_penjual", email);

        if(aktif != null){
            queryTask = queryTask.whereEqualTo("aktif", aktif);
        }
        if(sortedField != null){
            queryTask = queryTask.orderBy(sortedField, sortType);
        }

        return queryTask.get();
    }

    public Task<DocumentSnapshot> getVariantsById(String id_variasi){
        return firebaseDb.collection("variasi_produk")
                .document(id_variasi)
                .get();
    }

    public Task<QuerySnapshot> getVariants(String id_produk){
        return firebaseDb.collection("variasi_produk")
                .whereEqualTo("id_produk", id_produk)
                .get();
    }

    public void incProductSold(String id_produk, int jumlah){
        firebaseDb.collection("produk")
                .document(id_produk).update("terjual", FieldValue.increment(jumlah));
    }

    public Task<Void> deleteVariant(String id_varian){
        return firebaseDb.collection("variasi_produk")
                .document(String.valueOf(id_varian))
                .delete();
    }

    public Task<Void> deleteProduct(String id_produk){
        return firebaseDb.collection("produk")
                .document(String.valueOf(id_produk))
                .delete();
    }

    public Task<DocumentReference> addProduct(ProductInput input){
        Map<String, Object> produk = new HashMap<>();
        produk.put("email_penjual", input.getemailPenjual());
        produk.put("nama", input.nama.getValue());
        produk.put("kategori", input.getKategori().getValue());
        produk.put("harga", Integer.parseInt(input.harga.getValue()));
        produk.put("ulasan", 0);
        produk.put("stok", Integer.parseInt(input.stok.getValue()));
        produk.put("berat", Integer.parseInt(input.berat.getValue()));
        produk.put("deskripsi", input.deskripsi.getValue());
        produk.put("daftar_gambar", combineArrayIntoString(input.getPictureUrl()));
        produk.put("aktif", input.isAktif());
        produk.put("tanggal_upload", new Timestamp(new Date()));
        produk.put("terjual", 0);
        produk.put("dilihat", 0);

        return firebaseDb.collection("produk")
                .add(produk);
    }

    public Task<Void> activateProduct(boolean active, String id){
        Map<String, Object> produk = new HashMap<>();
        produk.put("aktif", active);
        produk.put("tanggal_upload", new Timestamp(new Date()));

        return firebaseDb.collection("produk")
                .document(id)
                .set(produk, SetOptions.merge());
    }

    public Task<Void> updateProduct(ProductInput input, String id){
        Map<String, Object> produk = new HashMap<>();
        produk.put("nama", input.nama.getValue());
        produk.put("kategori", input.getKategori().getValue());
        produk.put("harga", Integer.parseInt(input.harga.getValue()));
        produk.put("stok", Integer.parseInt(input.stok.getValue()));
        produk.put("berat", Integer.parseInt(input.berat.getValue()));
        produk.put("deskripsi", input.deskripsi.getValue());
        produk.put("daftar_gambar", combineArrayIntoString(input.getPictureUrl()));
        produk.put("tanggal_upload", new Timestamp(new Date()));

        return firebaseDb.collection("produk")
                .document(id)
                .set(produk, SetOptions.merge());
    }

    public Task<DocumentReference> addVariantProduct(VariantProductViewModel input, String gambar){
        Map<String, Object> varian = new HashMap<>();
        varian.put("id_produk", input.getIdProduk());
        varian.put("gambar", gambar);
        varian.put("nama", input.getNama());
        varian.put("harga", Integer.parseInt(input.getHarga()));
        varian.put("stok", Integer.parseInt(input.getStok()));

        return firebaseDb.collection("variasi_produk")
                .add(varian);
    }

    public Task<Void> updateVariantProduct(VariantProductViewModel input, String gambar){
        Map<String, Object> varian = new HashMap<>();
        varian.put("id_produk", input.getIdProduk());
        varian.put("gambar", gambar);
        varian.put("nama", input.getNama());
        varian.put("harga", Integer.parseInt(input.getHarga()));
        varian.put("stok", Integer.parseInt(input.getStok()));

        return firebaseDb.collection("variasi_produk")
                .document(input.getIdVariant())
                .set(varian);
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

}
