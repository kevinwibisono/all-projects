package my.istts.finalproject.adapters;

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
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemOrderBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.PesananItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<PesananItemViewModel> orderVMs = new ArrayList<>();
    private showDetailOrder showDetail;
    private Context ctx;

    public OrderAdapter(showDetailOrder showDetail) {
        this.showDetail = showDetail;
    }

    public void setOrderVMs(ArrayList<PesananItemViewModel> orderVMs) {
        this.orderVMs = orderVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_order, parent, false);
        return new OrderAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(orderVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail.show(orderVMs.get(position).getId(), orderVMs.get(position).getJenis());
            }
        });

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail.show(orderVMs.get(position).getId(), orderVMs.get(position).getJenis());
            }
        });

        orderVMs.get(position).getSellerPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivSellerClinic);
            Glide.with(ctx).load(picture).into(holder.ivSeller);
        });

        orderVMs.get(position).getItemPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivItem);
            Glide.with(ctx).load(picture).into(holder.ivHotel);
        });

    }

    public interface showDetailOrder{
        void show(String id_pj, int jenisPj);
    }

    @Override
    public int getItemCount() {
        return orderVMs.size();
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
