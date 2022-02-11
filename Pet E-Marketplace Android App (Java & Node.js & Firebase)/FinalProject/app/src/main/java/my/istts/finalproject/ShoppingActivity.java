package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
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

import my.istts.finalproject.adapters.ReccProductsAdapter;

import my.istts.finalproject.databinding.ActivityShoppingBinding;
import my.istts.finalproject.viewmodels.ProductListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

public class ShoppingActivity extends AppCompatActivity {

    private ProductListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ProductListViewModel(getApplication());
        ActivityShoppingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_shopping);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        if(getIntent().hasExtra("kategori")){
            viewModel.setCategory(getIntent().getIntExtra("kategori", 0));
        }
        else if(getIntent().hasExtra("search")){
            viewModel.setSearchKeyword(getIntent().getStringExtra("search"));
        }
        else{
            viewModel.initFilters();
            viewModel.getProductsFiltered();
        }

        ReccProductsAdapter reccprodadapter = new ReccProductsAdapter(new ReccProductsAdapter.onProductClickCallback() {
            @Override
            public void onProductClick(String id) {
                Intent productIntent = new Intent(ShoppingActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id);
                startActivity(productIntent);
            }
        });
        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(reccprodadapter);

        viewModel.getProducts().observe(this, vms->{
            reccprodadapter.setProductVMs(vms);
            reccprodadapter.notifyDataSetChanged();
        });

        TextInputEditText sbShop = findViewById(R.id.edSearchProductList);
        sbShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(ShoppingActivity.this, SearchActivity.class);
                searchIntent.putExtra("tipe", 0);
                startActivity(searchIntent);
            }
        });

        MaterialToolbar tbShop = findViewById(R.id.tbShopping);
        tbShop.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeIntentMenu(item.getItemId());
                return false;
            }
        });
        tbShop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MenuItem itemCart = tbShop.getMenu().findItem(R.id.headerProductCart);
        View actionView = itemCart.getActionView();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIntentMenu(R.id.headerProductCart);
            }
        });
        TextView textCartItemCount = (TextView) actionView.findViewById(R.id.icon_cart_badge);

        viewModel.isItemsInCart().observe(this, itemsInCart->{
            textCartItemCount.setVisibility(View.VISIBLE);
        });

        Chip chipSort = findViewById(R.id.chipProductSort);
        chipSort.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeSort();
            }
        });

        ChipGroup cgKategori = findViewById(R.id.chipGroupFilterKats);
        viewModel.getActiveCategories().observe(this, categories -> {
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
                        viewModel.removeCategories(index);
                    }
                });


                cgKategori.addView(newVariantChip);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) newVariantChip.getLayoutParams();
                layoutParams.setMargins(0, 0, 5, 0);
            }
        });

        LinearLayout filter = findViewById(R.id.filterSortProduct);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductBottomDialogFragment fragment = new ProductBottomDialogFragment();
                fragment.setViewModel(viewModel);
                fragment.show(getSupportFragmentManager(), "");
            }
        });

    }

    private void changeIntentMenu(int id){
        if(id == R.id.headerProductAkun){
            Intent mainIntent = new Intent(ShoppingActivity.this, MainActivity.class);
            mainIntent.putExtra("jumpTo", 3);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductChat){
            Intent mainIntent = new Intent(ShoppingActivity.this, ConversationsActivity.class);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductFav){
            Intent favIntent = new Intent(ShoppingActivity.this, FavoriteActivity.class);
            favIntent.putExtra("tipe", 0);
            startActivity(favIntent);
        }
        else if(id == R.id.headerProductHome){
            Intent homeIntent = new Intent(ShoppingActivity.this, MainActivity.class);
            startActivity(homeIntent);
        }
        else if(id == R.id.headerProductCart){
            startActivity(new Intent(ShoppingActivity.this, CartActivity.class));
        }
    }
}