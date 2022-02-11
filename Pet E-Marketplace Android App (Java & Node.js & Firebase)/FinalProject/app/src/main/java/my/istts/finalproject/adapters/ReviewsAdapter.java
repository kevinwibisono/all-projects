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
import my.istts.finalproject.databinding.ItemReviewBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.ReviewItemViewModel;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private ArrayList<ReviewItemViewModel> revVMs = new ArrayList<>();
    private Context ctx;

    public void setRevVMs(ArrayList<ReviewItemViewModel> revVMs) {
        this.revVMs = revVMs;
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
        holder.binding.setViewmodel(revVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        revVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
            Glide.with(ctx).load(pic).into(holder.ivGiver);
        });

        int numberScore = revVMs.get(position).getSkor();
        for (int i = 0; i < 5; i++) {
            //karena index dimulai 0, sedangkan skor dimulai dari 1, maka saat membanginkan tambah index(i) dgn 1
            if((i+1) <= numberScore){
                holder.ivStars[i].setImageResource(R.drawable.ic_baseline_star_20);
            }
            else{
                holder.ivStars[i].setImageResource(R.drawable.ic_baseline_star_border_20);
            }
        }
    }

    @Override
    public int getItemCount() {
        return revVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReviewBinding binding;
        ImageView ivGiver;
        ImageView[] ivStars = new ImageView[5];
        public ViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivGiver = itemView.findViewById(R.id.ivReviewGiver);
            ivStars[0] = itemView.findViewById(R.id.reviewStar1);
            ivStars[1] = itemView.findViewById(R.id.reviewStar2);
            ivStars[2] = itemView.findViewById(R.id.reviewStar3);
            ivStars[3] = itemView.findViewById(R.id.reviewStar4);
            ivStars[4] = itemView.findViewById(R.id.reviewStar5);
        }
    }
}
