package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ReviewsAdapter;

import my.istts.finalproject.databinding.ActivityReviewListBinding;
import my.istts.finalproject.viewmodels.ReviewListViewModel;
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

        ReviewsAdapter adapter = new ReviewsAdapter();

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