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
import my.istts.finalproject.databinding.ItemCartInvalidBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.CartInvItemViewModel;

import java.util.ArrayList;

public class CartInvalidAdapter extends RecyclerView.Adapter<CartInvalidAdapter.ViewHolder> {
    private ArrayList<CartInvItemViewModel> cartVMs = new ArrayList<>();
    private onitemClicked onitemClickedCallback;
    private Context ctx;

    public CartInvalidAdapter(onitemClicked onitemClickedCallback) {
        this.onitemClickedCallback = onitemClickedCallback;
    }

    public void setCartVMs(ArrayList<CartInvItemViewModel> cartVMs) {
        this.cartVMs = cartVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartInvalidBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_cart_invalid, parent, false);
        return new CartInvalidAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(cartVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        cartVMs.get(position).getItemPic().observe((LifecycleOwner) ctx, itemPic->{
            Glide.with(ctx).load(itemPic).into(holder.ivProduct);
            Glide.with(ctx).load(itemPic).into(holder.ivHotel);
        });

        cartVMs.get(position).getId_item().observe((LifecycleOwner) ctx, idItem->{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onitemClickedCallback.onClicked(idItem, cartVMs.get(position).getType().getValue());
                }
            });
        });


    }

    @Override
    public int getItemCount() {
        return cartVMs.size();
    }

    public interface onitemClicked{
        void onClicked(String id, int tipe);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCartInvalidBinding binding;
        ImageView ivHotel, ivProduct;
        public ViewHolder(ItemCartInvalidBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivProduct = itemView.findViewById(R.id.ivCartInvProduct);
            ivHotel = itemView.findViewById(R.id.ivCartInvHotel);
        }
    }
}
