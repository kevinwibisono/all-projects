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
import com.example.sellerapp.databinding.ItemImportantHotelBinding;
import com.example.sellerapp.databinding.ItemKomplainBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.ComplainItemViewModel;

import java.util.ArrayList;

public class KomplainAdapter extends RecyclerView.Adapter<KomplainAdapter.ViewHolder> {
    private ArrayList<ComplainItemViewModel> complains = new ArrayList<>();
    private showComplainDetail showDetailCallback;
    private Context ctx;

    public KomplainAdapter(showComplainDetail showDetailCallback) {
        this.showDetailCallback = showDetailCallback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    public void setComplains(ArrayList<ComplainItemViewModel> complains) {
        this.complains = complains;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemKomplainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_komplain, parent, false);
        return new KomplainAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(complains.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailCallback.complainDetail(complains.get(position).getId(), complains.get(position).getIdPJ());
            }
        });

        complains.get(position).getPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivComplain);
        });
    }

    @Override
    public int getItemCount() {
        return complains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemKomplainBinding binding;
        ImageView ivComplain;
        public ViewHolder(ItemKomplainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivComplain = itemView.findViewById(R.id.ivComplainItems);
        }
    }

    public interface showComplainDetail{
        void complainDetail(String id_komplain, String id_pj);
    }
}
