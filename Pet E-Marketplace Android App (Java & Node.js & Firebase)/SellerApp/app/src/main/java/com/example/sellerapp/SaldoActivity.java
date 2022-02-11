package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.sellerapp.adapters.RiwayatSaldoAdapter;
import com.example.sellerapp.databinding.ActivitySaldoBinding;
import com.example.sellerapp.viewmodels.SaldoViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class SaldoActivity extends AppCompatActivity {

    private SaldoViewModel viewModel;
    private AutoCompleteTextView autoRiwayat;
    private int jenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new SaldoViewModel(getApplication());

        ActivitySaldoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_saldo);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbSaldo = findViewById(R.id.tbSaldo);
        tbSaldo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RiwayatSaldoAdapter adapter = new RiwayatSaldoAdapter(new RiwayatSaldoAdapter.enlargeBuktiTransfer() {
            @Override
            public void onEnlarge(String url_gambar) {
                Intent productIntent = new Intent(SaldoActivity.this, ImageViewerActivity.class);
                productIntent.putExtra("url", url_gambar);
                startActivity(productIntent);
            }
        });
        RecyclerView rvSaldo = findViewById(R.id.rvSaldoHistory);
        rvSaldo.setAdapter(adapter);

        viewModel.getHistoriesVMs().observe(this, vms -> {
            adapter.setHistoriesVMs(vms);
            adapter.notifyDataSetChanged();
        });

        MaterialButton btnTarik = findViewById(R.id.btnTarikSaldo);
        btnTarik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tarikIntent = new Intent(SaldoActivity.this, TarikSaldoActivity.class);
                startActivity(tarikIntent);
            }
        });

        String[] options = {"Semua", "Pemasukan", "Pengeluaran", "Pencairan Saldo"};
        autoRiwayat = findViewById(R.id.autoSaldoType);
        autoRiwayat.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
        autoRiwayat.setText(options[0], false);
        autoRiwayat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                jenis = i-1;
                viewModel.getSaldoHistories(i-1);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        autoRiwayat.setText("Semua", false);
        viewModel.getSellerSaldo();

    }
}