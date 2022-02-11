package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sellerapp.databinding.ActivityAddGroomingPackBinding;
import com.example.sellerapp.databinding.ActivityAddPromoBinding;
import com.example.sellerapp.viewmodels.AddGroomingPackViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class AddGroomingPackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grooming_pack);

        AddGroomingPackViewModel viewModel = new AddGroomingPackViewModel();
        ActivityAddGroomingPackBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_grooming_pack);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        if(getIntent().hasExtra("id_pack")){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            viewModel.getPackageDetails(getIntent().getStringExtra("id_pack"));
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Mendapatkan Paket Grooming....");
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                dialog.show();
            }
            else{
                dialog.dismiss();
            }
        });

        TextInputLayout[] tls = {findViewById(R.id.tlAddGroomPackName), findViewById(R.id.tlAddGroomPackPrice)};
        viewModel.getErrors().observe(this, errors->{
            for (int i = 0; i < 2; i++) {
                tls[i].setError(errors[i]);
            }
            if(!errors[0].equals("")){
                tls[0].getEditText().requestFocus();
            }
            else{
                tls[1].getEditText().requestFocus();
            }
        });

        MaterialButton btnAdd = findViewById(R.id.btnConfirmAddPack);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewModel.groomingPackageValid()){
                    Intent resultintent = new Intent(AddGroomingPackActivity.this, SellerDetailActivity.class);
                    resultintent.putExtra("nama", viewModel.packName.getValue());
                    resultintent.putExtra("harga", viewModel.packPrice.getValue());
                    if(getIntent().hasExtra("id_pack")){
                        resultintent.putExtra("id_pack", getIntent().getStringExtra("id_pack"));
                    }
                    setResult(99, resultintent);
                    finish();
                }
            }
        });

        MaterialToolbar tbAddGrooming = findViewById(R.id.tbGroomPackAdd);
        tbAddGrooming.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}