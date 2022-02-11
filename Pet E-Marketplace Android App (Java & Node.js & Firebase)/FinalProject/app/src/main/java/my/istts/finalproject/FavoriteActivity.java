package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.AdoptionAdapter;
import my.istts.finalproject.adapters.HotelListAdapter;
import my.istts.finalproject.adapters.ReccProductsAdapter;
import my.istts.finalproject.databinding.ActivityFavoriteBinding;
import my.istts.finalproject.viewmodels.FavoriteViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class FavoriteActivity extends AppCompatActivity {

    private int tipe = 0;
    private RecyclerView rvFav;
    private ReccProductsAdapter productsAdapter;
    private HotelListAdapter hotelAdapter;
    private AdoptionAdapter adoptionAdapter;
    private FavoriteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new FavoriteViewModel(getApplication());
        ActivityFavoriteBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_favorite);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        tipe = getIntent().getIntExtra("tipe", 0);
        rvFav = findViewById(R.id.rvFav);

        MaterialToolbar tbFav = findViewById(R.id.tbFav);
        tbFav.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        productsAdapter = new ReccProductsAdapter(new ReccProductsAdapter.onProductClickCallback() {
            @Override
            public void onProductClick(String id) {
                Intent productIntent = new Intent(FavoriteActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id);
                startActivity(productIntent);
            }
        });
        productsAdapter.setCallbackFavorite(new ReccProductsAdapter.onProductFavoritedChanged() {
            @Override
            public void onFavoriteChanged() {
                viewModel.getFavorites(tipe);
            }
        });

        viewModel.getFavProducts().observe(this, products->{
            productsAdapter.setProductVMs(products);
            productsAdapter.notifyDataSetChanged();
        });

        hotelAdapter = new HotelListAdapter(new HotelListAdapter.hotelClickCallback() {
            @Override
            public void onHotelClick(String id_hotel) {
                Intent hotelIntent = new Intent(FavoriteActivity.this, HotelDetailActivity.class);
                hotelIntent.putExtra("id_kamar", id_hotel);
                startActivity(hotelIntent);
            }
        });
        hotelAdapter.setFavoriteCallback(new HotelListAdapter.hotelFavoriteChanged() {
            @Override
            public void onFavoriteChanged() {
                viewModel.getFavorites(tipe);
            }
        });

        viewModel.getFavHotels().observe(this, hotels->{
            hotelAdapter.setHotelVMs(hotels);
            hotelAdapter.notifyDataSetChanged();
        });

        adoptionAdapter = new AdoptionAdapter(new AdoptionAdapter.petClickCallback() {
            @Override
            public void onPetClick(String id_pet) {
                Intent detailIntent = new Intent(FavoriteActivity.this, PetAdoptDetailActivity.class);
                detailIntent.putExtra("id_pet", id_pet);
                startActivity(detailIntent);
            }
        });
        adoptionAdapter.setFavoriteCallback(new AdoptionAdapter.petFavoritedChanged() {
            @Override
            public void onFavoritedChanged() {
                viewModel.getFavorites(tipe);
            }
        });

        viewModel.getFavPetAdopt().observe(this, adopts->{
            adoptionAdapter.setAdoptsVMs(adopts);
            adoptionAdapter.notifyDataSetChanged();
        });

        changeRVType(tipe);

        TabLayout tabsFav = findViewById(R.id.tabsFav);
        TabLayout.Tab tab = tabsFav.getTabAt(tipe);
        tabsFav.selectTab(tab);

        tabsFav.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tipe = tab.getPosition();
                viewModel.getFavorites(tab.getPosition());
                changeRVType(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeRVType(int tipe){
        rvFav.setAdapter(null);
        if(tipe == 0){
            rvFav.setAdapter(productsAdapter);
            rvFav.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else if(tipe == 1){
            rvFav.setAdapter(hotelAdapter);
            rvFav.setLayoutManager(new LinearLayoutManager(this));
        }
        else{
            rvFav.setAdapter(adoptionAdapter);
            rvFav.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getFavorites(tipe);
        changeRVType(tipe);
    }
}