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
import com.example.sellerapp.databinding.ItemChooseRekeningBinding;
import com.example.sellerapp.databinding.ItemProductBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.RekeningItemViewModel;

import java.util.ArrayList;

public class RekeningAdapter extends RecyclerView.Adapter<RekeningAdapter.ViewHolder> {
    private ArrayList<RekeningItemViewModel> rekVMs = new ArrayList<>();
    private chooseRekening chooseRekeningCallback;
    private Context ctx;

    public RekeningAdapter(chooseRekening chooseRekeningCallback) {
        this.chooseRekeningCallback = chooseRekeningCallback;
    }

    public void setRekVMs(ArrayList<RekeningItemViewModel> rekVMs) {
        this.rekVMs = rekVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChooseRekeningBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_choose_rekening, parent, false);
        return new RekeningAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(rekVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        int[] bankIcons = {R.drawable.iconagi, R.drawable.iconbca, R.drawable.iconbni, R.drawable.iconcimb, R.drawable.iconmandiri};
        holder.ivRek.setImageResource(bankIcons[rekVMs.get(position).getJenis()]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseRekeningCallback.onChosen(rekVMs.get(position).getId());
            }
        });
    }

    public interface chooseRekening{
        void onChosen(int rekId);
    }

    @Override
    public int getItemCount() {
        return rekVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemChooseRekeningBinding binding;
        ImageView ivRek;
        public ViewHolder(ItemChooseRekeningBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            this.ivRek = itemView.findViewById(R.id.ivItemRek);
        }
    }
}
