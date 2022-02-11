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
import my.istts.finalproject.databinding.ItemCommentBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.CommentItemViewModel;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<CommentItemViewModel> comments = new ArrayList<>();
    private openFullDiscussion openFullCallback;
    private Context ctx;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    public CommentAdapter(openFullDiscussion openFullCallback) {
        this.openFullCallback = openFullCallback;
    }

    public void setComments(ArrayList<CommentItemViewModel> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(comments.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullCallback.openFull(comments.get(position).getId());
            }
        });

        comments.get(position).getPengomentarPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivComment);
        });

        comments.get(position).getReplyPengomentarPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivReply);
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCommentBinding binding;
        ImageView ivComment, ivReply;
        public ViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivComment = itemView.findViewById(R.id.ivCommentSender);
            ivReply = itemView.findViewById(R.id.ivReplySender);
        }
    }

    public interface openFullDiscussion{
        void openFull(String id_komentar);
    }
}
