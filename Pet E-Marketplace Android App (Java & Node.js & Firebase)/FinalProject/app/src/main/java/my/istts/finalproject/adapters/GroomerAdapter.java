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
import my.istts.finalproject.databinding.ItemGroomerBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.GroomerItemViewModel;

import java.util.ArrayList;

public class GroomerAdapter extends RecyclerView.Adapter<GroomerAdapter.ViewHolder> {
    private ArrayList<GroomerItemViewModel> groomerVMs = new ArrayList<>();
    private onGroomerClicked onGroomerClickedCallback;
    private Context ctx;

    public GroomerAdapter(onGroomerClicked onGroomerClickedCallback) {
        this.onGroomerClickedCallback = onGroomerClickedCallback;
    }

    public void setGroomerVMs(ArrayList<GroomerItemViewModel> groomerVMs) {
        this.groomerVMs = groomerVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroomerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_groomer, parent, false);
        return new GroomerAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);
        holder.binding.setViewmodel(groomerVMs.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGroomerClickedCallback.onClicked(groomerVMs.get(position).getEmailGroomer());
            }
        });

        groomerVMs.get(position).getGroomerPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivGroomer);
        });

        groomerVMs.get(position).getGroomerScore().observe((LifecycleOwner) ctx, skor->{
            if(!skor.equals("")){
                float score = Float.parseFloat(skor);
                for (int i = 0; i < 5; i++) {
                    //karena index dimulai 0, sedangkan skor dimulai dari 1, maka saat membanginkan tambah index(i) dgn 1
                    if((i+1) <= score){
                        holder.ivStars[i].setImageResource(R.drawable.ic_baseline_star_20);
                    }
                    else{
                        float diff = score - (i);
                        holder.ivStars[i].setImageResource(determineStar(diff));
                    }
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

    @Override
    public int getItemCount() {
        return groomerVMs.size();
    }

    public interface onGroomerClicked{
        void onClicked(String hp_groomer);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemGroomerBinding binding;
        ImageView ivGroomer;
        ImageView[] ivStars = new ImageView[5];
        public ViewHolder(ItemGroomerBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            this.binding = binding;
            ivGroomer = itemView.findViewById(R.id.ivGroomerPic);
            ivStars[0] = itemView.findViewById(R.id.starGroomer1);
            ivStars[1] = itemView.findViewById(R.id.starGroomer2);
            ivStars[2] = itemView.findViewById(R.id.starGroomer3);
            ivStars[3] = itemView.findViewById(R.id.starGroomer4);
            ivStars[4] = itemView.findViewById(R.id.starGroomer5);
        }
    }
}
