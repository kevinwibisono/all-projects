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
import com.example.sellerapp.databinding.ItemReviewBinding;
import com.example.sellerapp.viewmodels.itemviewmodels.ReviewItemViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private ArrayList<ReviewItemViewModel> reviews = new ArrayList<>();
    private addReviewReturn addReviewReturnCallback;
    private Context ctx;

    public ReviewsAdapter(addReviewReturn addReviewReturnCallback) {
        this.addReviewReturnCallback = addReviewReturnCallback;
    }

    public void setReviews(ArrayList<ReviewItemViewModel> reviews) {
        this.reviews = reviews;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_review, parent, false);
        return new ReviewsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(reviews.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReviewReturnCallback.onReturn(reviews.get(position).getId(), holder.edReturn.getText().toString());
                holder.edReturn.setText("");
            }
        });

        reviews.get(position).getPicture().observe((LifecycleOwner) ctx, pic->{
            Glide.with(ctx).load(pic).into(holder.reviewGiver);
        });

        for (int i = 1; i < 6; i++) {
            if(i <= reviews.get(position).getSkor()){
                holder.stars[i-1].setImageResource(R.drawable.ic_baseline_star_20);
            }
            else{
                float diff = reviews.get(position).getSkor() - (i);
                if(diff > 0){
                    holder.stars[i-1].setImageResource(R.drawable.ic_baseline_star_half_20);
                }
                else{
                    holder.stars[i-1].setImageResource(R.drawable.ic_baseline_star_border_20);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReviewBinding binding;
        MaterialButton btnReturn;
        ImageView reviewGiver;
        ImageView[] stars;
        TextInputEditText edReturn;
        public ViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            this.binding = binding;
            reviewGiver = itemView.findViewById(R.id.ivReviewListGiver);
            stars = new ImageView[]{itemView.findViewById(R.id.starReview1), itemView.findViewById(R.id.starReview2), itemView.findViewById(R.id.starReview3), itemView.findViewById(R.id.starReview4), itemView.findViewById(R.id.starReview5)};
            btnReturn = itemView.findViewById(R.id.btnReviewReturn);
            edReturn = itemView.findViewById(R.id.edReviewBalasan);
        }
    }

    public interface addReviewReturn{
        void onReturn(String id, String balasan);
    }
}
