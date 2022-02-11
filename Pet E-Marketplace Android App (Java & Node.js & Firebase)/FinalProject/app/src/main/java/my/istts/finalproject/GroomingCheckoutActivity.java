package my.istts.finalproject;

import androidx.annotation.Nullable;
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
import android.widget.TextView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.GroomingCheckoutAdapter;
import my.istts.finalproject.databinding.ActivityGroomingCheckoutBinding;
import my.istts.finalproject.interfaces.setPayment;
import my.istts.finalproject.viewmodels.GroomingCheckoutViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class GroomingCheckoutActivity extends AppCompatActivity implements setPayment {

    private int REQUEST_CHOOSE_ADDRESS = 1;
    private GroomingCheckoutViewModel viewModel;
    private PaymentBottomDialogFragment btmPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grooming_checkout);

        viewModel = new GroomingCheckoutViewModel(getApplication());
        ActivityGroomingCheckoutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_grooming_checkout);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbGroomCheckout = findViewById(R.id.tbGroomingCheckout);
        tbGroomCheckout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GroomingCheckoutAdapter adapter = new GroomingCheckoutAdapter(new GroomingCheckoutAdapter.onSubtotalChanged() {
            @Override
            public void onChanged() {
                viewModel.recountTotal();
            }
        }, new GroomingCheckoutAdapter.onGroomingDateChanged() {
            @Override
            public void onChanged() {
                viewModel.chooseGroomingDate();
            }
        });

        RecyclerView rvGroomingCheckout = findViewById(R.id.rvGroomingPacks);
        rvGroomingCheckout.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvGroomingCheckout.setAdapter(adapter);

        viewModel.getCheckoutVMs().observe(this, vms->{
            adapter.setCheckoutVMs(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getCartItems(getIntent().getStringExtra("dipilih"), getIntent().getStringExtra("daftar_penjual"));

        TextView tvOtherAddress = findViewById(R.id.tvOtherAddressGrooming);
        tvOtherAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otherIntent = new Intent(GroomingCheckoutActivity.this, AddressChooseActivity.class);
                startActivityForResult(otherIntent, REQUEST_CHOOSE_ADDRESS);
            }
        });

        viewModel.getSelectedAddress();

        LinearLayout partChoosePayment = findViewById(R.id.partGCPayment);
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
            if(!viewModel.getId_payment().equals("")){
                Intent proceedPayment = new Intent(GroomingCheckoutActivity.this, FinishPaymentActivity.class);
                proceedPayment.putExtra("id_payment", viewModel.getId_payment());
                startActivity(proceedPayment);
            }
            else{
                Intent goToOrders = new Intent(GroomingCheckoutActivity.this, MainActivity.class);
                goToOrders.putExtra("jumpTo", 2);
                startActivity(goToOrders);
            }
            viewModel.deleteCartsIncluded(getIntent().getStringExtra("dipilih"));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHOOSE_ADDRESS){
            if(data != null){
                String id = data.getStringExtra("id_alamat");
                viewModel.setSelectedAddress(id);
            }
            else{
                viewModel.getSelectedAddress();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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