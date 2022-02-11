package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ReviewsAdapter;
import my.istts.finalproject.adapters.ReccProductsSliderAdapter;
import my.istts.finalproject.databinding.ActivityPetProductBinding;
import my.istts.finalproject.viewmodels.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class PetProductActivity extends AppCompatActivity {

    private ProductViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ProductViewModel(getApplication());
        ActivityPetProductBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_product);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.setProduk(getIntent().getStringExtra("id_produk"));

        viewModel.getPictures().observe(this, pictures->{
            ImageSlider hotelPicSlider = findViewById(R.id.sliderProductDetail);
            ArrayList<SlideModel> slideModels = new ArrayList<>();
            for (int i = 0; i < pictures.length; i++) {
                if(!pictures[i].equals("")){
                    slideModels.add(new SlideModel(pictures[i], "", ScaleTypes.FIT));
                }
            }
            hotelPicSlider.setImageList(slideModels);
            hotelPicSlider.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemSelected(int i) {
                    Intent imageIntent = new Intent(PetProductActivity.this, ImageViewerActivity.class);
                    imageIntent.putExtra("url", slideModels.get(i).getImageUrl());
                    startActivity(imageIntent);
                }
            });
        });

        ImageView ivOwner = findViewById(R.id.ivProductOwner);
        viewModel.getOwnerPic().observe(this, ownerPic -> {
            Glide.with(this).load(ownerPic).into(ivOwner);
        });

        MaterialToolbar toolbarProductPage = findViewById(R.id.tbProduct);
        toolbarProductPage.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeIntentMenu(item.getItemId());
                return false;
            }
        });
        toolbarProductPage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MenuItem itemCart = toolbarProductPage.getMenu().findItem(R.id.headerProductCart);
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

        TextInputEditText sbProduct = findViewById(R.id.edSearchProduct);
        sbProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(PetProductActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        ReccProductsSliderAdapter sellerproadapter = new ReccProductsSliderAdapter(new ReccProductsSliderAdapter.onProductClickCallback() {
            @Override
            public void onProductClick(String id) {
                Intent productIntent = new Intent(PetProductActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id);
                startActivity(productIntent);
            }
        });
        RecyclerView rvSellerProducts = findViewById(R.id.rvSellerProducts);
        rvSellerProducts.setAdapter(sellerproadapter);

        viewModel.getShopProducts().observe(this, productVMs -> {
            sellerproadapter.setProductVMs(productVMs);
            sellerproadapter.notifyDataSetChanged();
        });


        ReccProductsSliderAdapter reccprodadapter = new ReccProductsSliderAdapter(new ReccProductsSliderAdapter.onProductClickCallback() {
            @Override
            public void onProductClick(String id) {
                Intent productIntent = new Intent(PetProductActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id);
                startActivity(productIntent);
            }
        });
        RecyclerView rvProducts = findViewById(R.id.rvReccProducts);
        rvProducts.setAdapter(reccprodadapter);

        viewModel.getReccProducts().observe(this, productVMs -> {
            reccprodadapter.setProductVMs(productVMs);
            reccprodadapter.notifyDataSetChanged();
        });

        ReviewsAdapter revsAdapter = new ReviewsAdapter();
        RecyclerView rvReviews = findViewById(R.id.rvProductReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvReviews.setAdapter(revsAdapter);

        viewModel.getReviews().observe(this, revVMs ->{
            revsAdapter.setRevVMs(revVMs);
            revsAdapter.notifyDataSetChanged();
        });

        //pindah ke halaman seller jika diklik
        LinearLayout sellerLine = findViewById(R.id.sellerLine);
        viewModel.getEmailShop().observe(this, hpShop->{
            sellerLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sellerIntent = new Intent(PetProductActivity.this, SellerProfileActivity.class);
                    sellerIntent.putExtra("hp_seller", hpShop);
                    startActivity(sellerIntent);
                }
            });
        });


        //Tambahkan variant dalam bentuk Chip
        ChipGroup variantsChipGroup = findViewById(R.id.chipsProductVariants);

        viewModel.getVariants().observe(this, variants -> {
            for (int i = 0; i < variants.size(); i++){
                final int index = i;
                Chip newVariantChip = new Chip(this);
                newVariantChip.setCheckable(true);
                newVariantChip.setText(variants.get(i).getNama()+" (Sisa "+variants.get(i).getStok()+")");
                newVariantChip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
                newVariantChip.setChipStrokeWidth(3);
                if(variants.get(i).getStok() <= 0){
                    newVariantChip.setEnabled(false);
                    newVariantChip.setText(variants.get(i).getNama()+" (Habis)");
                }

                variantsChipGroup.addView(newVariantChip);
                newVariantChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            viewModel.chooseVariant(index);
                        }
                    }
                });
            }
        });

        TabLayout tabLayoutReview = findViewById(R.id.tabsProductDetail);
        tabLayoutReview.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.getProductReviews(getIntent().getStringExtra("id_produk"), tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        TextView tvDiscuss = findViewById(R.id.petProductSeeDiscuss);
        tvDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(PetProductActivity.this, FullDiscussionActivity.class);
                discussIntent.putExtra("id_produk", getIntent().getStringExtra("id_produk"));
                startActivity(discussIntent);
            }
        });

        TextView tvReview = findViewById(R.id.petProductSeeReviews);
        tvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(PetProductActivity.this, ReviewListActivity.class);
                discussIntent.putExtra("id_item", getIntent().getStringExtra("id_produk"));
                discussIntent.putExtra("tipe", 0);
                startActivity(discussIntent);
            }
        });

        MaterialButton btnChat = findViewById(R.id.btnProductChat);
        MaterialButton btnEmptyChat = findViewById(R.id.btnProductEmptyChat);
        viewModel.getEmailShop().observe(this, hpShop->{
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toChat(hpShop);
                }
            });
            btnEmptyChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toChat(hpShop);
                }
            });
        });

        ProgressDialog loadingScreen = new ProgressDialog(this);
        loadingScreen.setTitle("Menambahkan Keranjang....");
        loadingScreen.setCancelable(false);
        viewModel.isCartLoading().observe(this, loading ->{
            if(loading){
                loadingScreen.show();
            }
            else{
                loadingScreen.dismiss();
            }
        });

        BottomAppBar btmPartProduct = findViewById(R.id.btmBarPetProduct);
        Snackbar snackbar = Snackbar.make(btmPartProduct, "", Snackbar.LENGTH_SHORT);

        viewModel.getToastMsg().observe(this, snackbar::setText);

        viewModel.getError().observe(this, error -> {
            if(error){
                snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_light))
                        .setAction("", null);
            }
            else{
                snackbar.setBackgroundTint(getResources().getColor(android.R.color.black));
                snackbar.setAction("Lihat", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PetProductActivity.this, CartActivity.class));
                    }
                }).setActionTextColor(Color.WHITE);
            }
            snackbar.setAnchorView(btmPartProduct);
            snackbar.show();
        });


    }


    private void changeIntentMenu(int id){
        if(id == R.id.headerProductAkun){
            Intent mainIntent = new Intent(PetProductActivity.this, MainActivity.class);
            mainIntent.putExtra("jumpTo", 3);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductChat){
            Intent mainIntent = new Intent(PetProductActivity.this, ConversationsActivity.class);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductFav){
            Intent favIntent = new Intent(PetProductActivity.this, FavoriteActivity.class);
            favIntent.putExtra("tipe", 0);
            startActivity(favIntent);
        }
        else if(id == R.id.headerProductHome){
            Intent homeIntent = new Intent(PetProductActivity.this, MainActivity.class);
            startActivity(homeIntent);
        }
        else if(id == R.id.headerProductCart){
            startActivity(new Intent(PetProductActivity.this, CartActivity.class));
        }
    }

    private void toChat(String hpShop){
        Intent chatIntent = new Intent(PetProductActivity.this, ChatActivity.class);
        chatIntent.putExtra("lawan_bicara", hpShop);
        chatIntent.putExtra("id_item", getIntent().getStringExtra("id_produk"));
        chatIntent.putExtra("tipe", 0);
        startActivity(chatIntent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}