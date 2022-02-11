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
import com.example.sellerapp.databinding.ItemHotelBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.HotelItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {
    private ArrayList<HotelItemViewModel> hotelVMs = new ArrayList<>();
    private Context ctx;
    private showRoomDetail detailRoomCallback;
    private showRoomPreview previewRoomCallback;

    public HotelAdapter(showRoomDetail detailRoomCallback, showRoomPreview previewRoomCallback) {
        this.detailRoomCallback = detailRoomCallback;
        this.previewRoomCallback = previewRoomCallback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    public void setHotelVMs(ArrayList<HotelItemViewModel> hotelVMs) {
        this.hotelVMs = hotelVMs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHotelBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_hotel, parent, false);
        return new HotelAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(hotelVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailRoomCallback.roomDetail(hotelVMs.get(position).getId());
            }
        });

        holder.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewRoomCallback.roomPreview(hotelVMs.get(position).getId());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewRoomCallback.roomPreview(hotelVMs.get(position).getId());
            }
        });

        hotelVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
            Glide.with(ctx).load(pic).into(holder.ivHotel);
        });

    }

    @Override
    public int getItemCount() {
        return hotelVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHotelBinding binding;
        MaterialButton btnEdit, btnPreview;
        ImageView ivHotel;
        public ViewHolder(ItemHotelBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            this.binding = binding;
            btnEdit = itemView.findViewById(R.id.btnRoomEdit);
            btnPreview = itemView.findViewById(R.id.btnRoomPreview);
            ivHotel = itemView.findViewById(R.id.itemHotelPicture);
        }
    }

    public interface showRoomDetail{
        void roomDetail(String id);
    }

    public interface showRoomPreview{
        void roomPreview(String id);
    }
}
