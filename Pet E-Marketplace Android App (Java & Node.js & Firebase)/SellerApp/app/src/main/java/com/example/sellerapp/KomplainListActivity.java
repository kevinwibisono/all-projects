package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sellerapp.adapters.KomplainAdapter;
import com.example.sellerapp.databinding.ActivityKomplainListBinding;
import com.example.sellerapp.viewmodels.KomplainListViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class KomplainListActivity extends AppCompatActivity {

    private KomplainListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain_list);

        viewModel = new KomplainListViewModel(getApplication());
        ActivityKomplainListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_komplain_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbKomplain = findViewById(R.id.tbComplainList);
        tbKomplain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SwipeRefreshLayout complainsList = findViewById(R.id.swipeRefreshKomplainList);
        complainsList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getSellerPJsComplains();
                complainsList.setRefreshing(false);
            }
        });

        KomplainAdapter adapter = new KomplainAdapter(new KomplainAdapter.showComplainDetail() {
            @Override
            public void complainDetail(String id_komplain, String id_pj) {
                Intent detailIntent = new Intent(KomplainListActivity.this, KomplainDetailActivity.class);
                detailIntent.putExtra("id_komplain", id_komplain);
                detailIntent.putExtra("id_pj", id_pj);
                startActivity(detailIntent);
            }
        });

        RecyclerView rvKomplain = findViewById(R.id.rvKomplainList);
        rvKomplain.setAdapter(adapter);

        viewModel.getPjComplains().observe(this, compains->{
            adapter.setComplains(compains);
            adapter.notifyDataSetChanged();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getSellerPJsComplains();
    }
}