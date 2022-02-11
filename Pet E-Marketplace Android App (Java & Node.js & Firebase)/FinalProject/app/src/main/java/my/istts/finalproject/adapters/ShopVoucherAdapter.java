package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemShopVoucherBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.PromoItemViewModel;

import java.util.ArrayList;

public class ShopVoucherAdapter extends RecyclerView.Adapter<ShopVoucherAdapter.ViewHolder> {
    private ArrayList<PromoItemViewModel> promoVMs = new ArrayList<>();
    private detailVoucher showVoucherCallback;
    private Context ctx;

    public ShopVoucherAdapter(detailVoucher showVoucherCallback) {
        this.showVoucherCallback = showVoucherCallback;
    }

    public void setPromoVMs(ArrayList<PromoItemViewModel> promoVMs) {
        this.promoVMs = promoVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShopVoucherBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_shop_voucher, parent, false);
        return new ShopVoucherAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(promoVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVoucherCallback.showDetail(promoVMs.get(position).getPromoId());
            }
        });
    }

    public interface detailVoucher{
        void showDetail(String id_promo);
    }

    @Override
    public int getItemCount() {
        return promoVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemShopVoucherBinding binding;
        public ViewHolder(ItemShopVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
