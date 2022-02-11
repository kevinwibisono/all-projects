package my.istts.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.AddressChooseAdapter;
import my.istts.finalproject.databinding.ActivityAddressChooseBinding;

import my.istts.finalproject.viewmodels.AddressChooseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddressChooseActivity extends AppCompatActivity {

    private int REQUEST_ADD_ADDRESS = 3;
    private AddressChooseViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AddressChooseViewModel(getApplication());
        ActivityAddressChooseBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_address_choose);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAddChoose = findViewById(R.id.tbAddressChoose);
        tbAddChoose.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AddressChooseAdapter adapter = new AddressChooseAdapter(new AddressChooseAdapter.addressChoose() {
            @Override
            public void onChosen(String id) {
                viewModel.setSelected(id);
                Intent resultintent = new Intent();
                resultintent.putExtra("id_alamat", id);
                setResult(2, resultintent);
                finish();
            }
        }, new AddressChooseAdapter.addressUpdate() {
            @Override
            public void onUpdate(String id) {
                Intent updateIntent = new Intent(AddressChooseActivity.this, AddAddressActivity.class);
                updateIntent.putExtra("id_alamat", id);
                startActivityForResult(updateIntent, REQUEST_ADD_ADDRESS);
            }
        }, new AddressChooseAdapter.addressDelete() {
            @Override
            public void onDelete(String id) {
                viewModel.deleteAddress(id);
            }
        });

        RecyclerView rvAddress = findViewById(R.id.rvAddressChoose);
        rvAddress.setLayoutManager(new LinearLayoutManager(this));
        rvAddress.setAdapter(adapter);

        viewModel.getAddresses();

        viewModel.getAddrsVMs().observe(this, vms ->{
            adapter.setAddrsVMs(vms);
            adapter.notifyDataSetChanged();
        });

        FloatingActionButton btnAdd = findViewById(R.id.btnAddAddress);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAddress = new Intent(AddressChooseActivity.this, AddAddressActivity.class);
                startActivityForResult(addAddress, REQUEST_ADD_ADDRESS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ADD_ADDRESS){
            viewModel.getAddresses();
        }
    }
}