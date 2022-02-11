package my.istts.finalproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemChatBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.ChatItemViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<ChatItemViewModel> chatVM = new ArrayList<>();
    private String current_hp;
    private Context ctx;
    private onShowImage onShowImageCallback;
    private onShowProduct onShowProductCallback;
    private onShowHotel onShowHotelCallback;
    private onShowAdopt onShowAdoptCallback;
    private onShowOrder onShowOrderCallback;

    public ChatAdapter(onShowImage onShowImageCallback, onShowProduct onShowProductCallback, onShowHotel onShowHotelCallback, onShowOrder onShowOrderCallback, onShowAdopt onShowAdoptCallback) {
        this.onShowImageCallback = onShowImageCallback;
        this.onShowProductCallback = onShowProductCallback;
        this.onShowHotelCallback = onShowHotelCallback;
        this.onShowOrderCallback = onShowOrderCallback;
        this.onShowAdoptCallback = onShowAdoptCallback;
    }

    public void setChatVM(ArrayList<ChatItemViewModel> chatVM) {
        this.chatVM = chatVM;
    }

    public void addChatVM(ChatItemViewModel chatVM){
        this.chatVM.add(chatVM);
    }

    public void setCurrent_hp(String current_hp) {
        this.current_hp = current_hp;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat, parent, false);
        return new ChatAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);
        holder.binding.setViewmodel(chatVM.get(position));

        if(chatVM.get(position).getReceiver().equals(this.current_hp)){
            //chat yang diterima oleh orang yang melihat halaman ini
            //taruh di sebelah kiri dgn background putih
            //3px = 1dp
            //30 = 10dp
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.START;
            params.setMargins(30, 0, 60, 45);
            holder.cardChat.setCardBackgroundColor(Color.WHITE);
            holder.cardChat.setLayoutParams(params);

            for (int i = 0; i < holder.cards.length; i++) {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.START;
                params.setMargins(30, 0, 60, 9);
                holder.cards[i].setStrokeColor(Color.WHITE);
                holder.cards[i].setLayoutParams(params);
            }
        }
        else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.END;
            params.setMargins(60, 0, 30, 45);
            holder.cardChat.setCardBackgroundColor(Color.rgb(139, 195, 74));
            holder.cardChat.setLayoutParams(params);

            for (int i = 0; i < holder.cards.length; i++) {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.END;
                params.setMargins(60, 0, 30, 9);
                holder.cards[i].setStrokeColor(Color.rgb(139, 195, 74));
                holder.cards[i].setLayoutParams(params);
            }
        }

        holder.ivChatPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowImageCallback.showImage(chatVM.get(position).getChatPic().getValue());
            }
        });

        holder.partProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowProductCallback.showProduct(chatVM.get(position).getIdItem());
            }
        });

        holder.partHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowHotelCallback.showHotel(chatVM.get(position).getIdItem());
            }
        });

        holder.partAdopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowAdoptCallback.showPetAdopt(chatVM.get(position).getIdItem());
            }
        });

        holder.cards[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowOrderCallback.showOrder(chatVM.get(position).getIdPJ(), chatVM.get(position).getOrderType().getValue());
            }
        });

        chatVM.get(position).getChatPic().observe((LifecycleOwner) ctx, pic -> {
            Glide.with(ctx).load(pic).into(holder.ivChatPic);
        });

        chatVM.get(position).getProductPic().observe((LifecycleOwner) ctx, picture ->{
            Glide.with(ctx).load(picture).into(holder.ivChatProdPic);
        });

        chatVM.get(position).getHotelPic().observe((LifecycleOwner) ctx, picture ->{
            Glide.with(ctx).load(picture).into(holder.ivChatHotelPic);
        });

        chatVM.get(position).getAdoptPic().observe((LifecycleOwner) ctx, picture ->{
            Glide.with(ctx).load(picture).into(holder.ivChatAdoptPic);
        });

        chatVM.get(position).getOrderItemPic().observe((LifecycleOwner) ctx, picture ->{
            Glide.with(ctx).load(picture).into(holder.ivChatOrderPic);
        });
    }

    @Override
    public int getItemCount() {
        return chatVM.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemChatBinding binding;
        MaterialCardView cardChat;
        MaterialCardView[] cards = new MaterialCardView[3];
        ImageView ivChatPic, ivChatProdPic, ivChatHotelPic, ivChatOrderPic, ivChatAdoptPic;
        LinearLayout partProduct, partHotel, partAdopt;
        public ViewHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            cardChat = itemView.findViewById(R.id.cardChat);
            cards[0] = itemView.findViewById(R.id.cardChatOrder);
            cards[1] = itemView.findViewById(R.id.cardChatProd);
            cards[2] = itemView.findViewById(R.id.cardChatPic);
            ivChatPic = itemView.findViewById(R.id.chatPicture);
            ivChatProdPic = itemView.findViewById(R.id.chatProductPic);
            ivChatHotelPic = itemView.findViewById(R.id.chatHotelPic);
            ivChatOrderPic = itemView.findViewById(R.id.chatOrderPic);
            ivChatAdoptPic = itemView.findViewById(R.id.chatAdoptPic);
            partProduct = itemView.findViewById(R.id.partChatProduct);
            partHotel = itemView.findViewById(R.id.partChatHotel);
            partAdopt = itemView.findViewById(R.id.partChatPet);
        }

    }

    public interface onShowImage{
        void showImage(String url);
    }

    public interface onShowProduct{
        void showProduct(String id);
    }

    public interface onShowHotel{
        void showHotel(String id);
    }

    public interface onShowAdopt{
        void showPetAdopt(String id);
    }

    public interface onShowOrder{
        void showOrder(String id, int jenis);
    }
}
