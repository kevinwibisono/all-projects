package my.istts.finalproject.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDBAccess {
    private FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();

    public Task<DocumentSnapshot> getProductById(String id_produk){
        return firebaseDb.collection("produk")
                .document(id_produk)
                .get();
    }

    public Task<QuerySnapshot> getTenProducts(){
        Query searchQuery = firebaseDb.collection("produk")
                .whereEqualTo("aktif", true)
                .orderBy("tanggal_upload", Query.Direction.DESCENDING)
                .limit(10);

        return searchQuery.get();
    }

    public Task<QuerySnapshot> getTenProductsNotOwned(String email){
        Query searchQuery = firebaseDb.collection("produk")
                .whereEqualTo("aktif", true)
                .whereNotEqualTo("email_penjual", email)
                .orderBy("email_penjual", Query.Direction.ASCENDING)
                .orderBy("tanggal_upload", Query.Direction.DESCENDING)
                .limit(10);

        return searchQuery.get();
    }

    public Task<QuerySnapshot> getTenProductsOwned(String email){
        Query searchQuery = firebaseDb.collection("produk")
                .whereEqualTo("aktif", true)
                .whereEqualTo("email_penjual", email)
                .orderBy("tanggal_upload", Query.Direction.DESCENDING)
                .limit(10);

        return searchQuery.get();
    }

    public Task<QuerySnapshot> getAllSellerProducts(String email){
        return firebaseDb.collection("produk")
                .whereEqualTo("email_penjual", email)
                .whereEqualTo("aktif", true)
                .get();
    }

    public Task<QuerySnapshot> getTopSellerProducts(String email){
        return firebaseDb.collection("produk")
				.whereEqualTo("aktif", true)
                .whereEqualTo("email_penjual", email)
                .orderBy("terjual", Query.Direction.DESCENDING)
                .limit(5)
                .get();
    }

    public Task<QuerySnapshot> getProductsFiltered(String sortedField, Query.Direction sortType, ArrayList<Integer> categories){
        Query queryTask = firebaseDb.collection("produk")
                .whereEqualTo("aktif", true);

        if(categories.size() > 0){
            queryTask = queryTask.whereIn("kategori", categories);
        }

        if(!sortedField.equals("")){
            queryTask = queryTask.orderBy(sortedField, sortType);
        }
        queryTask = queryTask.orderBy("tanggal_upload", Query.Direction.DESCENDING);

        return queryTask
                .get();
    }

    public Task<QuerySnapshot> getVariants(String id_produk){
        return firebaseDb.collection("variasi_produk")
                .whereEqualTo("id_produk", id_produk)
                .get();
    }

    public void seeProduct(String id_produk){
        firebaseDb.collection("produk")
                .document(id_produk).update("dilihat", FieldValue.increment(1));
    }

    public void incProductSold(String id_produk, int jumlah){
        firebaseDb.collection("produk")
                .document(id_produk).update("terjual", FieldValue.increment(jumlah));
    }

    public void incProductReview(String id_produk, int jumlah){
        firebaseDb.collection("produk")
                .document(id_produk).update("ulasan", FieldValue.increment(jumlah));
    }

    public void reduceProductQtyAfterBuy(String id_item, int qty){
        firebaseDb.collection("produk")
                .document(id_item)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Product produk = new Product(documentSnapshot);
                    int qtyAfter = produk.getStok() - qty;
                    boolean aktif = produk.isAktif();
                    if(qtyAfter <= 0){
                        qtyAfter = 0;
                        aktif = false;
                    }

                    Map<String, Object> prod = new HashMap<>();
                    prod.put("stok", qtyAfter);
                    prod.put("aktif", aktif);

                    firebaseDb.collection("produk")
                            .document(id_item)
                            .set(prod, SetOptions.merge());
                }
            }
        });
    }

    public void reduceVarQtyAfterBuy(String id_produk, String id_variasi, int qty){
        firebaseDb.collection("variasi_produk")
                .document(id_variasi)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    VarianProduk variasi = new VarianProduk(documentSnapshot);
                    int qtyAfter = variasi.getStok() - qty;
                    if(qtyAfter <= 0){
                        qtyAfter = 0;
                    }

                    Map<String, Object> varian = new HashMap<>();
                    varian.put("stok", qtyAfter);

                    reduceProductQtyAfterBuy(id_produk, qty);

                    firebaseDb.collection("variasi_produk")
                            .document(id_variasi)
                            .set(varian, SetOptions.merge());
                }
            }
        });
    }

    public void addVarQtyAfterCancel(String id_produk, String id_variasi, int qty){
        firebaseDb.collection("variasi_produk")
                .document(id_variasi)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    VarianProduk variasi = new VarianProduk(documentSnapshot);
                    int qtyAfter = variasi.getStok() + qty;

                    Map<String, Object> varian = new HashMap<>();
                    varian.put("stok", qtyAfter);

                    addProductQtyAfterCancel(id_produk, qty);

                    firebaseDb.collection("variasi_produk")
                            .document(id_variasi)
                            .set(varian, SetOptions.merge());
                }
            }
        });
    }

    public void addProductQtyAfterCancel(String id_produk, int qty){
        firebaseDb.collection("produk")
                .document(id_produk)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getData() != null){
                    Product produk = new Product(documentSnapshot);
                    int qtyAfter = produk.getStok() + qty;

                    Map<String, Object> prod = new HashMap<>();
                    prod.put("stok", qtyAfter);
                    prod.put("aktif", true);

                    firebaseDb.collection("produk")
                            .document(id_produk)
                            .set(prod, SetOptions.merge());
                }
            }
        });
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
