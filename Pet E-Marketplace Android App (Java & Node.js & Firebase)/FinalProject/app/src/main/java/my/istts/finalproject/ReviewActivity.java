package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.GiveReviewAdapter;

import my.istts.finalproject.databinding.ActivityReviewBinding;
import my.istts.finalproject.viewmodels.ReviewViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ReviewViewModel viewModel = new ReviewViewModel(getApplication());
        ActivityReviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_review);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getOrderDetail(getIntent().getStringExtra("id_pj"));

        MaterialToolbar tbCart = findViewById(R.id.tbReview);
        tbCart.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GiveReviewAdapter adapter = new GiveReviewAdapter();

        RecyclerView rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setAdapter(adapter);

        viewModel.getReviewItems().observe(this, reviews->{
            adapter.setRevVMs(reviews);
            adapter.notifyDataSetChanged();
        });

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Memberikan Ulasan...");
        loading.setCancelable(false);
        viewModel.isLoading().observe(this, isloading->{
            if(isloading){
                loading.show();
            }
            else{
                loading.dismiss();
            }
        });

        viewModel.isDoneReview().observe(this, done->{
            finish();
        });
    }
}