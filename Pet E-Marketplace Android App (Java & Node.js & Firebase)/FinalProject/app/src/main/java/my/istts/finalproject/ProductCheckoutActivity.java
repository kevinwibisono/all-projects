package my.istts.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ProductCheckoutAdapter;
import my.istts.finalproject.databinding.ActivityProductCheckoutBinding;
import my.istts.finalproject.interfaces.setOngkir;
import my.istts.finalproject.interfaces.setPayment;
import my.istts.finalproject.interfaces.setVoucher;
import my.istts.finalproject.inputclasses.PJInput;
import my.istts.finalproject.viewmodels.ProductCheckoutViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class ProductCheckoutActivity extends AppCompatActivity implements setOngkir, setVoucher, setPayment {

    private OngkirBottomDialogFragment btmOngkir;
    private VoucherBottomDialogFragment btmVoucher;
    private PaymentBottomDialogFragment btmPayment;
    private ProductCheckoutViewModel viewModel;
    private int REQUEST_CHOOSE_ADDRESS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ProductCheckoutViewModel(getApplication());
        ActivityProductCheckoutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_product_checkout);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbPC = findViewById(R.id.tbProductCheckout);
        tbPC.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ProductCheckoutAdapter adapter = new ProductCheckoutAdapter(new ProductCheckoutAdapter.openOngkirOptions() {
            @Override
            public void onShowOptions(String seller) {
                btmOngkir = new OngkirBottomDialogFragment();
                btmOngkir.setViewModel(viewModel);
                btmOngkir.show(getSupportFragmentManager(), "");
                viewModel.getOngkirOptions(seller);
            }
        }, new ProductCheckoutAdapter.openVouchers() {
            @Override
            public void onShowVouchers(String seller) {
                btmVoucher = new VoucherBottomDialogFragment();
                btmVoucher.setViewModel(viewModel);
                btmVoucher.show(getSupportFragmentManager(), "");
                viewModel.getSellerPromos(seller);
            }
        }, new ProductCheckoutAdapter.onSubtotalChanged() {
            @Override
            public void onChanged() {
                viewModel.recountTotal();
            }
        }, new ProductCheckoutAdapter.onCancelVoucher() {
            @Override
            public void onCancelled(String hp_seller) {
                viewModel.cancelProductCheckoutsPromo(hp_seller);
            }
        }, new ProductCheckoutAdapter.onOpenNote() {
            @Override
            public void onOpen(PJInput pjInput) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductCheckoutActivity.this);
                builder.setTitle("Tambahkan Catatan");

                final TextInputEditText input = new TextInputEditText(ProductCheckoutActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(pjInput.catatan.getValue());
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pjInput.catatan.setValue(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) input.getLayoutParams();
                params.setMargins(30, 30, 30, 30);
                input.requestLayout();
            }
        });

        RecyclerView rvItems = findViewById(R.id.rvBoughtItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvItems.setAdapter(adapter);

        viewModel.getCheckoutVMs().observe(this, vms ->{
            adapter.setCheckoutVMs(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getCartItems(getIntent().getStringExtra("dipilih"), getIntent().getStringExtra("daftar_penjual"));

        TextView tvOtherAddress = findViewById(R.id.tvOtherAddress);
        tvOtherAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otherIntent = new Intent(ProductCheckoutActivity.this, AddressChooseActivity.class);
                startActivityForResult(otherIntent, REQUEST_CHOOSE_ADDRESS);
            }
        });

        viewModel.getSelectedAddress();

        LinearLayout partChoosePayment = findViewById(R.id.partPCPayment);
        partChoosePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btmPayment = new PaymentBottomDialogFragment();
                btmPayment.setSaldo(viewModel.getSaldo());
                btmPayment.setTotal(viewModel.getTotal());
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
                Intent proceedPayment = new Intent(ProductCheckoutActivity.this, FinishPaymentActivity.class);
                proceedPayment.putExtra("id_payment", viewModel.getPaymentId());
                startActivity(proceedPayment);
            }
            else{
                Intent goToOrders = new Intent(ProductCheckoutActivity.this, MainActivity.class);
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
    public void onChooseOngkir(int pos) {
        btmOngkir.dismiss();
        viewModel.chooseOngkir(pos);
    }

    @Override
    public void onChooseVoucher(int position) {
        btmVoucher.dismiss();
        viewModel.choosePromo(position);
    }

    @Override
    public void onSetPayment(String payment) {
        ImageView ivPayment = findViewById(R.id.ivChosenPCPayment);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}