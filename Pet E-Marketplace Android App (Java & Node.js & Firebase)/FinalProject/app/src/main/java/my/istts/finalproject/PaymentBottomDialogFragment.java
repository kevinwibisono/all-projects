package my.istts.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.PaymentAdapter;
import my.istts.finalproject.interfaces.setPayment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PaymentBottomDialogFragment extends BottomSheetDialogFragment {    private LiveData<Integer> saldo, total;
    private setPayment paymentFunc;

    public void setSaldo(LiveData<Integer> saldo) {
        this.saldo = saldo;
    }

    public void setTotal(LiveData<Integer> total) {
        this.total = total;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_dialog_payment, container,
                false);

        // get the views and attach the listener
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] paymentMethods = {"BCA Bank Transfer", "AGI Virtual Account", "BNI VA", "CIMB Niaga VA", "Mandiri VA",
                "Gerai Indomaret", "Gerai Alfamart", "QRIS/E-Wallet"};

        int[] paymentIcons = {R.drawable.iconbca, R.drawable.iconagi, R.drawable.iconbni, R.drawable.iconcimb, R.drawable.iconmandiri,
                R.drawable.iconindomaret, R.drawable.iconalfamart, R.drawable.iconqris};

        PaymentAdapter adapter = new PaymentAdapter(paymentMethods, paymentIcons, new PaymentAdapter.paymentMethodSelected() {
            @Override
            public void onPaymentSelected(String payment) {
                paymentFunc.onSetPayment(payment);
            }
        });

        RecyclerView rvOngkir = view.findViewById(R.id.rvPaymentOptions);
        rvOngkir.setAdapter(adapter);

        TextView saldoPawfriends = view.findViewById(R.id.tvPaymentSaldo);
        LinearLayout ownSaldo = view.findViewById(R.id.partBtmPaymentSaldo);

        saldo.observe(getViewLifecycleOwner(), saldoNum->{
            if(saldoNum < total.getValue()){
                ownSaldo.setAlpha((float) 0.15);
            }
            else{
                ownSaldo.setAlpha(1);
                ownSaldo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paymentFunc.onSetPayment("Saldo PawFriends");
                    }
                });
            }
            String saldoStr = "Rp "+ThousandSeparator.getTS(saldoNum);
            saldoPawfriends.setText(saldoStr);
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof setPayment){
            paymentFunc = (setPayment) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement the interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        paymentFunc = null;
    }

}
