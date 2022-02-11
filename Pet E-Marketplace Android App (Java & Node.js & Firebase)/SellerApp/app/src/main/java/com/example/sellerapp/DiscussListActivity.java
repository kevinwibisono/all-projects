package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sellerapp.adapters.DiscussListAdapter;
import com.example.sellerapp.databinding.ActivityDiscussListBinding;
import com.example.sellerapp.databinding.ActivityFullDiscussionBinding;
import com.example.sellerapp.viewmodels.DiscussListViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class DiscussListActivity extends AppCompatActivity {

    private DiscussListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss_list);

        viewModel = new DiscussListViewModel(getApplication());
        ActivityDiscussListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_discuss_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbDiscussList = findViewById(R.id.tbDissussList);
        tbDiscussList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DiscussListAdapter adapter = new DiscussListAdapter(new DiscussListAdapter.seeFullDiscuss() {
            @Override
            public void seeFull(String id_item, int tipe) {
                Intent discussIntent = new Intent(DiscussListActivity.this, FullDiscussionActivity.class);
                if(tipe == 0){
                    discussIntent.putExtra("id_produk", id_item);
                }
                else{
                    discussIntent.putExtra("id_kamar", id_item);
                }
                startActivity(discussIntent);
            }
        });

        RecyclerView rvDiscuss = findViewById(R.id.rvDiscussList);
        rvDiscuss.setAdapter(adapter);

        viewModel.getDiscuss().observe(this, discuss->{
            adapter.setDiscussList(discuss);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.searchDiscussList();
    }
}