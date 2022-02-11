package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sellerapp.adapters.ReviewListAdapter;
import com.example.sellerapp.databinding.ActivityReviewListBinding;
import com.example.sellerapp.viewmodels.ReviewListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class ReviewListActivity extends AppCompatActivity {

    private int skorIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReviewListViewModel viewModel = new ReviewListViewModel(getApplication());
        ActivityReviewListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_review_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbReviewList = findViewById(R.id.tbItemReviews);
        tbReviewList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String id = getIntent().getStringExtra("id_item");
        int tipe = getIntent().getIntExtra("tipe", 0);
        skorIdx = 0;

        ReviewListAdapter adapter = new ReviewListAdapter();

        RecyclerView rvReviewList = findViewById(R.id.rvReviewList);
        rvReviewList.setAdapter(adapter);

        viewModel.getReviewsVMs().observe(this, reviews->{
            adapter.setRevVMs(reviews);
            adapter.notifyDataSetChanged();
        });

        viewModel.setItem(id, tipe);

        TabLayout tabLayoutReview = findViewById(R.id.tabsReviewList);
        tabLayoutReview.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                skorIdx = tab.getPosition();
                viewModel.getItemReviews(skorIdx);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        ImageView ivTarget = findViewById(R.id.ivUlasanTarget);
        viewModel.getTargetPicture().observe(this, picture->{
            Glide.with(this).load(picture).into(ivTarget);
        });

    }
}