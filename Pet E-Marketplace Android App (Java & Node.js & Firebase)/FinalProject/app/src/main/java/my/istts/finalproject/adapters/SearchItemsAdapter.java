package my.istts.finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;
import my.istts.finalproject.SearchItem;

import java.util.ArrayList;

public class SearchItemsAdapter extends RecyclerView.Adapter<SearchItemsAdapter.ViewHolder> {
    private onButtonClickCallback callbackItemSearchClick;
    private ArrayList<SearchItem> searchItems = new ArrayList<>();

    public SearchItemsAdapter(onButtonClickCallback callback){
        this.callbackItemSearchClick = callback;
    }

    public void setSearchItems(ArrayList<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackItemSearchClick.onButtonClick(searchItems.get(position).getIndex(), searchItems.get(position).getJenis());
            }
        });

        holder.tvItemSearchJudul.setText(searchItems.get(position).getSearch());
        holder.tvItemSearchJenis.setText(searchItems.get(position).getSearchType());
    }

    public interface onButtonClickCallback{
        void onButtonClick(int value, int jenis);
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemSearch; TextView tvItemSearchJudul; TextView tvItemSearchJenis;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemSearch = itemView.findViewById(R.id.ivItemSearch);
            tvItemSearchJudul = itemView.findViewById(R.id.tvItemSearchJudul);
            tvItemSearchJenis = itemView.findViewById(R.id.tvItemSearchJenis);
        }
    }
}
