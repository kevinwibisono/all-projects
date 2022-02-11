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
import my.istts.finalproject.databinding.ItemComplainBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.ComplainItemViewModel;

import java.util.ArrayList;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ViewHolder> {
    private ArrayList<ComplainItemViewModel> complains = new ArrayList<>();
    private onComplainClicked onComplainClickedCallback;
    private Context ctx;

    public ComplainAdapter(onComplainClicked onComplainClickedCallback) {
        this.onComplainClickedCallback = onComplainClickedCallback;
    }

    public void setComplains(ArrayList<ComplainItemViewModel> complains) {
        this.complains = complains;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemComplainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_complain, parent, false);
        return new ComplainAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(complains.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComplainClickedCallback.complainClicked(complains.get(position).getId());
            }
        });

        complains.get(position).getPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivComplain);
        });
    }

    public interface onComplainClicked{
        void complainClicked(String id_komplain);
    }

    @Override
    public int getItemCount() {
        return complains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemComplainBinding binding;
        ImageView ivComplain;
        public ViewHolder(ItemComplainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivComplain = itemView.findViewById(R.id.ivComplainItems);
        }
    }
}
