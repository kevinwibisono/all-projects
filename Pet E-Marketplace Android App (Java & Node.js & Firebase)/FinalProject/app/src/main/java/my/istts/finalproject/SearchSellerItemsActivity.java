package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.SearchProdRoomAdapter;
import my.istts.finalproject.databinding.ActivitySearchSellerProdRoomBinding;
import my.istts.finalproject.viewmodels.SearchProdRoomViewModel;

public class SearchSellerItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SearchProdRoomViewModel viewModel = new SearchProdRoomViewModel();
        ActivitySearchSellerProdRoomBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_search_seller_prod_room);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        //0-> produk    1->hotel
        int tipe = getIntent().getIntExtra("tipe", 0);
        String hpSeller = getIntent().getStringExtra("hp_seller");

        SearchProdRoomAdapter adapter = new SearchProdRoomAdapter(tipe, new SearchProdRoomAdapter.onSearchItemCallback() {
            @Override
            public void onSearchItemClick(String id) {
                if(tipe == 0){
                    Intent productIntent = new Intent(SearchSellerItemsActivity.this, PetProductActivity.class);
                    productIntent.putExtra("id_produk", id);
                    startActivity(productIntent);
                }
                else{
                    Intent hotelIntent = new Intent(SearchSellerItemsActivity.this, HotelDetailActivity.class);
                    hotelIntent.putExtra("id_kamar", id);
                    startActivity(hotelIntent);
                }
            }
        });

        RecyclerView rvSearch = findViewById(R.id.rvSearchProdRoom);
        rvSearch.setAdapter(adapter);

        viewModel.getHotels().observe(this, hotels->{
            adapter.setHotelVMs(hotels);
            adapter.notifyDataSetChanged();
        });

        viewModel.getProducts().observe(this, products->{
            adapter.setProductVMs(products);
            adapter.notifyDataSetChanged();
        });

        viewModel.search(tipe, hpSeller);

        EditText edSearch = findViewById(R.id.edSearchSellerItems);
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == 6){
                    //enter
                    viewModel.search(tipe, hpSeller);
                }
                return false;
            }
        });
    }
}