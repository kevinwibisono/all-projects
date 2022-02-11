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
import com.example.sellerapp.databinding.ItemOrderBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.PesananItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<PesananItemViewModel> orders;
    private onShowOrderDetail onShowOrderDetailCallback;
    private Context ctx;

    public OrderAdapter(onShowOrderDetail onShowOrderDetailCallback) {
        this.onShowOrderDetailCallback = onShowOrderDetailCallback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    public void setOrders(ArrayList<PesananItemViewModel> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_order, parent, false);
        return new OrderAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(orders.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowOrderDetailCallback.onShow(orders.get(position).getId(), orders.get(position).getJenis());
            }
        });

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowOrderDetailCallback.onShow(orders.get(position).getId(), orders.get(position).getJenis());
            }
        });

        orders.get(position).getBuyerPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivSellerClinic);
            Glide.with(ctx).load(picture).into(holder.ivSeller);
        });

        orders.get(position).getItemPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivItem);
            Glide.with(ctx).load(picture).into(holder.ivHotel);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public interface onShowOrderDetail{
        void onShow(String id_pj, int jenis_pj);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;
        MaterialButton btnDetail;
        ImageView ivSellerClinic, ivSeller, ivItem, ivHotel;
        public ViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            btnDetail = itemView.findViewById(R.id.btnDetailOrder);
            ivSeller = itemView.findViewById(R.id.ivOrderSeller);
            ivSellerClinic = itemView.findViewById(R.id.ivOrderSellerClinic);
            ivItem = itemView.findViewById(R.id.ivOrderItem);
            ivHotel = itemView.findViewById(R.id.ivOrderHotel);
        }
    }
}
