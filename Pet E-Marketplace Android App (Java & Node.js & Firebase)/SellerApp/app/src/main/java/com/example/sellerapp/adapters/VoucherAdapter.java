package com.example.sellerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp.R;
import com.example.sellerapp.databinding.ItemVoucherBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.PromoItemViewModel;

import java.util.ArrayList;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    ArrayList<PromoItemViewModel> voucherVMs = new ArrayList<>();
    detailVoucher voucherClickCallback;

    public void setVoucherVMs(ArrayList<PromoItemViewModel> voucherVMs) {
        this.voucherVMs = voucherVMs;
    }

    public VoucherAdapter(detailVoucher voucherClickCallback) {
        this.voucherClickCallback = voucherClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVoucherBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_voucher, parent, false);
        return new VoucherAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(voucherVMs.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voucherClickCallback.showVoucher(voucherVMs.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemVoucherBinding binding;
        public ViewHolder(ItemVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface detailVoucher{
        void showVoucher(String id);
    }
}
