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
import my.istts.finalproject.databinding.ItemConversationBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.ConversationItemViewModel;

import java.util.ArrayList;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    private ArrayList<ConversationItemViewModel> convVM = new ArrayList<>();
    private openConversation convOpen;
    private Context ctx;

    public void setConvVM(ArrayList<ConversationItemViewModel> convVM) {
        this.convVM = convVM;
    }

    public ConversationsAdapter(openConversation convOpen) {
        this.convOpen = convOpen;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConversationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_conversation, parent, false);
        return new ConversationsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(convVM.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        convVM.get(position).getPicture().observe((LifecycleOwner) ctx, picture -> {
            Glide.with(ctx).load(picture).into(holder.ivFriend);
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convOpen.onOpen(convVM.get(position).getID());
            }
        });
    }

    public interface openConversation{
        void onOpen(String id);
    }

    @Override
    public int getItemCount() {
        return convVM.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemConversationBinding binding;
        ImageView ivFriend;
        public ViewHolder(ItemConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivFriend = itemView.findViewById(R.id.convItemPicture);
        }
    }
}
