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
import my.istts.finalproject.databinding.ItemClinicBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.ClinicItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ClinicListAdapter extends RecyclerView.Adapter<ClinicListAdapter.ViewHolder> {
    private ArrayList<ClinicItemViewModel> clinics = new ArrayList<>();
    private toClinicPage pageCallback;
    private navigateToClinic navigateToClinicCallback;
    private Context ctx;

    public ClinicListAdapter(toClinicPage pageCallback, navigateToClinic navigateToClinicCallback) {
        this.pageCallback = pageCallback;
        this.navigateToClinicCallback = navigateToClinicCallback;
    }

    public void setClinics(ArrayList<ClinicItemViewModel> clinics) {
        this.clinics = clinics;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClinicBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_clinic, parent, false);
        return new ClinicListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setViewmodel(clinics.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageCallback.toPage(clinics.get(position).getEmailClinic());
            }
        });

        clinics.get(position).getClinicPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivClinic);
        });

        clinics.get(position).getClinicScore().observe((LifecycleOwner) ctx, skor->{
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

        holder.btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToClinicCallback.navigateTo(clinics.get(position).getClinicCoors());
            }
        });
    }

    public interface navigateToClinic{
        void navigateTo(String koordinat);
    }

    public interface toClinicPage{
        void toPage(String hp_klinik);
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
        return clinics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemClinicBinding binding;
        ImageView ivClinic;
        ImageView[] ivStars = new ImageView[5];
        MaterialButton btnNavigate;
        public ViewHolder(ItemClinicBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            this.binding = binding;
            ivClinic = itemView.findViewById(R.id.ivClinicPic);
            btnNavigate = itemView.findViewById(R.id.btnNavigationClinic);
            ivStars[0] = itemView.findViewById(R.id.starClinic1);
            ivStars[1] = itemView.findViewById(R.id.starClinic2);
            ivStars[2] = itemView.findViewById(R.id.starClinic3);
            ivStars[3] = itemView.findViewById(R.id.starClinic4);
            ivStars[4] = itemView.findViewById(R.id.starClinic5);
        }
    }
}
