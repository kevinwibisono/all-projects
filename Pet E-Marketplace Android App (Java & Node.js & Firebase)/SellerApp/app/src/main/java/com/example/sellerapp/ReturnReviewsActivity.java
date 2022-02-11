package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.example.sellerapp.adapters.ReviewsAdapter;
import com.example.sellerapp.databinding.ActivityReturnReviewsBinding;
import com.example.sellerapp.viewmodels.ReturnReviewsViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class ReturnReviewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReturnReviewsViewModel viewModel = new ReturnReviewsViewModel(getApplication());
        ActivityReturnReviewsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_return_reviews);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbReturn = findViewById(R.id.tbReviews);
        tbReturn.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setTitle("Mengirim balasan");
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                loadingDialog.show();
            }
            else{
                loadingDialog.dismiss();
            }
        });

        ReviewsAdapter adapter = new ReviewsAdapter(new ReviewsAdapter.addReviewReturn() {
            @Override
            public void onReturn(String id, String balasan) {
                viewModel.addReviewReturn(id, balasan);
            }
        });

        RecyclerView rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(adapter);

        viewModel.getReviewsVMs().observe(this, vms->{
            adapter.setReviews(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.beginGetReviews();

        TabLayout tabsStatus = findViewById(R.id.tabsStatusUlasan);
        tabsStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    //belum dibalas
                    viewModel.getReviewsRoleBased(false);
                }
                else{
                    //sudah dibalas
                    viewModel.getReviewsRoleBased(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}