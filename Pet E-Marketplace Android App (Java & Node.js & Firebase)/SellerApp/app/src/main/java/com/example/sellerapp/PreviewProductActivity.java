package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.sellerapp.adapters.ReviewListAdapter;
import com.example.sellerapp.databinding.ActivityPreviewProductBinding;
import com.example.sellerapp.viewmodels.PreviewProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class PreviewProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreviewProductViewModel viewModel = new PreviewProductViewModel(getApplication());
        ActivityPreviewProductBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_preview_product);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        String receivedProduct = getIntent().getStringExtra("id_produk");
        viewModel.setProduk(receivedProduct);

        MaterialToolbar tbPreview = findViewById(R.id.tbProduct);
        tbPreview.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewModel.getGambarPenjual().observe(this, gambarUrl -> {
            ImageView ivSellerPic = findViewById(R.id.ivProductSellerPic);
            Glide.with(this).load(gambarUrl).into(ivSellerPic);
        });

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

        MaterialButton btnEdit = findViewById(R.id.btnUpdateProduct);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProduct = new Intent(PreviewProductActivity.this, ProductActivity.class);
                addProduct.putExtra("id_produk", viewModel.getIdProduk());
                startActivity(addProduct);
            }
        });

        MaterialButton btnUlasan = findViewById(R.id.btnSeeUlasan);
        btnUlasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviews = new Intent(PreviewProductActivity.this, ReturnReviewsActivity.class);
                reviews.putExtra("id_produk", viewModel.getIdProduk());
                startActivity(reviews);
            }
        });

        viewModel.getPictures().observe(this, pictures -> {
            ImageSlider articleSlider = findViewById(R.id.sliderProductPreview);
            ArrayList<SlideModel> slideModels = new ArrayList<>();
            for (int i=0;i<5;i++){
                if(!pictures[i].equals("")){
                    slideModels.add(new SlideModel(pictures[i], "", ScaleTypes.FIT));
                }
            }
            articleSlider.setImageList(slideModels);
        });

        TabLayout tabLayoutReview = findViewById(R.id.tabsProductReviewStar);
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

        ReviewListAdapter adapter = new ReviewListAdapter();

        RecyclerView rvReviews = findViewById(R.id.rvProductReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvReviews.setAdapter(adapter);
        viewModel.getRevVMs().observe(this, vms -> {
            adapter.setRevVMs(vms);
            adapter.notifyDataSetChanged();
        });

        TextView seeDiscuss = findViewById(R.id.petProductSeeDiscuss);
        seeDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(PreviewProductActivity.this, FullDiscussionActivity.class);
                discussIntent.putExtra("id_produk", getIntent().getStringExtra("id_produk"));
                startActivity(discussIntent);
            }
        });

        TextView seeFullReviews = findViewById(R.id.petProductSeeReviews);
        seeFullReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(PreviewProductActivity.this, ReviewListActivity.class);
                discussIntent.putExtra("id_item", getIntent().getStringExtra("id_produk"));
                discussIntent.putExtra("tipe", 0);
                startActivity(discussIntent);
            }
        });

        ImageView[] stars = {findViewById(R.id.starProductDetail1), findViewById(R.id.starProductDetail2), findViewById(R.id.starProductDetail3), findViewById(R.id.starProductDetail4), findViewById(R.id.starProductDetail5)};
        viewModel.getNilai().observe(this, score -> {
            float numberScore = Float.parseFloat(score);
            for (int i = 0; i < 5; i++) {
                //karena index dimulai 0, sedangkan skor dimulai dari 1, maka saat membanginkan tambah index(i) dgn 1
                if((i+1) <= numberScore){
                    stars[i].setImageResource(R.drawable.ic_baseline_star_20);
                }
                else{
                    float diff = numberScore - (i);
                    stars[i].setImageResource(determineStar(diff));
                }
            }
        });

    }

    private int determineStar(float diff){
        if(diff < 0.2){
            return R.drawable.ic_baseline_star_border_20;
        }
        else if(diff > 0.1 && diff < 0.9){
            return R.drawable.ic_baseline_star_half_20;
        }
        else{
            return R.drawable.ic_baseline_star_20;
        }
    }
}