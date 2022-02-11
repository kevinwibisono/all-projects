package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ComplainAdapter;

import my.istts.finalproject.databinding.ActivityComplainsBinding;
import my.istts.finalproject.viewmodels.ComplainsViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ComplainsActivity extends AppCompatActivity {

    private ComplainsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ComplainsViewModel();
        ActivityComplainsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_complains);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbComplains = findViewById(R.id.tbComplaints);
        tbComplains.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SwipeRefreshLayout complainsList = findViewById(R.id.pageComplainsRefresh);
        complainsList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getPJDetail(getIntent().getStringExtra("id_pj"));
                complainsList.setRefreshing(false);
            }
        });


        ComplainAdapter adapter = new ComplainAdapter(new ComplainAdapter.onComplainClicked() {
            @Override
            public void complainClicked(String id_komplain) {
                Intent compIntent = new Intent(ComplainsActivity.this, ComplainDetailActivity.class);
                compIntent.putExtra("id_komplain", id_komplain);
                compIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
                startActivity(compIntent);
            }
        });

        viewModel.getPjComplains().observe(this, complains->{
            adapter.setComplains(complains);
            adapter.notifyDataSetChanged();
        });

        RecyclerView rvComplains = findViewById(R.id.rvPJComplains);
        rvComplains.setAdapter(adapter);
        rvComplains.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        ImageView ivOrder = findViewById(R.id.ivComplainsOrder);
        viewModel.getOrderItemsPic().observe(this, picture->{
            Glide.with(this).load(picture).into(ivOrder);
        });


        MaterialButton btnAddComplain = findViewById(R.id.btnAddComplain);
        viewModel.getCanAdd().observe(ComplainsActivity.this, canAdd->{
            if(canAdd){
                btnAddComplain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent compIntent = new Intent(ComplainsActivity.this, AddComplainActivity.class);
                        compIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
                        startActivity(compIntent);
                    }
                });
            }
            else{
                btnAddComplain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ComplainsActivity.this, "Tidak Dapat Menambahkan Komplain Baru. Terdapat komplain yang belum ditanggapi penjual atau sudah disetujui", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getPJDetail(getIntent().getStringExtra("id_pj"));
    }
}