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
import my.istts.finalproject.databinding.ItemDiscussBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.DiscussItemViewModel;

import java.util.ArrayList;

public class DiscussListAdapter extends RecyclerView.Adapter<DiscussListAdapter.ViewHolder> {
    private ArrayList<DiscussItemViewModel> discuss = new ArrayList<>();
    private toDiscussPage callback;
    private Context ctx;

    public DiscussListAdapter(toDiscussPage callback) {
        this.callback = callback;
    }

    public void setDiscuss(ArrayList<DiscussItemViewModel> discuss) {
        this.discuss = discuss;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiscussBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_discuss, parent, false);
        return new DiscussListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setViewmodel(discuss.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showPage(discuss.get(position).getId());
            }
        });

        discuss.get(position).getPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivDiscuss);
        });
    }

    public interface toDiscussPage{
        void showPage(String id_diskusi);
    }

    @Override
    public int getItemCount() {
        return discuss.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDiscussBinding binding;
        ImageView ivDiscuss;
        public ViewHolder(ItemDiscussBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            this.binding = binding;
            ivDiscuss = itemView.findViewById(R.id.imageDiscuss);
        }
    }
}
