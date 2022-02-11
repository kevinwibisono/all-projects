package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.HotelListAdapter;
import my.istts.finalproject.databinding.ActivityHotelBinding;
import my.istts.finalproject.viewmodels.HotelListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;


public class HotelActivity extends AppCompatActivity {

    private HotelListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new HotelListViewModel(getApplication());
        ActivityHotelBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_hotel);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        if(getIntent().hasExtra("fasilitas")){
            viewModel.setFac(getIntent().getIntExtra("fasilitas", 0));
        }
        else if(getIntent().hasExtra("search")){
            viewModel.setSearchKeyword(getIntent().getStringExtra("search"));
        }
        else{
            viewModel.initFilters();
            viewModel.getHotelsFiltered();
        }

        HotelListAdapter adapter = new HotelListAdapter(new HotelListAdapter.hotelClickCallback() {
            @Override
            public void onHotelClick(String id_hotel) {
                Intent hotelIntent = new Intent(HotelActivity.this, HotelDetailActivity.class);
                hotelIntent.putExtra("id_kamar", id_hotel);
                startActivity(hotelIntent);
            }
        });

        RecyclerView rvHotels = findViewById(R.id.rvHotels);
        rvHotels.setLayoutManager(new LinearLayoutManager(this));
        rvHotels.setAdapter(adapter);

        viewModel.getHotelRooms().observe(this, hotelRooms->{
            adapter.setHotelVMs(hotelRooms);
            adapter.notifyDataSetChanged();
        });

        TextInputEditText sbHotel = findViewById(R.id.edSearchHotelList);
        sbHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(HotelActivity.this, SearchActivity.class);
                searchIntent.putExtra("tipe", 1);
                startActivity(searchIntent);
            }
        });

        MaterialToolbar tbHotel = findViewById(R.id.tbHotels);
        tbHotel.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tbHotel.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeMenuIntent(item.getItemId());
                return false;
            }
        });

        MenuItem itemCart = tbHotel.getMenu().findItem(R.id.headerProductCart);
        View actionView = itemCart.getActionView();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenuIntent(R.id.headerProductCart);
            }
        });
        TextView textCartItemCount = (TextView) actionView.findViewById(R.id.icon_cart_badge);

        viewModel.isItemInCart().observe(this, itemsInCart->{
            textCartItemCount.setVisibility(View.VISIBLE);
        });

        Chip chipSort = findViewById(R.id.chipHotelSort);
        chipSort.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeSort();
            }
        });

        ChipGroup cgKategori = findViewById(R.id.chipGroupFilterFacs);
        viewModel.getActiveFacs().observe(this, categories -> {
            cgKategori.removeAllViews();
            for (int i = 0; i < categories.size(); i++) {
                final int index = i;
                Chip newVariantChip = new Chip(this);

                newVariantChip.requestLayout();
                newVariantChip.setText(categories.get(i));
                newVariantChip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
                newVariantChip.setChipStrokeWidth(3);
                newVariantChip.setCloseIconVisible(true);
                newVariantChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewModel.removeFac(index);
                    }
                });


                cgKategori.addView(newVariantChip);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) newVariantChip.getLayoutParams();
                layoutParams.setMargins(0, 0, 5, 0);
            }
        });


        LinearLayout filter = findViewById(R.id.filterSortHotel);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HotelBottomDialogFragment fragment = new HotelBottomDialogFragment();
                fragment.setViewModel(viewModel);
                fragment.show(getSupportFragmentManager(), "");
            }
        });
    }

    private void changeMenuIntent(int id){
        if(id == R.id.headerProductAkun){
            Intent mainIntent = new Intent(HotelActivity.this, MainActivity.class);
            mainIntent.putExtra("jumpTo", 3);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductChat){
            Intent mainIntent = new Intent(HotelActivity.this, ConversationsActivity.class);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductFav){
            Intent favIntent = new Intent(HotelActivity.this, FavoriteActivity.class);
            favIntent.putExtra("tipe", 1);
            startActivity(favIntent);
        }
        else if(id == R.id.headerProductHome){
            Intent homeIntent = new Intent(HotelActivity.this, MainActivity.class);
            startActivity(homeIntent);
        }
        else if(id == R.id.headerProductCart){
            Intent cartIntent = new Intent(HotelActivity.this, CartActivity.class);
            cartIntent.putExtra("tipe", 2);
            startActivity(cartIntent);
        }
    }
}