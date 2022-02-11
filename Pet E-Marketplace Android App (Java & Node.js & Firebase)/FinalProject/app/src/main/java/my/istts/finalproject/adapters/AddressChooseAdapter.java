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
import my.istts.finalproject.databinding.ItemAddressChooseBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.AddressItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AddressChooseAdapter extends RecyclerView.Adapter<AddressChooseAdapter.ViewHolder> {
    private ArrayList<AddressItemViewModel> addrsVMs = new ArrayList<>();
	private addressChoose callback;
	private addressUpdate updateCallback;
	private addressDelete deleteCallback;
	private Context ctx;

    public AddressChooseAdapter(addressChoose callback, addressUpdate updateCallback, addressDelete deleteCallback) {
        this.callback = callback;
        this.updateCallback = updateCallback;
        this.deleteCallback = deleteCallback;
    }

    public void setAddrsVMs(ArrayList<AddressItemViewModel> addrsVMs) {
        this.addrsVMs = addrsVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddressChooseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_address_choose, parent, false);
        return new AddressChooseAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.setViewmodel(addrsVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onChosen(addrsVMs.get(position).getId());
            }
        });

		holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCallback.onUpdate(addrsVMs.get(position).getId());
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCallback.onDelete(addrsVMs.get(position).getId());
            }
        });
    }
	
	public interface addressChoose{
		void onChosen(String id);
	}

    public interface addressUpdate{
        void onUpdate(String id);
    }

    public interface addressDelete{
        void onDelete(String id);
    }

    @Override
    public int getItemCount() {
        return addrsVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemAddressChooseBinding binding;
        MaterialButton btnUpdate, btnDelete;
        public ViewHolder(ItemAddressChooseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            btnUpdate = itemView.findViewById(R.id.btnAddressUpdate);
            btnDelete = itemView.findViewById(R.id.btnAddressDelete);
        }
    }
}
