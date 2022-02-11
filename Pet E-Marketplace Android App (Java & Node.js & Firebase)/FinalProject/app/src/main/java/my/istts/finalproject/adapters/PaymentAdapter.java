package my.istts.finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    private String[] methods;
    private int[] methodsPics;
	private paymentMethodSelected callback;

    public PaymentAdapter(String[] methods, int[] methodsPics, paymentMethodSelected callback) {
        this.methods = methods;
        this.methodsPics = methodsPics;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v = inf.inflate(R.layout.item_payment,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPaymentSelected(methods[position]);
            }
        });

		holder.ivPayment.setImageResource(methodsPics[position]);
		holder.tvPayment.setText(methods[position]);
    }
	
	public interface paymentMethodSelected{
		void onPaymentSelected(String payment);
	}

    @Override
    public int getItemCount() {
        return methods.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPayment;
        TextView tvPayment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPayment = itemView.findViewById(R.id.ivPaymentMethod);
            tvPayment = itemView.findViewById(R.id.tvPaymentMethod);
        }
    }
}
