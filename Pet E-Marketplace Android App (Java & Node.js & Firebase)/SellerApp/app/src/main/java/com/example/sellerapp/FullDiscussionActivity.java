package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sellerapp.adapters.CommentAdapter;
import com.example.sellerapp.databinding.ActivityFullDiscussionBinding;
import com.example.sellerapp.viewmodels.FullDiscussionViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class FullDiscussionActivity extends AppCompatActivity {

    private FullDiscussionViewModel viewModel;
    private String id_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new FullDiscussionViewModel(getApplication());
        ActivityFullDiscussionBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_full_discussion);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbDiscuss = findViewById(R.id.tbDiscussReplies);
        tbDiscuss.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(getIntent().hasExtra("id_komentar")) {
            this.id_item = getIntent().getStringExtra("id_komentar");
            viewModel.setKomentar(getIntent().getStringExtra("id_komentar"));
        }
        else if(getIntent().hasExtra("id_produk")){
            this.id_item = getIntent().getStringExtra("id_produk");
            viewModel.setProduk(getIntent().getStringExtra("id_produk"));
        }
        else if(getIntent().hasExtra("id_kamar")){
            this.id_item = getIntent().getStringExtra("id_kamar");
            viewModel.setHotel(getIntent().getStringExtra("id_kamar"));
        }

        CommentAdapter adapter = new CommentAdapter(new CommentAdapter.openFullDiscussion() {
            @Override
            public void openFull(String id_komentar) {
                Intent fullDiscussion = new Intent(FullDiscussionActivity.this, FullDiscussionActivity.class);
                fullDiscussion.putExtra("id_komentar", id_komentar);
                startActivity(fullDiscussion);
            }
        });

        RecyclerView rvDiscuss = findViewById(R.id.rvDiscussReplies);
        rvDiscuss.setAdapter(adapter);
        rvDiscuss.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getComments().observe(this, comments->{
            adapter.setComments(comments);
            adapter.notifyDataSetChanged();
        });

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Menambahkan Komentar...");
        loading.setCancelable(false);
        viewModel.isAddLoading().observe(this, addloading->{
            if(addloading){
                loading.show();
            }
            else{
                loading.dismiss();
            }
        });

        ImageView ivProdRoom = findViewById(R.id.ivKomentarItem);
        viewModel.getItemPic().observe(this, picture->{
            Glide.with(this).load(picture).into(ivProdRoom);
        });

        ImageView ivCommentator = findViewById(R.id.ivPengomentar);
        viewModel.getPengomentarPic().observe(this, picture->{
            Glide.with(this).load(picture).into(ivCommentator);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getCommentsOfItem(id_item);
    }
}