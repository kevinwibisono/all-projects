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

import com.example.sellerapp.R;
import com.example.sellerapp.databinding.ItemHistorySaldoBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.RiwayatItemViewModel;

import java.util.ArrayList;

public class RiwayatSaldoAdapter extends RecyclerView.Adapter<RiwayatSaldoAdapter.ViewHolder> {
    private ArrayList<RiwayatItemViewModel> historiesVMs = new ArrayList<>();
    private enlargeBuktiTransfer buktiTransferCallback;
    private Context ctx;

    public RiwayatSaldoAdapter(enlargeBuktiTransfer buktiTransferCallback) {
        this.buktiTransferCallback = buktiTransferCallback;
    }

    public void setHistoriesVMs(ArrayList<RiwayatItemViewModel> historiesVMs) {
        this.historiesVMs = historiesVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistorySaldoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_history_saldo, parent, false);
        return new RiwayatSaldoAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);
        holder.binding.setViewmodel(historiesVMs.get(position));

        int[] bankIcons = {R.drawable.iconagi, R.drawable.iconbca, R.drawable.iconbni, R.drawable.iconcimb, R.drawable.iconmandiri};
        holder.ivRekening.setImageResource(bankIcons[historiesVMs.get(position).getJenisRek()]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buktiTransferCallback.onEnlarge(historiesVMs.get(position).gambar_bukti());
            }
        });
    }

    public interface enlargeBuktiTransfer{
        void onEnlarge(String url_gambar);
    }

    @Override
    public int getItemCount() {
        return historiesVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistorySaldoBinding binding;
        ImageView ivRekening, ivBuktiTrf;

        public ViewHolder(ItemHistorySaldoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivRekening = itemView.findViewById(R.id.ivHistoryRek);
        }

    }
}
