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
import my.istts.finalproject.databinding.ItemReccomendProductBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ReccProductsAdapter extends RecyclerView.Adapter<ReccProductsAdapter.ViewHolder> {
    ArrayList<ProductItemViewModel> productVMs = new ArrayList<>();
    onProductClickCallback callbackProduct;
    onProductFavoritedChanged callbackFavorite;
    Context ctx;

    public ReccProductsAdapter(onProductClickCallback callbackProduct) {
        this.callbackProduct = callbackProduct;
    }

    public void setCallbackFavorite(onProductFavoritedChanged callbackFavorite) {
        this.callbackFavorite = callbackFavorite;
    }

    public void setProductVMs(ArrayList<ProductItemViewModel> productVMs) {
        this.productVMs = productVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReccomendProductBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_reccomend_product, parent, false);
        return new ReccProductsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setViewmodel(productVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackProduct.onProductClick(productVMs.get(position).getId());
            }
        });

        productVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
            Glide.with(ctx).load(pic).into(holder.ivReccProduct);
        });

        productVMs.get(position).getSkor().observe((LifecycleOwner) ctx, score -> {
            float numberScore = Float.parseFloat(score);
            for (int i = 0; i < 5; i++) {
                //karena index dimulai 0, sedangkan skor dimulai dari 1, maka saat membanginkan tambah index(i) dgn 1
                if((i+1) <= numberScore){
                    holder.ivStars[i].setImageResource(R.drawable.ic_baseline_star_20);
                }
                else{
                    float diff = numberScore - (i);
                    holder.ivStars[i].setImageResource(determineStar(diff));
                }
            }
        });

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productVMs.get(position).setFavorite();
                if(callbackFavorite != null){
                    callbackFavorite.onFavoriteChanged();
                }
            }
        });

    }

    private int determineStar(float diff){
        if(diff < 0.2){
            return R.drawable.ic_baseline_star_border_20;
        }
        else if(diff > 0.1 && diff < 0.9){
            return R.drawable.ic_baseline_star_half_20;
        }
        else{
            return R.drawable.ic_baseline_star_20;
        }
    }

    public interface onProductClickCallback{
        void onProductClick(String id);
    }

    public interface onProductFavoritedChanged{
        void onFavoriteChanged();
    }

    @Override
    public int getItemCount() {
        return productVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReccomendProductBinding binding;
        ImageView ivReccProduct;
        ImageView[] ivStars = new ImageView[5];
        MaterialButton btnFavorite;
        public ViewHolder(ItemReccomendProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            this.ivReccProduct = itemView.findViewById(R.id.ivReccProduct);
            ivStars[0] = itemView.findViewById(R.id.reccProdStar1);
            ivStars[1] = itemView.findViewById(R.id.reccProdStar2);
            ivStars[2] = itemView.findViewById(R.id.reccProdStar3);
            ivStars[3] = itemView.findViewById(R.id.reccProdStar4);
            ivStars[4] = itemView.findViewById(R.id.reccProdStar5);
            btnFavorite = itemView.findViewById(R.id.btnFavoriteProductItem);
        }
    }
}
