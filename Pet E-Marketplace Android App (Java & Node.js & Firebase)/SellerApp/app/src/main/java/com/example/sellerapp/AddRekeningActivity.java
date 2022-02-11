package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.sellerapp.databinding.ActivityAddRekeningBinding;
import com.example.sellerapp.databinding.ActivitySaldoBinding;
import com.example.sellerapp.viewmodels.AddRekeningViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

public class AddRekeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AddRekeningViewModel viewModel = new AddRekeningViewModel(getApplication());

        ActivityAddRekeningBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rekening);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAddRek = findViewById(R.id.tbAddRekening);
        tbAddRek.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String[] opts = {"Artha Graha Internasional", "Bank Central Asia", "Bank Negara Indonesia", "CIMB Niaga", "Mandiri"};
        AutoCompleteTextView autoRekening = findViewById(R.id.autoCompleteRekening);
        autoRekening.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opts));
        autoRekening.setText(opts[0], false);

        autoRekening.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setJenis(i);
            }
        });

        TextInputLayout[] tlS = {findViewById(R.id.tlAddNoRek), findViewById(R.id.tlAddNamaRek)};
        viewModel.getErrors().observe(this, errors->{
            for (int i = 0; i < errors.length; i++) {
                tlS[i].setError(errors[i]);
            }
        });

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Menambahkan Rekening...");
        loading.setCancelable(false);
        viewModel.isLoading().observe(this, isloading->{
            if(isloading){
                loading.show();
            }
            else{
                loading.dismiss();
            }
        });

        viewModel.isDoneAdd().observe(this, done->{
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        finish();;
    }
}