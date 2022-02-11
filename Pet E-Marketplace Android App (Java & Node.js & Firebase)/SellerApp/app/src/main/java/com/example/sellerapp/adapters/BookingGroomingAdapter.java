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
import com.example.sellerapp.databinding.ItemBookingGroomingBinding;
import com.example.sellerapp.databinding.ItemGroomingPackageBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.GroomingItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class BookingGroomingAdapter extends RecyclerView.Adapter<BookingGroomingAdapter.ViewHolder> {
    private ArrayList<GroomingItemViewModel> groomings = new ArrayList<>();
    private onGroomingClicked onGroomingClickedCallback;
    private Context ctx;

    public BookingGroomingAdapter(onGroomingClicked onGroomingClickedCallback) {
        this.onGroomingClickedCallback = onGroomingClickedCallback;
    }

    public void setGroomings(ArrayList<GroomingItemViewModel> groomings) {
        this.groomings = groomings;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingGroomingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_booking_grooming, parent, false);
        return new BookingGroomingAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(groomings.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGroomingClickedCallback.onClicked(groomings.get(position).getIdPJ());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGroomingClickedCallback.onClicked(groomings.get(position).getIdPJ());
            }
        });
    }

    public interface onGroomingClicked{
        void onClicked(String id_pj);
    }

    @Override
    public int getItemCount() {
        return groomings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBookingGroomingBinding binding;
        MaterialButton btnDetail;
        public ViewHolder(ItemBookingGroomingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            btnDetail = itemView.findViewById(R.id.btnGroomingDetail);
        }
    }
}
