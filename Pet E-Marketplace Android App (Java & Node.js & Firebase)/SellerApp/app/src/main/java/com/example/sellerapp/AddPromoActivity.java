package com.example.sellerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sellerapp.adapters.VoucherProductAdapter;
import com.example.sellerapp.databinding.ActivityAddPromoBinding;
import com.example.sellerapp.viewmodels.AddPromoViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddPromoActivity extends AppCompatActivity {

    private AddPromoViewModel viewModel;
    private int REQUEST_CHOOSE_PRODUCTS = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AddPromoViewModel(getApplication());
        ActivityAddPromoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_promo);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        if(getIntent().hasExtra("id_promo")){
            viewModel.updateVoucher(getIntent().getStringExtra("id_promo"));
        }

        TextInputEditText edVoucherEnd = findViewById(R.id.edVoucherEnd);
        edVoucherEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar myCalendar = Calendar.getInstance();
                new DatePickerDialog(
                        AddPromoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                viewModel.setBerlaku(new Date(year-1900, monthOfYear, dayOfMonth));
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                edVoucherEnd.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        viewModel.getTanggal().observe(this, tanggal ->{
            edVoucherEnd.setText(tanggal);
        });

        MaterialButton btnChooseProduct = findViewById(R.id.btnVoucherProduct);
        btnChooseProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseIntent = new Intent(AddPromoActivity.this, ChoosePromoProductActivity.class);
                startActivityForResult(chooseIntent, REQUEST_CHOOSE_PRODUCTS);
            }
        });

        ProgressDialog loadScreen = new ProgressDialog(this);
        loadScreen.setCancelable(false);
        viewModel.getLoadingTitle().observe(this, loadScreen::setTitle);
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                loadScreen.show();
            }
            else{
                loadScreen.dismiss();
            }
        });

        TextInputLayout[] tlS = {findViewById(R.id.tlVoucherJudul), findViewById(R.id.tlVoucherDate), findViewById(R.id.tlVoucherMin), findViewById(R.id.tlVoucherMax), findViewById(R.id.tlVoucherPercent), findViewById(R.id.tlVoucherPercent)};
        TextView tvProductError = findViewById(R.id.tvVoucherErrorProduct);
        viewModel.getFieldErrors().observe(this, errors -> {
            for (int i = 0; i < errors.length; i++){
                if(i == 4){
                    tvProductError.setText(errors[i]);
                }
                else{
                    tlS[i].setError(errors[i]);
                }
            }
        });

        ScrollView svActivity = findViewById(R.id.svAddVoucher);
        viewModel.getFocusError().observe(this, focus ->{
            if(focus == 4){
                svActivity.scrollTo(0, tvProductError.getTop());
            }
            else{
                tlS[focus].getEditText().requestFocus();
            }
        });

        viewModel.isAdded().observe(this, added -> {
            startActivity(new Intent(AddPromoActivity.this, ListPromoActivity.class));
        });

        VoucherProductAdapter adapter = new VoucherProductAdapter();

        RecyclerView rvProducts = findViewById(R.id.rvVoucherProducts);
        rvProducts.setAdapter(adapter);

        viewModel.getChosenProductsVMs().observe(this, productItemViewModels -> {
            adapter.setProductVMs(productItemViewModels);
            adapter.notifyDataSetChanged();
        });

        MaterialToolbar tbAddPromo = findViewById(R.id.tbAddPromo);
        tbAddPromo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && requestCode == REQUEST_CHOOSE_PRODUCTS){
            ArrayList<String> chosen = data.getStringArrayListExtra("chosen_products");
            viewModel.setChosenProductsIds(chosen);
        }
    }
}