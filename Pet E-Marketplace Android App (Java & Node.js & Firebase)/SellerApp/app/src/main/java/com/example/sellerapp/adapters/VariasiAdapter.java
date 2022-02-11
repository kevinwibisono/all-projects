package com.example.sellerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp.R;
import com.example.sellerapp.databinding.ItemVariasiProdukBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.VariantProductViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class VariasiAdapter extends RecyclerView.Adapter<VariasiAdapter.ViewHolder> {
    private ArrayList<VariantProductViewModel> variants = new ArrayList<>();
    private deleteVariasi deleteVariasiListener;
    private updateVariasi updateVariasiListener;

    public VariasiAdapter(deleteVariasi deleteVariasiListener, updateVariasi updateVariasiListener) {
        this.deleteVariasiListener = deleteVariasiListener;
        this.updateVariasiListener = updateVariasiListener;
    }

    public void setVariants(ArrayList<VariantProductViewModel> variants) {
        this.variants = variants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVariasiProdukBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_variasi_produk, parent, false);
        return new VariasiAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(variants.get(position));

        holder.ivPicture.setImageBitmap(variants.get(position).getGambar());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteVariasiListener.onDelete(position);
            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateVariasiListener.onUpdate(variants.get(position).getIdVariant());
            }
        });
    }

    @Override
    public int getItemCount() {
        return variants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemVariasiProdukBinding binding;
        ImageView ivPicture;
        MaterialButton btnDelete, btnUpdate;

        public ViewHolder(ItemVariasiProdukBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivPicture = itemView.findViewById(R.id.itemVariasiPicture);
            btnDelete = itemView.findViewById(R.id.btnDeleteVariasi);
            btnUpdate = itemView.findViewById(R.id.btnUpdateVariasi);
        }
    }

    public interface deleteVariasi{
        void onDelete(int index);
    }

    public interface updateVariasi{
        void onUpdate(String id_variasi);
    }
}
