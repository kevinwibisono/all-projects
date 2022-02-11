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
import my.istts.finalproject.databinding.ItemGiveReviewBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.GiveReviewItemViewModel;

import java.util.ArrayList;

public class GiveReviewAdapter extends RecyclerView.Adapter<GiveReviewAdapter.ViewHolder> {
    private ArrayList<GiveReviewItemViewModel> revVMs = new ArrayList<>();
	private Context ctx;

    public void setRevVMs(ArrayList<GiveReviewItemViewModel> revVMs) {
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
        ItemGiveReviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_give_review, parent, false);
        return new GiveReviewAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setViewmodel(revVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        revVMs.get(position).getItemPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivItem);
        });
    }

    @Override
    public int getItemCount() {
        return revVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemGiveReviewBinding binding;
        ImageView ivItem;
        public ViewHolder(ItemGiveReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivItem = itemView.findViewById(R.id.ivReviewItem);

        }
    }
}
