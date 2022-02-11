package com.example.sellerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class PetAppointmentAdapter extends RecyclerView.Adapter<PetAppointmentAdapter.ViewHolder> {
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> ages = new ArrayList<>();
    private ArrayList<String> kinds = new ArrayList<>();

    public void setNames(String[] names) {
        this.names.clear();
        this.names.addAll(Arrays.asList(names));
    }

    public void setAges(String[] ages) {
        this.ages.clear();
        this.ages.addAll(Arrays.asList(ages));
    }

    public void setKinds(String[] kinds) {
        this.kinds.clear();
        this.kinds.addAll(Arrays.asList(kinds));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v = inf.inflate(R.layout.item_pet_appointment,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		holder.kindName.setText(kinds.get(position)+" "+names.get(position));
		holder.age.setText(ages.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView kindName, age;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kindName = itemView.findViewById(R.id.tvPetAppoName);
            age = itemView.findViewById(R.id.tvPetAppoAge);
        }
    }
}
