package my.istts.finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemOngkirBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.OngkirItemViewModel;

import java.util.ArrayList;

public class OngkirAdapter extends RecyclerView.Adapter<OngkirAdapter.ViewHolder> {
    private ArrayList<OngkirItemViewModel> ongkirVMs = new ArrayList<>();
    private chooseOngkir chooseOngkirCallback;

    public OngkirAdapter(chooseOngkir chooseOngkirCallback) {
        this.chooseOngkirCallback = chooseOngkirCallback;
    }

    public void setOngkirVMs(ArrayList<OngkirItemViewModel> ongkirVMs) {
        this.ongkirVMs = ongkirVMs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOngkirBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_ongkir, parent, false);
        return new OngkirAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(ongkirVMs.get(position));

        String[] kurirNames = {"pickup", "jne", "pos", "tiki"};
        int[] kurirDrawable = {R.drawable.self_pickup, R.drawable.logo_jne, R.drawable.logo_pos, R.drawable.logo_tiki};

        int matchLogo = -1;
        for (int i = 0; i < 4; i++) {
            if(kurirNames[i].equals(ongkirVMs.get(position).getKurir())){
                matchLogo = kurirDrawable[i];
            }
        }
        if(matchLogo > -1){
            holder.ivOngkir.setImageResource(matchLogo);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseOngkirCallback.onChosen(position);
            }
        });
    }

    public interface chooseOngkir{
        void onChosen(int position);
    }

    @Override
    public int getItemCount() {
        return ongkirVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemOngkirBinding binding;
        ImageView ivOngkir;
        public ViewHolder(ItemOngkirBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivOngkir = itemView.findViewById(R.id.ivOngkirKurir);
        }
    }
}
