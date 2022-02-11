package com.example.sellerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp.R;

public class DiscussAdapter extends RecyclerView.Adapter<DiscussAdapter.ViewHolder> {
    private int[] tempForLength;
    private seeFullDiscuss fullDiscussCallback;

    public DiscussAdapter(int[] tempForLength, seeFullDiscuss fullDiscussCallback) {
        this.tempForLength = tempForLength;
        this.fullDiscussCallback = fullDiscussCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v = inf.inflate(R.layout.item_comment_header,parent,false);
        return new DiscussAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvFullComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullDiscussCallback.seeFull();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tempForLength.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullComments;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullComments = itemView.findViewById(R.id.tvFullDiscuss);
        }
    }

    public interface seeFullDiscuss{
        void seeFull();
    }
}
