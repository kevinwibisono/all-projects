package com.example.sellerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellerapp.R;
import com.example.sellerapp.databinding.ItemChatBinding;
import com.example.sellerapp.databinding.ItemDiskusiBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.DiscussListItemViewModel;

import java.util.ArrayList;

public class DiscussListAdapter extends RecyclerView.Adapter<DiscussListAdapter.ViewHolder> {
    private ArrayList<DiscussListItemViewModel> discussList = new ArrayList<>();
    private seeFullDiscuss fullDiscussCallback;
    private Context ctx;

    public DiscussListAdapter(seeFullDiscuss fullDiscussCallback) {
        this.fullDiscussCallback = fullDiscussCallback;
    }

    public void setDiscussList(ArrayList<DiscussListItemViewModel> discussList) {
        this.discussList = discussList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiskusiBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_diskusi, parent, false);
        return new DiscussListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);
        holder.binding.setViewmodel(discussList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullDiscussCallback.seeFull(discussList.get(position).getId_item(), discussList.get(position).getTipe());
            }
        });

        discussList.get(position).getItemPic().observe((LifecycleOwner) ctx, pic->{
            Glide.with(ctx).load(pic).into(holder.ivItem);
        });
    }

    @Override
    public int getItemCount() {
        return discussList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDiskusiBinding binding;
        ImageView ivItem;
        public ViewHolder(ItemDiskusiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivItem = itemView.findViewById(R.id.ivDiscussItem);
        }
    }

    public interface seeFullDiscuss{
        void seeFull(String id_item, int tipe);
    }
}
