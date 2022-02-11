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
import my.istts.finalproject.databinding.ItemProductchatBinding;
import my.istts.finalproject.interfaces.chatChooseProduct;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;

import java.util.ArrayList;

public class ChatProductAdapter extends RecyclerView.Adapter<ChatProductAdapter.ViewHolder> {
    private ArrayList<ProductItemViewModel> prodVMs = new ArrayList<>();
    private chatChooseProduct chooseItemCallback;
    private Context ctx;

    public ChatProductAdapter(chatChooseProduct chooseItemCallback, Context ctx) {
        this.chooseItemCallback = chooseItemCallback;
        this.ctx = ctx;
    }

    public void setProdVMs(ArrayList<ProductItemViewModel> prodVMs) {
        this.prodVMs = prodVMs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductchatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_productchat, parent, false);
        return new ChatProductAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);
        holder.binding.setViewmodel(prodVMs.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseItemCallback.chooseProduct(prodVMs.get(position).getId());
            }
        });

        prodVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic -> {
            Glide.with(ctx).load(pic).into(holder.ivProduct);
        });

    }

    @Override
    public int getItemCount() {
        return prodVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductchatBinding binding;
        ImageView ivProduct;
        public ViewHolder(ItemProductchatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivProduct = itemView.findViewById(R.id.ivChatProductPic);
        }

    }
}
