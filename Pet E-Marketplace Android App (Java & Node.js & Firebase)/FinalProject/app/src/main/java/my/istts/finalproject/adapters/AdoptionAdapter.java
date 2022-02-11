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
import my.istts.finalproject.databinding.ItemPetAdoptBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.AdoptionItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AdoptionAdapter extends RecyclerView.Adapter<AdoptionAdapter.ViewHolder> {
    private ArrayList<AdoptionItemViewModel> adoptsVMs = new ArrayList<>();
	private petClickCallback callback;
	private petFavoritedChanged favoriteCallback;
	private Context ctx;

    public void setAdoptsVMs(ArrayList<AdoptionItemViewModel> adoptsVMs) {
        this.adoptsVMs = adoptsVMs;
    }

    public AdoptionAdapter(petClickCallback callback) {
		this.callback = callback;
    }

    public void setFavoriteCallback(petFavoritedChanged favoriteCallback) {
        this.favoriteCallback = favoriteCallback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPetAdoptBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_pet_adopt, parent, false);
        return new AdoptionAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setViewmodel(adoptsVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPetClick(adoptsVMs.get(position).getId());
            }
        });

		adoptsVMs.get(position).getPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivPet);
        });

		holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adoptsVMs.get(position).setFavorite();
                if(favoriteCallback != null){
                    favoriteCallback.onFavoritedChanged();
                }
            }
        });
    }
	
	public interface petClickCallback{
		void onPetClick(String id_pet);
	}

    public interface petFavoritedChanged{
        void onFavoritedChanged();
    }

    @Override
    public int getItemCount() {
        return adoptsVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPetAdoptBinding binding;
        ImageView ivPet;
        MaterialButton btnFavorite;
        public ViewHolder(ItemPetAdoptBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivPet = itemView.findViewById(R.id.ivPetAdopt);
            btnFavorite = itemView.findViewById(R.id.btnFavoriteAdoptItem);
        }
    }
}
