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
import com.example.sellerapp.databinding.ItemProductChooseBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;

import java.util.ArrayList;

public class VoucherProductChooseAdapter extends RecyclerView.Adapter<VoucherProductChooseAdapter.ViewHolder> {
    ArrayList<ProductItemViewModel> productVMs = new ArrayList<>();
    Context ctx;

    public void setProductVMs(ArrayList<ProductItemViewModel> productVMs) {
        this.productVMs = productVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductChooseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_product_choose, parent, false);
        return new VoucherProductChooseAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(productVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        productVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
            Glide.with(ctx).load(pic).into(holder.ivProductChoose);
        });
    }

    @Override
    public int getItemCount() {
        return productVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductChooseBinding binding;
        ImageView ivProductChoose;
        public ViewHolder(ItemProductChooseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivProductChoose = itemView.findViewById(R.id.ivChooseProduct);
        }
    }
}
