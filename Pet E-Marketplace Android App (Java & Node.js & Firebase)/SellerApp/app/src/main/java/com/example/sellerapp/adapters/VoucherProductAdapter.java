package com.example.sellerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellerapp.R;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;

import java.util.ArrayList;

public class VoucherProductAdapter extends RecyclerView.Adapter<VoucherProductAdapter.ViewHolder> {
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
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v = inf.inflate(R.layout.item_voucher_product,parent,false);
        return new VoucherProductAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvProduct.setText(productVMs.get(position).getName());

        productVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
            Glide.with(ctx).load(pic).into(holder.ivProduct);
        });
    }

    @Override
    public int getItemCount() {
        return productVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProduct;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivVoucherProduct);
            tvProduct = itemView.findViewById(R.id.tvVoucherProduct);
        }
    }
}
