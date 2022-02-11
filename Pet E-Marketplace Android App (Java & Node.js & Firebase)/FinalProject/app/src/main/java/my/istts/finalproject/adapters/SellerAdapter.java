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
import my.istts.finalproject.databinding.ItemFollowSellerBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.SellerItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.ViewHolder> {
    private ArrayList<SellerItemViewModel> sellerVMs = new ArrayList<>();
    private sellerItemInterface sellerInterfaceCallback;
    private Context ctx;

    public SellerAdapter(sellerItemInterface sellerInterfaceCallback) {
        this.sellerInterfaceCallback = sellerInterfaceCallback;
    }

    public void setSellerVMs(ArrayList<SellerItemViewModel> sellerVMs) {
        this.sellerVMs = sellerVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFollowSellerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_follow_seller, parent, false);
        return new SellerAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);
        holder.binding.setViewmodel(sellerVMs.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellerInterfaceCallback.onSellerClicked(sellerVMs.get(position).getSellerEmail());
            }
        });

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellerInterfaceCallback.onSellerFavClicked(sellerVMs.get(position).getSellerEmail());
            }
        });

        sellerVMs.get(position).getSellerPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivSeller);
        });

        sellerVMs.get(position).getSellerScore().observe((LifecycleOwner) ctx, skor->{
            if(!skor.equals("")){
                for (int i = 0; i < 5; i++) {
                    //karena index dimulai 0, sedangkan skor dimulai dari 1, maka saat membanginkan tambah index(i) dgn 1
                    if((i+1) <= skor){
                        holder.ivStars[i].setImageResource(R.drawable.ic_baseline_star_20);
                    }
                    else{
                        float diff = skor - (i);
                        holder.ivStars[i].setImageResource(determineStar(diff));
                    }
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

    @Override
    public int getItemCount() {
        return sellerVMs.size();
    }

    public interface sellerItemInterface{
        void onSellerClicked(String hp_seller);
        void onSellerFavClicked(String hp_seller);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemFollowSellerBinding binding;
        ImageView ivSeller;
        MaterialButton btnFav;
        ImageView[] ivStars = new ImageView[5];
        public ViewHolder(ItemFollowSellerBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            this.binding = binding;
            ivSeller = itemView.findViewById(R.id.ivSellerPic);
            btnFav = itemView.findViewById(R.id.btnFavoriteSeller);
            ivStars[0] = itemView.findViewById(R.id.starSeller1);
            ivStars[1] = itemView.findViewById(R.id.starSeller2);
            ivStars[2] = itemView.findViewById(R.id.starSeller3);
            ivStars[3] = itemView.findViewById(R.id.starSeller4);
            ivStars[4] = itemView.findViewById(R.id.starSeller5);
        }
    }
}
