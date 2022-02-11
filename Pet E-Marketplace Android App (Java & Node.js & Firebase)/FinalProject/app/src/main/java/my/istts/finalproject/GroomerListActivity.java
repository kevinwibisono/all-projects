package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.GroomerAdapter;
import my.istts.finalproject.databinding.ActivityGroomerListBinding;

import my.istts.finalproject.viewmodels.GroomerListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class GroomerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GroomerListViewModel viewModel = new GroomerListViewModel();
        ActivityGroomerListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_groomer_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbGrooms = findViewById(R.id.tbGrooming);
        tbGrooms.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GroomerAdapter adapter = new GroomerAdapter(new GroomerAdapter.onGroomerClicked() {
            @Override
            public void onClicked(String hp_groomer) {
                Intent groomerProfileIntent = new Intent(GroomerListActivity.this, SellerProfileActivity.class);
                groomerProfileIntent.putExtra("hp_seller", hp_groomer);
                startActivity(groomerProfileIntent);
            }
        });

        RecyclerView rvGrooming = findViewById(R.id.rvGrooming);
        rvGrooming.setLayoutManager(new LinearLayoutManager(this));
        rvGrooming.setAdapter(adapter);

        viewModel.getGroomers().observe(this, vms->{
            adapter.setGroomerVMs(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getAllGroomer();

        MaterialButton btnSearch = findViewById(R.id.btnGroomerNameSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getAllGroomer();
            }
        });
    }

}