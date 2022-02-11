package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sellerapp.databinding.ActivityTarikSaldoBinding;
import com.example.sellerapp.viewmodels.TarikSaldoViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

public class TarikSaldoActivity extends AppCompatActivity {

    private TarikSaldoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new TarikSaldoViewModel(getApplication());

        ActivityTarikSaldoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_tarik_saldo);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tb = findViewById(R.id.tbTarikSaldo);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ProgressDialog loadScreen = new ProgressDialog(this);
        loadScreen.setCancelable(false);
        loadScreen.setTitle("Mengajukan Penarikan...");
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                loadScreen.show();
            }
            else{
                loadScreen.dismiss();
            }
        });

        TextView otherRekening = findViewById(R.id.openOtherRekening);
        otherRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent allRekening = new Intent(TarikSaldoActivity.this, RekeningListActivity.class);
                startActivity(allRekening);
            }
        });

        int[] bankIcons = {R.drawable.iconagi, R.drawable.iconbca, R.drawable.iconbni, R.drawable.iconcimb, R.drawable.iconmandiri};
        viewModel.getRekType().observe(this, type -> {
            ImageView ivRek = findViewById(R.id.ivTarikRek);
            ivRek.setImageResource(bankIcons[type]);
        });

        TextInputLayout tlTarikSaldo = findViewById(R.id.tlTarikSaldo);
        viewModel.isValid().observe(this, valid -> {
            if(valid){
                tlTarikSaldo.setError("");
                Toast.makeText(this, "Penarikan Saldo Berhasil Diajukan", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                //munculkan errorText
                tlTarikSaldo.setError("Jumlah Penarikan Melebihi Saldo");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getSaldoRek();
    }
}