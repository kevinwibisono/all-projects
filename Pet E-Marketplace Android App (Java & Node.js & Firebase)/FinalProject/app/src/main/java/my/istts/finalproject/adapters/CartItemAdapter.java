package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemCartBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.CartItemViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private ArrayList<CartItemViewModel> cartVMs = new ArrayList<>();
    private cartEventsListener cartEventsCallback;
    private Context ctx;

    public CartItemAdapter(cartEventsListener cartEventsCallback) {
        this.cartEventsCallback = cartEventsCallback;
    }

    public void setCartVMs(ArrayList<CartItemViewModel> cartVMs) {
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
        ItemCartBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_cart, parent, false);
        return new CartItemAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(cartVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(position == 0){
            params.setMargins(30, 30, 30, 0);
        }
        else{
            params.setMargins(30, 120, 30, 0);
        }
        holder.cardSeller.setLayoutParams(params);

        cartVMs.get(position).getItemPic().observe((LifecycleOwner) ctx, itemPic->{
            Glide.with(ctx).load(itemPic).into(holder.ivProduct);
            Glide.with(ctx).load(itemPic).into(holder.ivHotel);
            Glide.with(ctx).load(itemPic).into(holder.ivInvProduct);
            Glide.with(ctx).load(itemPic).into(holder.ivInvHotel);
        });

        cartVMs.get(position).getSellerPic().observe((LifecycleOwner) ctx, sellerPic->{
            Glide.with(ctx).load(sellerPic).into(holder.ivSeller);
        });

        holder.cardSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartEventsCallback.onSellerClick(cartVMs.get(position).getEmailSeller());
            }
        });

        for (MaterialButton btnDelete:
             holder.btnDeletes) {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartEventsCallback.onCartDeleteClick(position);
                }
            });
        }

        holder.partProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartEventsCallback.onCartItemClick(cartVMs.get(position).getIdItem(), cartVMs.get(position).getType());
            }
        });

        holder.partHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartEventsCallback.onCartItemClick(cartVMs.get(position).getIdItem(), cartVMs.get(position).getType());
            }
        });

        holder.chkSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartVMs.get(position).switchSellerChecked();
                cartEventsCallback.onCheckSeller(cartVMs.get(position));
            }
        });

        holder.chkProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartVMs.get(position).switchItemChecked();
                cartEventsCallback.onCheckChange(cartVMs.get(position));
            }
        });

        holder.chkHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartVMs.get(position).switchItemChecked();
                cartEventsCallback.onCheckChange(cartVMs.get(position));
            }
        });

        holder.chkGrooming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartVMs.get(position).switchItemChecked();
                cartEventsCallback.onCheckChange(cartVMs.get(position));
            }
        });

        cartVMs.get(position).getItemQty().observe((LifecycleOwner) ctx, qty -> {
            cartEventsCallback.onQtyChange(cartVMs.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return cartVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;
        ImageView ivSeller, ivHotel, ivProduct;
        ImageView ivInvHotel, ivInvProduct;
        MaterialButton[] btnDeletes = new MaterialButton[5];
        MaterialCardView cardSeller;
        LinearLayout partProduct, partHotel;
        CheckBox chkSeller, chkHotel, chkProduct, chkGrooming;
        public ViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivSeller = itemView.findViewById(R.id.ivCartSeller);
            btnDeletes[0] = itemView.findViewById(R.id.btnDeleteCartProduct);
            btnDeletes[1] = itemView.findViewById(R.id.btnDeleteCartHotel);
            btnDeletes[2] = itemView.findViewById(R.id.btnDeleteCartGrooming);
            btnDeletes[3] = itemView.findViewById(R.id.btnDeleteCartInvProduct);
            btnDeletes[4] = itemView.findViewById(R.id.btnDeleteCartInvHotel);
            ivProduct = itemView.findViewById(R.id.ivCartProduct);
            ivHotel = itemView.findViewById(R.id.ivCartHotel);
            ivInvProduct = itemView.findViewById(R.id.ivCartInvProduct);
            ivInvHotel = itemView.findViewById(R.id.ivCartInvHotel);
            cardSeller = itemView.findViewById(R.id.cardCartSeller);
            partProduct = itemView.findViewById(R.id.partCartProduct);
            partHotel = itemView.findViewById(R.id.partCartHotel);
            chkSeller = itemView.findViewById(R.id.chkBoxCartSeller);
            chkHotel = itemView.findViewById(R.id.chkBoxCartHotel);
            chkProduct = itemView.findViewById(R.id.chkBoxCartProduct);
            chkGrooming = itemView.findViewById(R.id.chkBoxCartGrooming);
        }
    }

    public interface cartEventsListener{
        void onCartDeleteClick(int position);
        void onCartItemClick(String id_item, int tipe);
        void onSellerClick(String seller);
        void onCheckSeller(CartItemViewModel cartVM);
        void onCheckChange(CartItemViewModel cartVM);
        void onQtyChange(CartItemViewModel cartVM);
    }


}
