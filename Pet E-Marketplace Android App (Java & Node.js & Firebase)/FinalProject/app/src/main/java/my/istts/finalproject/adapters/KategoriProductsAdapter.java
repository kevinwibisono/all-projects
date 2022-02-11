package my.istts.finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;
import my.istts.finalproject.inputclasses.KategoriProduct;

import java.util.ArrayList;

public class KategoriProductsAdapter extends RecyclerView.Adapter<KategoriProductsAdapter.ViewHolder> {
    ArrayList<KategoriProduct> listKategori;
    onCategoryClick onCategoryClickCallback;

    public KategoriProductsAdapter(ArrayList<KategoriProduct> listKategori, onCategoryClick callback) {
        this.listKategori = listKategori;
        this.onCategoryClickCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v = inf.inflate(R.layout.item_kategori_shopping,parent,false);
        return new KategoriProductsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.ivKategori.setImageResource(listKategori.get(position).getImage());
        holder.tvKategori.setText(listKategori.get(position).getNama());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoryClickCallback.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listKategori.size();
    }

    public interface onCategoryClick{
        void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivKategori; TextView tvKategori;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivKategori = itemView.findViewById(R.id.ivKategoriProduct);
            tvKategori = itemView.findViewById(R.id.tvKategoriProduct);
        }
    }
}
