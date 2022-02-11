package my.istts.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ActivityAddAddressBinding;

import my.istts.finalproject.viewmodels.AddAddressViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddAddressActivity extends AppCompatActivity {

    private int REQUEST_SEARCH_ADDRESS = 4;
    private AddAddressViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new AddAddressViewModel(getApplication());
        ActivityAddAddressBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_address);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        if(getIntent().hasExtra("id_alamat")){
            viewModel.setAddress(getIntent().getStringExtra("id_alamat"));
        }

        MaterialToolbar tbAdd = findViewById(R.id.tbAddAddress);
        tbAdd.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextInputEditText edAddress = findViewById(R.id.edAddressFull);
        edAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchAddress = new Intent(AddAddressActivity.this, AddressSearchActivity.class);
                startActivityForResult(searchAddress, REQUEST_SEARCH_ADDRESS);
            }
        });

        TextInputLayout[] tls = {findViewById(R.id.tlAddressName), findViewById(R.id.tlAddressPhone), findViewById(R.id.tlAddressFull)};
        viewModel.getErrors().observe(this, errors ->{
            for (int i = 0; i < 3; i++) {
                tls[i].setError(errors[i]);
            }
        });

        viewModel.getFocusedField().observe(this, tlNum -> {
            tls[tlNum].getEditText().requestFocus();
        });

        viewModel.isDone().observe(this, done -> {
            Intent resultintent = new Intent();
            setResult(5, resultintent);
            finish();
        });

        ProgressDialog loadingScreen = new ProgressDialog(this);
        viewModel.getLoadingTitle().observe(this, loadingScreen::setTitle);
        loadingScreen.setCancelable(false);
        viewModel.isLoading().observe(this, loading ->{
            if(loading){
                loadingScreen.show();
            }
            else{
                loadingScreen.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SEARCH_ADDRESS && data != null){
            String alamat = data.getStringExtra("alamat");
            String kota = data.getStringExtra("kota");
            String kodepos = data.getStringExtra("kodepos");
            String kelurahan = data.getStringExtra("kelurahan");
            String kecamatan = data.getStringExtra("kecamatan");
            String koordinat = data.getStringExtra("koordinat");
            viewModel.getHereAddress(alamat, kota, kodepos, kelurahan, kecamatan, koordinat);
        }
    }
}