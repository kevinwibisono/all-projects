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
import com.example.sellerapp.databinding.ItemImportantProdsBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.ProductItemViewModel;

import java.util.ArrayList;

public class ImportantAdapter extends RecyclerView.Adapter<ImportantAdapter.ViewHolder> {
    private ArrayList<ProductItemViewModel> prodVMs = new ArrayList<>();
    private onImportantItemClicked onImportantItemClickedListener;
    private Context ctx;

    public ImportantAdapter(onImportantItemClicked onImportantItemClickedListener) {
        this.onImportantItemClickedListener = onImportantItemClickedListener;
    }

    public void setProdVMs(ArrayList<ProductItemViewModel> prodVMs) {
        this.prodVMs = prodVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImportantProdsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_important_prods, parent, false);
        return new ImportantAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(prodVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        prodVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic -> {
            Glide.with(ctx).load(pic).into(holder.ivProductPic);
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImportantItemClickedListener.onClicked(prodVMs.get(position).getId());
            }
        });

    }

    public interface onImportantItemClicked{
        void onClicked(String id_product);
    }

    @Override
    public int getItemCount() {
        return prodVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemImportantProdsBinding binding;
        ImageView ivProductPic;
        public ViewHolder(ItemImportantProdsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivProductPic = itemView.findViewById(R.id.ivImportantProductPicture);
        }
    }
}
