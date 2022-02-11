package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemPaketgroomingBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.PaketGroomingItemViewModel;

import java.util.ArrayList;

public class PaketGroomingAdapter extends RecyclerView.Adapter<PaketGroomingAdapter.ViewHolder> {
    private ArrayList<PaketGroomingItemViewModel> packsVM = new ArrayList<>();
    private Context ctx;
    private onPaketQtyChanged onPaketQtyChangedCallback;

    public PaketGroomingAdapter(onPaketQtyChanged onPaketQtyChangedCallback) {
        this.onPaketQtyChangedCallback = onPaketQtyChangedCallback;
    }

    public void setPacksVM(ArrayList<PaketGroomingItemViewModel> packsVM) {
        this.packsVM = packsVM;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPaketgroomingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_paketgrooming, parent, false);
        return new PaketGroomingAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(packsVM.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        packsVM.get(position).getPaketInCart().observe((LifecycleOwner) ctx, jum->{
            onPaketQtyChangedCallback.onQtyChanged();
        });
    }

    public interface onPaketQtyChanged{
        void onQtyChanged();
    }

    @Override
    public int getItemCount() {
        return packsVM.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPaketgroomingBinding binding;
        public ViewHolder(ItemPaketgroomingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
