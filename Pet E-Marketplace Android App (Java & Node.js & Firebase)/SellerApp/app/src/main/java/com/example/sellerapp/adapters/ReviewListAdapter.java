package com.example.sellerapp.adapters;

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
import com.example.sellerapp.R;
import com.example.sellerapp.databinding.ItemChatBinding;
import com.example.sellerapp.databinding.ItemListreviewBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.ReviewItemViewModel;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {
    private ArrayList<ReviewItemViewModel> revVMs = new ArrayList<>();
    private Context ctx;

    public void setRevVMs(ArrayList<ReviewItemViewModel> revs){
        revVMs = revs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListreviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_listreview, parent, false);
        return new ReviewListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(revVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        revVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
            if(!pic.equals("")){
                Glide.with(ctx).load(pic).into(holder.ivPersonPic);
            }
        });

        for (int i = 1; i < 6; i++) {
            if(i <= revVMs.get(position).getSkor()){
                holder.stars[i-1].setImageResource(R.drawable.ic_baseline_star_20);
            }
            else{
                float diff = revVMs.get(position).getSkor() - (i);
                holder.stars[i-1].setImageResource(determineStar(diff));
            }
        }
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
        return revVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListreviewBinding binding;
        ImageView[] stars;
        ImageView ivPersonPic;
        public ViewHolder(ItemListreviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            stars = new ImageView[]{itemView.findViewById(R.id.reviewListStar1), itemView.findViewById(R.id.reviewListStar2), itemView.findViewById(R.id.reviewListStar3), itemView.findViewById(R.id.reviewListStar4), itemView.findViewById(R.id.reviewListStar5)};
            ivPersonPic = itemView.findViewById(R.id.ivReviewGiver);
        }

    }
}
