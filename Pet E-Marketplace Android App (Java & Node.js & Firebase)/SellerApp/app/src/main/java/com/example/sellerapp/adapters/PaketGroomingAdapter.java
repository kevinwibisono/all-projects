package com.example.sellerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp.R;
import com.example.sellerapp.databinding.ItemGroomingPackageBinding;
import com.example.sellerapp.databinding.ItemImportantProdsBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.PaketGroomingItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class PaketGroomingAdapter extends RecyclerView.Adapter<PaketGroomingAdapter.ViewHolder> {
    private ArrayList<PaketGroomingItemViewModel> packVMs = new ArrayList<>();
    private onGroomingPackDelete onGroomingPackDeleteCallback;
    private onGroomingPackUpdate onGroomingPackUpdateCallback;
    private Context ctx;

    public PaketGroomingAdapter(onGroomingPackDelete onGroomingPackDeleteCallback, onGroomingPackUpdate onGroomingPackUpdateCallback) {
        this.onGroomingPackDeleteCallback = onGroomingPackDeleteCallback;
        this.onGroomingPackUpdateCallback = onGroomingPackUpdateCallback;
    }

    public void setPackVMs(ArrayList<PaketGroomingItemViewModel> packVMs) {
        this.packVMs = packVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroomingPackageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_grooming_package, parent, false);
        return new PaketGroomingAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(packVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGroomingPackDeleteCallback.onDelete(position);
            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGroomingPackUpdateCallback.onUpdate(packVMs.get(position).getId());
            }
        });
    }

    public interface onGroomingPackDelete{
        void onDelete(int pos);
    }

    public interface onGroomingPackUpdate{
        void onUpdate(String id_pack);
    }

    @Override
    public int getItemCount() {
        return packVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemGroomingPackageBinding binding;
        MaterialButton btnDelete, btnUpdate;
        public ViewHolder(ItemGroomingPackageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            btnDelete = itemView.findViewById(R.id.btnDeletePaket);
            btnUpdate = itemView.findViewById(R.id.btnUpdatePaket);
        }
    }
}
