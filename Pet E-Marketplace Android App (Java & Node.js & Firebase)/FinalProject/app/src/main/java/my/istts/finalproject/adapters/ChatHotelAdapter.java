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
import my.istts.finalproject.databinding.ItemHotelchatBinding;

import my.istts.finalproject.interfaces.chatChooseHotel;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;

import java.util.ArrayList;

public class ChatHotelAdapter extends RecyclerView.Adapter<ChatHotelAdapter.ViewHolder> {
    private ArrayList<HotelItemViewModel> hotelVMs = new ArrayList<>();
    private chatChooseHotel chooseItemCallback;
    private Context ctx;

    public ChatHotelAdapter(chatChooseHotel chooseItemCallback, Context ctx) {
        this.chooseItemCallback = chooseItemCallback;
        this.ctx = ctx;
    }

    public void setHotelVMs(ArrayList<HotelItemViewModel> hotelVMs) {
        this.hotelVMs = hotelVMs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHotelchatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_hotelchat, parent, false);
        return new ChatHotelAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);
        holder.binding.setViewmodel(hotelVMs.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseItemCallback.chooseHotel(hotelVMs.get(position).getId());
            }
        });

        hotelVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic -> {
            Glide.with(ctx).load(pic).into(holder.ivHotel);
        });

    }

    @Override
    public int getItemCount() {
        return hotelVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHotelchatBinding binding;
        ImageView ivHotel;
        public ViewHolder(ItemHotelchatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivHotel = itemView.findViewById(R.id.ivChatHotelPic);
        }

    }
}
