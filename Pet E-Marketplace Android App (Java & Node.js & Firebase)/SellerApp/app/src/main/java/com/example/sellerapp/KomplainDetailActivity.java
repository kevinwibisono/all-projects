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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.sellerapp.adapters.ComplainProductAdapter;
import com.example.sellerapp.databinding.ActivityKomplainDetailBinding;
import com.example.sellerapp.viewmodels.KomplainDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class KomplainDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain_detail);

        KomplainDetailViewModel viewModel = new KomplainDetailViewModel(getApplication());
        ActivityKomplainDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_komplain_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbKomplainDetail = findViewById(R.id.tbKomplainDetail);
        tbKomplainDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewModel.getBukti().observe(this, images->{
            ImageSlider complainSlider = findViewById(R.id.sliderComplain);
            ArrayList<SlideModel> slideModels = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                slideModels.add(new SlideModel(images.get(i), "", ScaleTypes.FIT));
            }
            complainSlider.setImageList(slideModels);
            complainSlider.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemSelected(int i) {
                    Intent imageIntent = new Intent(KomplainDetailActivity.this, ImageViewerActivity.class);
                    imageIntent.putExtra("url", slideModels.get(i).getImageUrl());
                    startActivity(imageIntent);
                }
            });
        });

        viewModel.getPJDetail(getIntent().getStringExtra("id_pj"));
        viewModel.getComplainDetail(getIntent().getStringExtra("id_komplain"));

        ComplainProductAdapter adapter = new ComplainProductAdapter(new ComplainProductAdapter.onCheckedChanged() {
            @Override
            public void onChecked(int index, boolean checked) {
            }
        }, false);

        RecyclerView rvComplain = findViewById(R.id.rvProductComplain);
        rvComplain.setAdapter(adapter);

        viewModel.getComplainItemPj().observe(this, itemPJs->{
            adapter.setItemPJs(itemPJs);
            adapter.notifyDataSetChanged();
        });

        ImageView ivOrder = findViewById(R.id.ivComplainOrder);
        viewModel.getOrderItemsPic().observe(this, picture->{
            Glide.with(this).load(picture).into(ivOrder);
        });

        TextView tvLink = findViewById(R.id.tvComplainLink);
        viewModel.getComplainVideo().observe(this, link->{
            tvLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
            });
        });

        MaterialButton btnChat = findViewById(R.id.btnComplainChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(KomplainDetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("lawan_bicara", viewModel.getOrderBuyer().getValue());
                chatIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
                startActivity(chatIntent);
            }
        });

        String[] alertTitle = {"Apakah anda yakin akan menerima komplain? Pesanan akan otomatis diselesaikan", "Apakah anda yakin akan menolak komplain?"};
        MaterialButton[] btns = {findViewById(R.id.btnComplainAccept), findViewById(R.id.btnComplainReject)};
        for (int i = 0; i < 2; i++) {
            final int index = i;
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialAlertDialogBuilder(KomplainDetailActivity.this)
                            .setTitle("Perhatian!")
                            .setMessage(alertTitle[index])
                            .setNegativeButton("Tidak", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .setPositiveButton("Ya", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                viewModel.changeComplainStatus(getIntent().getStringExtra("id_komplain"), index+1, getIntent().getStringExtra("id_pj"));
                            })
                            .show();
                }
            });
        }

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Mengubah Status Komplain...");
        loading.setCancelable(false);
        viewModel.isFinishingOrder().observe(this, finishing->{
            if(finishing){
                loading.show();
            }
            else{
                loading.dismiss();
                Intent backIntent = new Intent(KomplainDetailActivity.this, MainActivity.class);
                backIntent.putExtra("Role", 0);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backIntent);
            }
        });

    }
}