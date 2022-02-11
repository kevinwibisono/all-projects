package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.VoucherProductAdapter;
import my.istts.finalproject.databinding.ActivityVoucherDetailBinding;

import my.istts.finalproject.viewmodels.VoucherDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class VoucherDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_detail);

        VoucherDetailViewModel viewModel = new VoucherDetailViewModel();
        ActivityVoucherDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbDetail = findViewById(R.id.tbVoucherDetail);
        tbDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewModel.setPromo(getIntent().getStringExtra("id_promo"));

        VoucherProductAdapter adapter = new VoucherProductAdapter(new VoucherProductAdapter.toProductPage() {
            @Override
            public void toPage(String id_produk) {
                Intent productIntent = new Intent(VoucherDetailActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id_produk);
                startActivity(productIntent);
            }
        });

        RecyclerView rvProducts = findViewById(R.id.rvVoucherProduct);
        rvProducts.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvProducts.setAdapter(adapter);

        viewModel.getPromoProducts().observe(this, products->{
            adapter.setProducts(products);
            adapter.notifyDataSetChanged();
        });

        ImageView ivSeller = findViewById(R.id.ivVoucherDetailSeller);
        viewModel.getPromoPic().observe(this, pic->{
            Glide.with(this).load(pic).into(ivSeller);
        });
    }
}