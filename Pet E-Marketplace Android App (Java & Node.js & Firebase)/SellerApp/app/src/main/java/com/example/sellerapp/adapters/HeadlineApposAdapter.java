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
import com.example.sellerapp.databinding.ItemChatBinding;
import com.example.sellerapp.databinding.ItemHeadlineAppoBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.AppointmentItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class HeadlineApposAdapter extends RecyclerView.Adapter<HeadlineApposAdapter.ViewHolder> {
    private ArrayList<AppointmentItemViewModel> appos = new ArrayList<>();
    private onHeadlineAppoClicked onHeadlineAppoClickedListener;
    private Context ctx;

    public HeadlineApposAdapter(onHeadlineAppoClicked onHeadlineAppoClickedListener) {
        this.onHeadlineAppoClickedListener = onHeadlineAppoClickedListener;
    }

    public void setAppos(ArrayList<AppointmentItemViewModel> appos) {
        this.appos = appos;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHeadlineAppoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_headline_appo, parent, false);
        return new HeadlineApposAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(appos.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHeadlineAppoClickedListener.onAppoClicked(appos.get(position).getIdPJ());
            }
        });

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHeadlineAppoClickedListener.onAppoClicked(appos.get(position).getIdPJ());
            }
        });
    }

    @Override
    public int getItemCount() {
        return appos.size();
    }

    public interface onHeadlineAppoClicked{
        void onAppoClicked(String id_pj);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHeadlineAppoBinding binding;
        MaterialButton btnDetail;
        public ViewHolder(ItemHeadlineAppoBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            this.binding = binding;
            btnDetail = itemView.findViewById(R.id.btnDetailHeadlineAppo);
        }
    }
}
