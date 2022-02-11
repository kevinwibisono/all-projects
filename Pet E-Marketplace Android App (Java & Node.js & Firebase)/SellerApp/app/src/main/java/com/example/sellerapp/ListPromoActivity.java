package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sellerapp.adapters.VoucherAdapter;
import com.example.sellerapp.databinding.ActivityListPromoBinding;
import com.example.sellerapp.models.Promo;
import com.example.sellerapp.viewmodels.PromoViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListPromoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PromoViewModel viewModel = new PromoViewModel(getApplication());
        ActivityListPromoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_list_promo);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbListPromo = findViewById(R.id.tbListPromo);
        tbListPromo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        VoucherAdapter adapter = new VoucherAdapter(new VoucherAdapter.detailVoucher() {
            @Override
            public void showVoucher(String id) {
                Intent detailIntent = new Intent(ListPromoActivity.this, AddPromoActivity.class);
                detailIntent.putExtra("id_promo", id);
                startActivity(detailIntent);
            }
        });

        RecyclerView rvVouchers = findViewById(R.id.rvVouchers);
        rvVouchers.setAdapter(adapter);

        viewModel.getPromoVMs().observe(this, vms ->{
            adapter.setVoucherVMs(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getShopPromos();

        FloatingActionButton btnAdd = findViewById(R.id.btnAddVoucher);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(ListPromoActivity.this, AddPromoActivity.class);
                startActivity(addIntent);
            }
        });
    }
}