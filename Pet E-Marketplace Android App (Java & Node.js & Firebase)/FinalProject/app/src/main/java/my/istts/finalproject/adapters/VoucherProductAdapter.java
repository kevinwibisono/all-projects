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
import my.istts.finalproject.databinding.ItemProductVoucherBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;

import java.util.ArrayList;

public class VoucherProductAdapter extends RecyclerView.Adapter<VoucherProductAdapter.ViewHolder> {
    private ArrayList<ProductItemViewModel> products = new ArrayList<>();
    private toProductPage productPageCallback;
    private Context ctx;

    public VoucherProductAdapter(toProductPage productPageCallback) {
        this.productPageCallback = productPageCallback;
    }

    public void setProducts(ArrayList<ProductItemViewModel> products) {
        this.products = products;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductVoucherBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_product_voucher, parent, false);
        return new VoucherProductAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(products.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        products.get(position).getPicture().observe((LifecycleOwner) ctx, pic->{
            Glide.with(ctx).load(pic).into(holder.ivProduct);
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productPageCallback.toPage(products.get(position).getId());
            }
        });
    }

    public interface toProductPage{
        void toPage(String id_produk);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductVoucherBinding binding;
        ImageView ivProduct;
        public ViewHolder(ItemProductVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivProduct = itemView.findViewById(R.id.ivVoucherProd);
        }
    }
}
