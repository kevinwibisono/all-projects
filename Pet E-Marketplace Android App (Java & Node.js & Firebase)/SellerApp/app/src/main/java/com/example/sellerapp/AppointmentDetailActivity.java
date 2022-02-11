package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sellerapp.adapters.PetAppointmentAdapter;
import com.example.sellerapp.databinding.ActivityAppointmentDetailBinding;
import com.example.sellerapp.viewmodels.AppointmentDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class AppointmentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppointmentDetailViewModel viewModel = new AppointmentDetailViewModel();
        ActivityAppointmentDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAppoDetail = findViewById(R.id.tbAppoDetail);
        tbAppoDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PetAppointmentAdapter adapter = new PetAppointmentAdapter();
        RecyclerView rvPets = findViewById(R.id.rvAppoDetail);
        rvPets.setAdapter(adapter);

        viewModel.getAppointmentDetail(getIntent().getStringExtra("id_pj"));

        viewModel.getPetsName().observe(this, names->{
            adapter.setAges(viewModel.getPetsAge().getValue());
            adapter.setKinds(viewModel.getPetsKind().getValue());
            adapter.setNames(names);
            adapter.notifyDataSetChanged();
        });

        ImageView ivClinic = findViewById(R.id.ivAppoDetailClinic);
        viewModel.getSellerPic().observe(this, picture->{
            Glide.with(this).load(picture).into(ivClinic);
        });

        MaterialButton btnChat = findViewById(R.id.btnAppoDetailChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(AppointmentDetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("lawan_bicara", viewModel.getBuyerEmail().getValue());
                chatIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
                startActivity(chatIntent);
            }
        });

        MaterialButton btnNavigate = findViewById(R.id.btnAppoDetailNavigate);
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+viewModel.getCoordinate().getValue());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        MaterialButton btnAccept = findViewById(R.id.btnAppoDetailAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.acceptAppointment();
            }
        });

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Mengubah Status...");
        loading.setCancelable(true);
        viewModel.isLoading().observe(this, updateOrderLoading->{
            if(updateOrderLoading){
                loading.show();
            }
            else{
                loading.dismiss();
            }
        });
    }
}