package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.sellerapp.adapters.VoucherProductChooseAdapter;
import com.example.sellerapp.databinding.ActivityChoosePromoProductBinding;
import com.example.sellerapp.viewmodels.PromoChooseProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ChoosePromoProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_promo_product);

        PromoChooseProductViewModel viewModel = new PromoChooseProductViewModel(getApplication());
        ActivityChoosePromoProductBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_promo_product);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbChoosePromo = findViewById(R.id.tbChoosePromo);
        tbChoosePromo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        VoucherProductChooseAdapter adapter = new VoucherProductChooseAdapter();

        RecyclerView rvProducts = findViewById(R.id.rvProductChoices);
        rvProducts.setAdapter(adapter);

        viewModel.getProductVMs().observe(this, vms ->{
            adapter.setProductVMs(vms);
        });
        viewModel.getSellerProducts();

        MaterialButton btnChoose = findViewById(R.id.btnVoucherProductChoose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultintent = new Intent();
                resultintent.putExtra("chosen_products", viewModel.getProductIncludes());
                setResult(13, resultintent);
                finish();
            }
        });

        CheckBox checkAll = findViewById(R.id.cbChooseProductAll);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                viewModel.addRemoveAll(b);

            }
        });
    }
}