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
import com.example.sellerapp.databinding.ItemImportantProdsBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.HotelItemViewModel;

import java.util.ArrayList;

public class ImportantHotelAdapter extends RecyclerView.Adapter<ImportantHotelAdapter.ViewHolder> {
    private ArrayList<HotelItemViewModel> hotelVMs = new ArrayList<>();
    private onImportantHotelClicked onImportantHotelClickedListener;
    private Context ctx;

    public ImportantHotelAdapter(onImportantHotelClicked onImportantHotelClickedListener) {
        this.onImportantHotelClickedListener = onImportantHotelClickedListener;
    }

    public void setHotelVMs(ArrayList<HotelItemViewModel> hotelVMs) {
        this.hotelVMs = hotelVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImportantHotelBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_important_hotel, parent, false);
        return new ImportantHotelAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(hotelVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        hotelVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic -> {
            Glide.with(ctx).load(pic).into(holder.ivHotelPic);
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImportantHotelClickedListener.onClicked(hotelVMs.get(position).getId());
            }
        });

    }

    public interface onImportantHotelClicked{
        void onClicked(String id_hotel);
    }

    @Override
    public int getItemCount() {
        return hotelVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemImportantHotelBinding binding;
        ImageView ivHotelPic;
        public ViewHolder(ItemImportantHotelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivHotelPic = itemView.findViewById(R.id.ivImportantHotel);
        }
    }
}
