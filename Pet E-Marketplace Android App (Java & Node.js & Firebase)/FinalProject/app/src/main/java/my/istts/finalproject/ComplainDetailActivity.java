package my.istts.finalproject;

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
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ComplainProductAdapter;

import my.istts.finalproject.databinding.ActivityComplainDetailBinding;
import my.istts.finalproject.viewmodels.ComplainDetailViewModel;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class ComplainDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ComplainDetailViewModel viewModel = new ComplainDetailViewModel(getApplication());
        ActivityComplainDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbComplainDetail = findViewById(R.id.tbComplainDetail);
        tbComplainDetail.setNavigationOnClickListener(new View.OnClickListener() {
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
                    Intent imageIntent = new Intent(ComplainDetailActivity.this, ImageViewerActivity.class);
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

        viewModel.getComplainItemPj().observe(this, pjItems->{
            adapter.setItemPJs(pjItems);
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
        viewModel.getOrderSeller().observe(this, seller->{});
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(ComplainDetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("lawan_bicara", viewModel.getOrderSeller().getValue());
                chatIntent.putExtra("id_item", getIntent().getStringExtra("id_pj"));
                chatIntent.putExtra("tipe", 1);
                startActivity(chatIntent);
            }
        });

        MaterialButton btnCancel = findViewById(R.id.btnComplainCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(ComplainDetailActivity.this)
                        .setTitle("Perhatian!")
                        .setMessage("Apakah anda yakin akan membatalkan komplain? Pesananmu akan otomatis diselesaikan")
                        .setNegativeButton("Tidak", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            viewModel.cancelComplain(getIntent().getStringExtra("id_pj"));
                        })
                        .show();
            }
        });

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Membatalkan Komplain...");
        loading.setCancelable(false);
        viewModel.isFinishingOrder().observe(this, finishing->{
            if(finishing){
                loading.show();
            }
            else{
                loading.dismiss();
                Intent backIntent = new Intent(ComplainDetailActivity.this, OrderDetailActivity.class);
                backIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backIntent);
            }
        });
    }
}