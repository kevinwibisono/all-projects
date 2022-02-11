package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemVoucherBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.PromoItemViewModel;

import java.util.ArrayList;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private ArrayList<PromoItemViewModel> voucherVMs = new ArrayList<>();
    private chooseVoucher chooseVoucherCallback;
    private seeVoucherDetail detailVoucherCallback;
    private Context ctx;

    public VoucherAdapter(chooseVoucher chooseVoucherCallback, seeVoucherDetail detailVoucherCallback, Context ctx) {
        this.chooseVoucherCallback = chooseVoucherCallback;
        this.detailVoucherCallback = detailVoucherCallback;
        this.ctx = ctx;
    }

    public void setVoucherVMs(ArrayList<PromoItemViewModel> voucherVMs) {
        this.voucherVMs = voucherVMs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVoucherBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_voucher, parent, false);
        return new VoucherAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(voucherVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(voucherVMs.get(position).isValid()){
                    chooseVoucherCallback.onChosen(position);
                }
            }
        });

        holder.tvSKPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailVoucherCallback.onDetail(voucherVMs.get(position).getPromoId());
            }
        });
    }

    public interface chooseVoucher{
        void onChosen(int position);
    }

    public interface seeVoucherDetail{
        void onDetail(String id);
    }

    @Override
    public int getItemCount() {
        return voucherVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemVoucherBinding binding;
        TextView tvSKPromo;
        public ViewHolder(ItemVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            tvSKPromo = binding.getRoot().findViewById(R.id.tvSKPromo);
        }
    }
}
