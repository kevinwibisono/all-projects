package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemDetailComplainBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.ItemPJViewModel;

import java.util.ArrayList;

public class ComplainProductAdapter extends RecyclerView.Adapter<ComplainProductAdapter.ViewHolder> {
    private ArrayList<ItemPJViewModel> itemPJs = new ArrayList<>();
    private onCheckedChanged onCheckedChangedCallback;
    private boolean showCheck;
    private Context ctx;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    public ComplainProductAdapter(onCheckedChanged onCheckedChangedCallback, boolean showCheck) {
        this.onCheckedChangedCallback = onCheckedChangedCallback;
        this.showCheck = showCheck;
    }

    public void setItemPJs(ArrayList<ItemPJViewModel> itemPJs) {
        this.itemPJs = itemPJs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailComplainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_detail_complain, parent, false);
        return new ComplainProductAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(itemPJs.get(position));
        holder.binding.setShowCheck(showCheck);

        Glide.with(ctx).load(itemPJs.get(position).getPicture()).into(holder.itemPic);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onCheckedChangedCallback.onChecked(position, b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemPJs.size();
    }

    public interface onCheckedChanged{
        void onChecked(int index, boolean checked);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDetailComplainBinding binding;
        CheckBox checkBox;
        ImageView itemPic;
        public ViewHolder(ItemDetailComplainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            checkBox = itemView.findViewById(R.id.checkComplainProduct);
            itemPic = itemView.findViewById(R.id.ivComplainProduct);
        }
    }
}
