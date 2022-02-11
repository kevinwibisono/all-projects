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
import my.istts.finalproject.databinding.ItemSearchProdroomBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelItemViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductItemViewModel;

import java.util.ArrayList;

public class SearchProdRoomAdapter extends RecyclerView.Adapter<SearchProdRoomAdapter.ViewHolder> {
    private int type;
    private ArrayList<ProductItemViewModel> productVMs = new ArrayList<>();
    private ArrayList<HotelItemViewModel> hotelVMs = new ArrayList<>();
    private onSearchItemCallback callbackSearchClicked;
    private Context ctx;

    public SearchProdRoomAdapter(int type, onSearchItemCallback callbackSearchClicked) {
        this.type = type;
        this.callbackSearchClicked = callbackSearchClicked;
    }

    public void setProductVMs(ArrayList<ProductItemViewModel> productVMs) {
        this.productVMs = productVMs;
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
        ItemSearchProdroomBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_search_prodroom, parent, false);
        return new SearchProdRoomAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(type == 0){
            holder.binding.setProductVM(productVMs.get(position));
        }
        else{
            holder.binding.setHotelVM(hotelVMs.get(position));
        }
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type == 0){
                    callbackSearchClicked.onSearchItemClick(productVMs.get(position).getId());
                }
                else{
                    callbackSearchClicked.onSearchItemClick(hotelVMs.get(position).getId());
                }
            }
        });

        if(type == 0){
            productVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
                Glide.with(ctx).load(pic).into(holder.ivProdRoom);
            });
        }
        else{
            hotelVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic ->{
                Glide.with(ctx).load(pic).into(holder.ivProdRoom);
            });
        }
    }

    public interface onSearchItemCallback{
        void onSearchItemClick(String id);
    }

    @Override
    public int getItemCount() {
        if(type == 0){
            return productVMs.size();
        }
        else{
            return hotelVMs.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemSearchProdroomBinding binding;
        ImageView ivProdRoom;
        public ViewHolder(ItemSearchProdroomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            this.ivProdRoom = itemView.findViewById(R.id.ivSearchProdRoom);
        }
    }
}
