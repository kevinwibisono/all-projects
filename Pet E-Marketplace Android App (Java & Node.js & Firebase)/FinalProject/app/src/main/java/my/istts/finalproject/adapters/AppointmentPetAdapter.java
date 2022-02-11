package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemPetAppoBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.AppoPetItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AppointmentPetAdapter extends RecyclerView.Adapter<AppointmentPetAdapter.ViewHolder> {
    private ArrayList<AppoPetItemViewModel> petVMs;
    private Context ctx;
    private onPetDelete onPetDeleteCallback;

    public AppointmentPetAdapter(onPetDelete onPetDeleteCallback) {
        this.onPetDeleteCallback = onPetDeleteCallback;
    }

    public void setPetVMs(ArrayList<AppoPetItemViewModel> petVMs) {
        this.petVMs = petVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPetAppoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_pet_appo, parent, false);
        return new AppointmentPetAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(petVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPetDeleteCallback.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return petVMs.size();
    }

    public interface onPetDelete{
        void onDelete(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPetAppoBinding binding;
        MaterialButton btnDelete;
        public ViewHolder(ItemPetAppoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            btnDelete = itemView.findViewById(R.id.btnDeleteAppoPet);
        }
    }
}
