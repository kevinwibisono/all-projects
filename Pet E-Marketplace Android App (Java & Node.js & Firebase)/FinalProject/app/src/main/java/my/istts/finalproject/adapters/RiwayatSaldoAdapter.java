package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemHistorySaldoBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.RiwayatItemViewModel;

import java.util.ArrayList;

public class RiwayatSaldoAdapter extends RecyclerView.Adapter<RiwayatSaldoAdapter.ViewHolder> {
    private ArrayList<RiwayatItemViewModel> historiesVMs = new ArrayList<>();
    private enlargeBuktiTransfer buktiTransferCallback;
    private Context ctx;

    public RiwayatSaldoAdapter(enlargeBuktiTransfer buktiTransferCallback) {
        this.buktiTransferCallback = buktiTransferCallback;
    }

    public void setHistoriesVMs(ArrayList<RiwayatItemViewModel> historiesVMs) {
        this.historiesVMs = historiesVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistorySaldoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_history_saldo, parent, false);
        return new RiwayatSaldoAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);
        holder.binding.setViewmodel(historiesVMs.get(position));

        int[] bankIcons = {R.drawable.iconagi, R.drawable.iconbca, R.drawable.iconbni, R.drawable.iconcimb, R.drawable.iconmandiri};
        holder.ivRekening.setImageResource(bankIcons[historiesVMs.get(position).getJenisRek()]);

        holder.tvBuktiTrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buktiTransferCallback.onEnlarge(historiesVMs.get(position).gambar_bukti());
            }
        });
    }

    public interface enlargeBuktiTransfer{
        void onEnlarge(String url_gambar);
    }

    @Override
    public int getItemCount() {
        return historiesVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistorySaldoBinding binding;
        ImageView ivRekening;
        TextView tvBuktiTrf;

        public ViewHolder(ItemHistorySaldoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            ivRekening = itemView.findViewById(R.id.ivHistoryRek);
            tvBuktiTrf = itemView.findViewById(R.id.tvRiwayatBuktiTrf);
        }

    }
}
