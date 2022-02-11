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
import my.istts.finalproject.databinding.ItemListHotelBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class HotelListAdapter extends RecyclerView.Adapter<HotelListAdapter.ViewHolder> {
    private ArrayList<HotelItemViewModel> hotelVMs = new ArrayList<>();
	private hotelClickCallback callback;
	private hotelFavoriteChanged favoriteCallback;
	private Context ctx;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    public HotelListAdapter(hotelClickCallback callback) {
		this.callback = callback;
    }

    public void setHotelVMs(ArrayList<HotelItemViewModel> hotelVMs) {
        this.hotelVMs = hotelVMs;
    }

    public void setFavoriteCallback(hotelFavoriteChanged favoriteCallback) {
        this.favoriteCallback = favoriteCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListHotelBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_list_hotel, parent, false);
        return new HotelListAdapter.ViewHolder(binding);
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
            Glide.with(ctx).load(pic).into(holder.ivHotel);
        });

        hotelVMs.get(position).getSkor().observe((LifecycleOwner) ctx, score -> {
            float numberScore = Float.parseFloat(score);
            for (int i = 0; i < 5; i++) {
                //karena index dimulai 0, sedangkan skor dimulai dari 1, maka saat membanginkan tambah index(i) dgn 1
                if((i+1) <= numberScore){
                    holder.ivStars[i].setImageResource(R.drawable.ic_baseline_star_20);
                }
                else{
                    float diff = numberScore - (i);
                    holder.ivStars[i].setImageResource(determineStar(diff));
                }
            }
        });

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hotelVMs.get(position).setFavorite();
                if(favoriteCallback != null){
                    favoriteCallback.onFavoriteChanged();
                }
            }
        });
    }

    private int determineStar(float diff){
        if(diff < 0.2){
            return R.drawable.ic_baseline_star_border_20;
        }
        else if(diff > 0.1 && diff < 0.9){
            return R.drawable.ic_baseline_star_half_20;
        }
        else{
            return R.drawable.ic_baseline_star_20;
        }
    }

	public interface hotelClickCallback{
		void onHotelClick(String id_hotel);
	}

    public interface hotelFavoriteChanged{
        void onFavoriteChanged();
    }

    @Override
    public int getItemCount() {
        return hotelVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListHotelBinding binding;
        ImageView ivHotel;
        ImageView[] ivStars = new ImageView[5];
        MaterialButton btnFavorite;
        public ViewHolder(ItemListHotelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivHotel = itemView.findViewById(R.id.ivHotelListPic);
            ivStars[0] = itemView.findViewById(R.id.starHotelList1);
            ivStars[1] = itemView.findViewById(R.id.starHotelList2);
            ivStars[2] = itemView.findViewById(R.id.starHotelList3);
            ivStars[3] = itemView.findViewById(R.id.starHotelList4);
            ivStars[4] = itemView.findViewById(R.id.starHotelList5);
            btnFavorite = itemView.findViewById(R.id.btnFavoriteHotelItem);
        }
    }
}
