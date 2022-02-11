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
import my.istts.finalproject.databinding.ItemOrderchatBinding;
import my.istts.finalproject.interfaces.chatChooseOrder;
import my.istts.finalproject.viewmodels.itemviewmodels.PesananItemViewModel;

import java.util.ArrayList;

public class ChatOrderAdapter extends RecyclerView.Adapter<ChatOrderAdapter.ViewHolder> {
    private ArrayList<PesananItemViewModel> orderVMs = new ArrayList<>();
    private chatChooseOrder chooseOrderCallback;
    private Context ctx;

    public ChatOrderAdapter(chatChooseOrder chooseOrderCallback, Context ctx) {
        this.chooseOrderCallback = chooseOrderCallback;
        this.ctx = ctx;
    }

    public void setOrderVMs(ArrayList<PesananItemViewModel> orderVMs) {
        this.orderVMs = orderVMs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderchatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_orderchat, parent, false);
        return new ChatOrderAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(orderVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseOrderCallback.chooseOrder(orderVMs.get(position).getId());
            }
        });

        orderVMs.get(position).getItemPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivChatOrder);
        });
    }

    @Override
    public int getItemCount() {
        return orderVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderchatBinding binding;
        ImageView ivChatOrder;
        public ViewHolder(ItemOrderchatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivChatOrder = itemView.findViewById(R.id.ivChatOrder);
        }
    }
}
