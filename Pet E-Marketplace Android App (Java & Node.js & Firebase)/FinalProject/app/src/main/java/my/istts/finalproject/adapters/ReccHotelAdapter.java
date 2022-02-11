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
import my.istts.finalproject.databinding.ItemReccommendRoomBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;

import java.util.ArrayList;

public class ReccHotelAdapter extends RecyclerView.Adapter<ReccHotelAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<HotelItemViewModel> hotelVMs = new ArrayList<>();
	private hotelClickCallback callback;

    public ReccHotelAdapter(hotelClickCallback callback) {
		this.callback = callback;
    }

    public void setHotelVMs(ArrayList<HotelItemViewModel> hotelVMs) {
        this.hotelVMs = hotelVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReccommendRoomBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_reccommend_room, parent, false);
        return new ReccHotelAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(hotelVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onHotelClick(hotelVMs.get(position).getId());
            }
        });

		hotelVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
            Glide.with(ctx).load(pic).into(holder.picRoom);
        });

        hotelVMs.get(position).getOwnerPic().observe((LifecycleOwner) ctx, pic ->{
            Glide.with(ctx).load(pic).into(holder.picOwner);
        });
    }

	public interface hotelClickCallback{
		void onHotelClick(String id_kamar);
	}

    @Override
    public int getItemCount() {
        return hotelVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReccommendRoomBinding binding;
        ImageView picOwner, picRoom;
        public ViewHolder(ItemReccommendRoomBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            this.binding = binding;
            picOwner = itemView.findViewById(R.id.ivReccRoomOwner);
            picRoom = itemView.findViewById(R.id.ivReccRoom);
        }
    }
}
