package my.istts.finalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ItemPaymentAdapter;
import my.istts.finalproject.databinding.ActivityFinishPaymentBinding;
import my.istts.finalproject.viewmodels.FinishPaymentViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class FinishPaymentActivity extends AppCompatActivity {

    private FinishPaymentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new FinishPaymentViewModel(getApplication());
        ActivityFinishPaymentBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_finish_payment);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbFinish = findViewById(R.id.tbFinishPayment);
        tbFinish.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitActivity();
            }
        });

        LinearLayout[] partCopy = {findViewById(R.id.partCopyPaymentNo), findViewById(R.id.partCopyPaymentTotal)};
        String[] clipLabel = {"va", "total"};
        String[] textCopied = {"", String.valueOf(viewModel.getTotal().getValue())};

        viewModel.getNomor().observe(this, nomor ->{
            textCopied[0] = nomor;
        });

        viewModel.getTotal().observe(this, total ->{
            textCopied[1] = String.valueOf(total);
        });

        for (int i = 0; i < 2; i++) {
            final int index = i;
            partCopy[i].setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(clipLabel[index], textCopied[index]);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(FinishPaymentActivity.this, "Berhasil Disalin", Toast.LENGTH_SHORT).show();
                }
            });
        }

        WebView wvQR = findViewById(R.id.wvFinishPaymentQR);
        viewModel.getQR().observe(this, wvQR::loadUrl);

        ImageView ivMethod = findViewById(R.id.ivFinishPaymentMethod);
        viewModel.getMethod().observe(this, method->{
            if(!method.equals("")){
                ivMethod.setImageResource(paymentMethodPicture(method));
            }
        });

        viewModel.getPayment(getIntent().getStringExtra("id_payment"));

        ItemPaymentAdapter adapter = new ItemPaymentAdapter(new ItemPaymentAdapter.onClickProduct() {
            @Override
            public void onClicked(String id_produk) {
                Intent productIntent = new Intent(FinishPaymentActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id_produk);
                startActivity(productIntent);
            }
        }, new ItemPaymentAdapter.onClickSeller() {
            @Override
            public void onClicked(String seller) {
                Intent sellerIntent = new Intent(FinishPaymentActivity.this, SellerProfileActivity.class);
                sellerIntent.putExtra("hp_seller", seller);
                startActivity(sellerIntent);
            }
        });

        RecyclerView rvItemPayment = findViewById(R.id.rvDetailPaymentItems);
        rvItemPayment.setAdapter(adapter);
        rvItemPayment.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        viewModel.getPaymentDetailItems().observe(this, items->{
            adapter.setItemVMs(items);
            adapter.notifyDataSetChanged();
        });

        MaterialButton cancelBtn = findViewById(R.id.btnFinishPayCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(FinishPaymentActivity.this)
                        .setTitle("Perhatian!")
                        .setMessage("Apakah anda yakin akan membatalkan pesanan ini?")
                        .setNegativeButton("Tidak", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            viewModel.cancelPayment(getIntent().getStringExtra("id_payment"));
                        })
                        .show();
            }
        });

        ProgressDialog loadingScreen = new ProgressDialog(this);
        loadingScreen.setTitle("Membatalkan Pesanan...");
        loadingScreen.setCancelable(false);
        viewModel.isCancelLoading().observe(this, loading ->{
            if(loading){
                loadingScreen.show();
            }
            else{
                loadingScreen.dismiss();
            }
        });

        viewModel.isCancelFinished().observe(this, canceled->{
            Toast.makeText(this, "Pesanan Berhasil Dibatalkan", Toast.LENGTH_SHORT).show();
            quitActivity();
        });
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

    private void quitActivity(){
        Intent goToOrders = new Intent(FinishPaymentActivity.this, MainActivity.class);
        goToOrders.putExtra("jumpTo", 2);
        startActivity(goToOrders);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        quitActivity();
    }
}