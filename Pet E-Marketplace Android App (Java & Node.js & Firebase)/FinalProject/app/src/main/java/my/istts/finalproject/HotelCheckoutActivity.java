package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.HotelCheckoutAdapter;

import my.istts.finalproject.databinding.ActivityHotelCheckoutBinding;
import my.istts.finalproject.interfaces.setPayment;
import my.istts.finalproject.viewmodels.HotelCheckoutViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class HotelCheckoutActivity extends AppCompatActivity implements setPayment {

    private HotelCheckoutViewModel viewModel;
    private PaymentBottomDialogFragment btmPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_checkout);

        viewModel = new HotelCheckoutViewModel(getApplication());
        ActivityHotelCheckoutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_hotel_checkout);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbHotelCheckout = findViewById(R.id.tbHotelCheckout);
        tbHotelCheckout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        HotelCheckoutAdapter adapter = new HotelCheckoutAdapter(new HotelCheckoutAdapter.onSubtotalChanged() {
            @Override
            public void onChanged() {
                viewModel.recountTotal();
            }
        }, new HotelCheckoutAdapter.onBookBeginDateChanged() {
            @Override
            public void onChanged() {
                viewModel.chooseBookingDate();
            }
        }, new HotelCheckoutAdapter.onBookEndDateChanged() {
            @Override
            public void onChanged() {
                viewModel.chooseBookingDate();
            }
        });

        RecyclerView rvHotel = findViewById(R.id.rvBookedRooms);
        rvHotel.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvHotel.setAdapter(adapter);

        viewModel.getCheckoutVMs().observe(this, vms->{
            adapter.setCheckoutVMs(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getCartItems(getIntent().getStringExtra("dipilih"), getIntent().getStringExtra("daftar_penjual"));

        LinearLayout partChoosePayment = findViewById(R.id.partHCPayment);
        partChoosePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btmPayment = new PaymentBottomDialogFragment();
                btmPayment.setTotal(viewModel.getTotal());
                btmPayment.setSaldo(viewModel.getSaldo());
                btmPayment.show(getSupportFragmentManager(), "");
                viewModel.getMySaldo();
            }
        });

        ProgressDialog loadingScreen = new ProgressDialog(this);
        loadingScreen.setTitle("Proses Checkout....");
        loadingScreen.setCancelable(false);
        viewModel.isCheckoutProcessLoading().observe(this, loading ->{
            if(loading){
                loadingScreen.show();
            }
            else{
                loadingScreen.dismiss();
            }
        });

        viewModel.isCheckoutDone().observe(this, done ->{
            if(!viewModel.getPaymentId().equals("")){
                Intent proceedPayment = new Intent(HotelCheckoutActivity.this, FinishPaymentActivity.class);
                proceedPayment.putExtra("id_payment", viewModel.getPaymentId());
                startActivity(proceedPayment);
            }
            else{
                Intent goToOrders = new Intent(HotelCheckoutActivity.this, MainActivity.class);
                goToOrders.putExtra("jumpTo", 2);
                startActivity(goToOrders);
            }
            viewModel.deleteCartsIncluded(getIntent().getStringExtra("dipilih"));
        });
    }

    @Override
    public void onSetPayment(String payment) {
        ImageView ivPayment = findViewById(R.id.ivChosenGCPayment);
        ivPayment.setImageResource(paymentMethodPicture(payment));

        btmPayment.dismiss();
        viewModel.setPaymentMethod(payment);
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
}