package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ClinicListAdapter;
import my.istts.finalproject.databinding.ActivityClinicListBinding;

import my.istts.finalproject.viewmodels.ClinicListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ClinicListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClinicListViewModel viewModel = new ClinicListViewModel();
        ActivityClinicListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_clinic_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbClinic = findViewById(R.id.tbClinicList);
        tbClinic.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ClinicListAdapter adapter = new ClinicListAdapter(new ClinicListAdapter.toClinicPage() {
            @Override
            public void toPage(String hp_klinik) {
                Intent clinicDetailntent = new Intent(ClinicListActivity.this, SellerProfileActivity.class);
                clinicDetailntent.putExtra("hp_seller", hp_klinik);
                startActivity(clinicDetailntent);
            }
        }, new ClinicListAdapter.navigateToClinic() {
            @Override
            public void navigateTo(String koordinat) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+koordinat);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        RecyclerView rvClinic = findViewById(R.id.rvClinicList);
        rvClinic.setLayoutManager(new LinearLayoutManager(this));
        rvClinic.setAdapter(adapter);

        viewModel.getClinics().observe(this, vms->{
            adapter.setClinics(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getAllClinics();

        MaterialButton btnSearch = findViewById(R.id.btnClinicNameSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getAllClinics();
            }
        });

    }
}