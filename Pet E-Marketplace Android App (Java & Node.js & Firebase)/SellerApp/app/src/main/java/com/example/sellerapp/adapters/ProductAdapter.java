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
import com.example.sellerapp.databinding.ItemProductBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<ProductItemViewModel> productVM = new ArrayList<>();
    private showProductDetail detailProductCallback;
    private showProductPreview previewProductCallback;
    private Context ctx;

    public ProductAdapter(showProductDetail detailProductCallback, showProductPreview previewProductCallback) {
        this.detailProductCallback = detailProductCallback;
        this.previewProductCallback = previewProductCallback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    public void setVM(ArrayList<ProductItemViewModel> productVM) {
        this.productVM = productVM;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_product, parent, false);
        return new ProductAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.productBinding.setViewmodel(productVM.get(position));
        holder.productBinding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailProductCallback.showDetail(productVM.get(position).getId());
            }
        });

        holder.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewProductCallback.showPreview(productVM.get(position).getId());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewProductCallback.showPreview(productVM.get(position).getId());
            }
        });

        productVM.get(position).getPicture().observe((LifecycleOwner) ctx, picture -> {
            if(!picture.equals("")){
                Glide.with(ctx).load(picture).into(holder.ivProductPic);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productVM.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding productBinding;
        MaterialButton btnEdit, btnPreview;
        ImageView ivProductPic;

        public ViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            productBinding = binding;
            View itemView = binding.getRoot();
            btnEdit = itemView.findViewById(R.id.btnProductEdit);
            btnPreview = itemView.findViewById(R.id.btnProductPreview);
            ivProductPic = itemView.findViewById(R.id.itemProductPicture);
        }

    }

    public interface showProductDetail{
        void showDetail(String id);
    }

    public interface showProductPreview{
        void showPreview(String id);
    }
}
