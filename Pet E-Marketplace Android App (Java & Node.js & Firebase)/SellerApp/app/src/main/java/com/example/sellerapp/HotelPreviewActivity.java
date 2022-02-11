package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.sellerapp.adapters.ReviewListAdapter;
import com.example.sellerapp.databinding.ActivityHotelPreviewBinding;
import com.example.sellerapp.viewmodels.HotelPreviewViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HotelPreviewActivity extends AppCompatActivity {

    private HotelPreviewViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new HotelPreviewViewModel(getApplication());
        ActivityHotelPreviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_hotel_preview);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbHotelPreview = findViewById(R.id.tbHotelPreview);
        tbHotelPreview.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewModel.setKamar(getIntent().getStringExtra("id_kamar"));

        ReviewListAdapter adapter = new ReviewListAdapter();

        RecyclerView rvReviews = findViewById(R.id.rvHotelReviews);
        rvReviews.setAdapter(adapter);

        viewModel.getRevVMs().observe(this, revs->{
            adapter.setRevVMs(revs);
            adapter.notifyDataSetChanged();
        });

        viewModel.getGambarPenjual().observe(this, gambar->{
            ImageView ivPemilik = findViewById(R.id.ivHotelOwner);
            Glide.with(this).load(gambar).into(ivPemilik);
        });

        TextView seeDiscuss = findViewById(R.id.petHotelSeeDiscuss);
        seeDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(HotelPreviewActivity.this, FullDiscussionActivity.class);
                discussIntent.putExtra("id_kamar", getIntent().getStringExtra("id_kamar"));
                startActivity(discussIntent);
            }
        });

        TextView seeFullReviews = findViewById(R.id.petHotelSeeReviews);
        seeFullReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(HotelPreviewActivity.this, ReviewListActivity.class);
                discussIntent.putExtra("id_item", getIntent().getStringExtra("id_kamar"));
                discussIntent.putExtra("tipe", 0);
                startActivity(discussIntent);
            }
        });

        MaterialButton btnUlasan = findViewById(R.id.btnHotelReviews);
        btnUlasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(HotelPreviewActivity.this, ReturnReviewsActivity.class);
                startActivity(reviewIntent);
            }
        });

        MaterialButton btnUpdate = findViewById(R.id.btnUpdateHotel);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateIntent = new Intent(HotelPreviewActivity.this, HotelActivity.class);
                updateIntent.putExtra("id_kamar", viewModel.getIdKamar());
                startActivity(updateIntent);
            }
        });

        viewModel.getPictures().observe(this, pictures -> {
            ImageSlider articleSlider = findViewById(R.id.sliderHotelDetail);
            ArrayList<SlideModel> slideModels = new ArrayList<>();
            for (int i=0;i<5;i++){
                if(!pictures[i].equals("")){
                    slideModels.add(new SlideModel(pictures[i], "", ScaleTypes.FIT));
                }
            }
            articleSlider.setImageList(slideModels);
        });

        ImageView[] stars = {findViewById(R.id.hotelStar1), findViewById(R.id.hotelStar2), findViewById(R.id.hotelStar3), findViewById(R.id.hotelStar4), findViewById(R.id.hotelStar5)};
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

        TabLayout tabLayoutReview = findViewById(R.id.tabsHotelReviewStar);
        tabLayoutReview.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.getHotelReviews(getIntent().getStringExtra("id_kamar"), tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
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