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

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemUnfinishedPaymentBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.UnfinishedPaymentItemViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class UnfinishedPaymentAdapter extends RecyclerView.Adapter<UnfinishedPaymentAdapter.ViewHolder> {
    private ArrayList<UnfinishedPaymentItemViewModel> paymentVMs = new ArrayList<>();
    private showDetailPayment showDetail;
    private Context ctx;

    public UnfinishedPaymentAdapter(showDetailPayment showDetail) {
        this.showDetail = showDetail;
    }

    public void setPaymentVMs(ArrayList<UnfinishedPaymentItemViewModel> paymentVMs) {
        this.paymentVMs = paymentVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUnfinishedPaymentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_unfinished_payment, parent, false);
        return new UnfinishedPaymentAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setViewmodel(paymentVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail.show(paymentVMs.get(position).getId());
            }
        });

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail.show(paymentVMs.get(position).getId());
            }
        });

        holder.ivMethod.setImageResource(paymentMethodPicture(paymentVMs.get(position).getMethod()));

    }

    private int paymentMethodPicture(String payment){
        String[] paymentMethods = {"Saldo PawFriends", "BCA Bank Transfer", "AGI Virtual Account", "BNI VA", "CIMB Niaga VA", "Mandiri VA",
                "Gerai Indomaret", "Gerai Alfamart", "QRIS/E-Wallet"};

        int[] paymentIcons = {R.drawable.iconmoney, R.drawable.iconbca, R.drawable.iconagi, R.drawable.iconbni, R.drawable.iconcimb, R.drawable.iconmandiri,
                R.drawable.iconindomaret, R.drawable.iconalfamart, R.drawable.iconqris};

        int chosenIcon = -1;
        for (int i = 0; i < paymentMethods.length; i++) {
            if(paymentMethods[i].equals(payment)){
                chosenIcon = paymentIcons[i];
            }
        }

        return chosenIcon;
    }

    public interface showDetailPayment{
        void show(String id_payment);
    }

    @Override
    public int getItemCount() {
        return paymentVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUnfinishedPaymentBinding binding;
        MaterialButton btnDetail;
        ImageView ivMethod;
        public ViewHolder(ItemUnfinishedPaymentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            btnDetail = itemView.findViewById(R.id.btnUnfinishedDetail);
            ivMethod = itemView.findViewById(R.id.ivUnfinishedMethod);
        }
    }
}
