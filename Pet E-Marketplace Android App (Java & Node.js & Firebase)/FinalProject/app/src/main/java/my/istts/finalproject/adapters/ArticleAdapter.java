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
import my.istts.finalproject.databinding.ItemArticleBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.ArticleItemViewModel;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private ArrayList<ArticleItemViewModel> articlesVMs = new ArrayList<>();
    private articleClickCallback callback;
    private Context ctx;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    public ArticleAdapter(articleClickCallback callback) {
        this.callback = callback;
    }

    public void setArticlesVMs(ArrayList<ArticleItemViewModel> articlesVMs) {
        this.articlesVMs = articlesVMs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArticleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_article, parent, false);
        return new ArticleAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setViewmodel(articlesVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClick(articlesVMs.get(position).getId());
            }
        });

        Glide.with(ctx).load(articlesVMs.get(position).getPicture()).into(holder.ivArticle);
    }

    @Override
    public int getItemCount() {
        return articlesVMs.size();
    }

    public interface articleClickCallback{
        void onClick(String id_artikel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemArticleBinding binding;
        ImageView ivArticle;
        public ViewHolder(ItemArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivArticle = itemView.findViewById(R.id.ivArticles);
        }
    }
}
