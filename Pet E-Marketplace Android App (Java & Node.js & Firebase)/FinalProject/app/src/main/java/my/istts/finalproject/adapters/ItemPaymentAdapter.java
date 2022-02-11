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
import my.istts.finalproject.databinding.ItemPaymentItemBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.ItemPaymentViewModel;

import java.util.ArrayList;

public class ItemPaymentAdapter extends RecyclerView.Adapter<ItemPaymentAdapter.ViewHolder> {
    private ArrayList<ItemPaymentViewModel> itemVMs = new ArrayList<>();
    private onClickProduct onClickProductCallback;
    private onClickSeller onClickSellerCallback;
    private Context ctx;

    public ItemPaymentAdapter(onClickProduct onClickProductCallback, onClickSeller onClickSellerCallback) {
        this.onClickProductCallback = onClickProductCallback;
        this.onClickSellerCallback = onClickSellerCallback;
    }

    public void setItemVMs(ArrayList<ItemPaymentViewModel> itemVMs) {
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
        ItemPaymentItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_payment_item, parent, false);
        return new ItemPaymentAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(itemVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.partSeller.getLayoutParams();
        if(position == 0){
            params.setMargins(30, 30, 30, 0);
        }
        else{
            params.setMargins(30, 120, 30, 0);
        }
        holder.partSeller.requestLayout();

        itemVMs.get(position).getSellerPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivSeller);
        });

        itemVMs.get(position).getPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivProduct);
            Glide.with(ctx).load(picture).into(holder.ivHotel);
        });

        itemVMs.get(position).getKurir().observe((LifecycleOwner) ctx, kurir->{
            int logo = getKurirLogo(kurir);
            if(logo > -1){
                holder.logoOngkir.setImageResource(logo);
            }
        });

        holder.partProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickProductCallback.onClicked(itemVMs.get(position).getItemId());
            }
        });

        holder.partSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSellerCallback.onClicked(itemVMs.get(position).getSeller());
            }
        });

    }

    public int getKurirLogo(String kurir){
        String[] kurirNames = {"pickup", "jne", "pos", "tiki"};
        int matchLogo = -1;
        int[] kurirDrawable = {R.drawable.self_pickup, R.drawable.logo_jne, R.drawable.logo_pos, R.drawable.logo_tiki};
        for (int i = 0; i < 4; i++) {
            if(kurir.toLowerCase().contains(kurirNames[i])){
                matchLogo = kurirDrawable[i];
            }
        }
        return matchLogo;
    }

    public interface onClickProduct{
        void onClicked(String id_produk);
    }

    public interface onClickSeller{
        void onClicked(String seller);
    }

    @Override
    public int getItemCount() {
        return itemVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPaymentItemBinding binding;
        ImageView ivProduct, ivHotel, ivSeller, logoOngkir;
        LinearLayout partSeller, partProduct;
        public ViewHolder(ItemPaymentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivSeller = itemView.findViewById(R.id.ivPaymentItemSeller);
            ivProduct = itemView.findViewById(R.id.ivPaymentItemPicture);
            ivHotel = itemView.findViewById(R.id.ivHotelPayment);
            logoOngkir = itemView.findViewById(R.id.logoPaymentOngkir);
            partSeller = itemView.findViewById(R.id.partSellerPayment);
            partProduct = itemView.findViewById(R.id.partItemPaymentProduct);
        }
    }
}
