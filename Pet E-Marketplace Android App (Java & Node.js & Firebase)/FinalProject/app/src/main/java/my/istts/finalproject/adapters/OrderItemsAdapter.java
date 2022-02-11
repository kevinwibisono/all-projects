package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemListOrderBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.ItemPJViewModel;

import java.util.ArrayList;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {
    private ArrayList<ItemPJViewModel> itemVMs = new ArrayList<>();
    private onClickOrderItemProduct onClickOrderItemProductCallback;
    private onClickOrderItemHotel onClickOrderItemHotelCallback;
    private Context ctx;

    public OrderItemsAdapter(onClickOrderItemProduct onClickOrderItemProductCallback, onClickOrderItemHotel onClickOrderItemHotelCallback) {
        this.onClickOrderItemProductCallback = onClickOrderItemProductCallback;
        this.onClickOrderItemHotelCallback = onClickOrderItemHotelCallback;
    }

    public void setItemVMs(ArrayList<ItemPJViewModel> itemVMs) {
        this.itemVMs = itemVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListOrderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_list_order, parent, false);
        return new OrderItemsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(itemVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        Glide.with(ctx).load(itemVMs.get(position).getPicture()).into(holder.ivProduct);
        Glide.with(ctx).load(itemVMs.get(position).getPicture()).into(holder.ivHotel);

        holder.partProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOrderItemProductCallback.onClickProduct(itemVMs.get(position).getId());
            }
        });

        holder.partHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOrderItemHotelCallback.onClickHotel(itemVMs.get(position).getId());
            }
        });
    }

    public interface onClickOrderItemProduct{
        void onClickProduct(String id_produk);
    }

    public interface onClickOrderItemHotel{
        void onClickHotel(String id_kamar);
    }

    @Override
    public int getItemCount() {
        return itemVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListOrderBinding binding;
        ImageView ivProduct, ivHotel;
        LinearLayout partProduct, partHotel;
        public ViewHolder(ItemListOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivProduct = itemView.findViewById(R.id.ivOrderItemProduct);
            ivHotel = itemView.findViewById(R.id.ivOrderItemHotel);
            partProduct = itemView.findViewById(R.id.partOrderItemsProduct);
            partHotel = itemView.findViewById(R.id.partOrderItemsHotel);
        }
    }
}
