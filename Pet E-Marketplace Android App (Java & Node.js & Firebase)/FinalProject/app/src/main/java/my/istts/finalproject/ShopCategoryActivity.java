package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.KategoriProductsAdapter;
import my.istts.finalproject.inputclasses.KategoriProduct;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ShopCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_category);

        MaterialToolbar tbKategori = findViewById(R.id.tbKategori);
        tbKategori.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<KategoriProduct> listKategori = new ArrayList<KategoriProduct>();
        listKategori.add(new KategoriProduct(R.mipmap.kategoridogfood, "Makanan Anjing"));
        listKategori.add(new KategoriProduct(R.mipmap.kategoricatfood, "Makanan Kucing"));
        listKategori.add(new KategoriProduct(R.mipmap.kategorirabbitfood, "Makanan Kelinci"));
        listKategori.add(new KategoriProduct(R.mipmap.kategoribirdfood, "Makanan Burung"));
        listKategori.add(new KategoriProduct(R.mipmap.kategorifishfood, "Makanan Ikan"));
        listKategori.add(new KategoriProduct(R.mipmap.kategorihamsterfood, "Makanan Hamster"));
        listKategori.add(new KategoriProduct(R.mipmap.kategorireptilefood, "Makanan Reptil"));
        listKategori.add(new KategoriProduct(R.mipmap.kategorifarmfood, "Pakan Ternak"));
        listKategori.add(new KategoriProduct(R.mipmap.kategorigroomingutilities, "Peralatan Grooming"));
        listKategori.add(new KategoriProduct(R.mipmap.kategorileashandhandlers, "Leash dan Handler"));
        listKategori.add(new KategoriProduct(R.mipmap.kategoritreats, "Treats (Kudapan)"));
        listKategori.add(new KategoriProduct(R.mipmap.kategorimedical, "Peralatan Kesehatan"));
        listKategori.add(new KategoriProduct(R.mipmap.kategoritoys, "Mainan dan Alat Ketangkasan"));
        listKategori.add(new KategoriProduct(R.mipmap.kategoricleaning, "Peralatan Kebersihan"));
        listKategori.add(new KategoriProduct(R.mipmap.kategoricage, "Kandang/Tempat Tidur"));

        KategoriProductsAdapter adapter = new KategoriProductsAdapter(listKategori, new KategoriProductsAdapter.onCategoryClick() {
            @Override
            public void onClick(int pos) {
                Intent searchIntent = new Intent(ShopCategoryActivity.this, ShoppingActivity.class);
                searchIntent.putExtra("kategori", pos);
                startActivity(searchIntent);
            }
        });

        RecyclerView rvKategori = findViewById(R.id.rvKategoriShop);
        rvKategori.setLayoutManager(new GridLayoutManager(this, 3));
        rvKategori.setAdapter(adapter);
    }
}