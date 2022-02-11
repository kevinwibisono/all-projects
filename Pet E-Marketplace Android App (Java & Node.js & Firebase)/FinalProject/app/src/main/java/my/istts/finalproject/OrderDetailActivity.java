package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.OrderItemsAdapter;

import my.istts.finalproject.databinding.ActivityOrderDetailBinding;
import my.istts.finalproject.viewmodels.OrderDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class OrderDetailActivity extends AppCompatActivity {

    private OrderDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        viewModel = new OrderDetailViewModel(getApplication());
        ActivityOrderDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getOrderDetails(getIntent().getStringExtra("id_pj"));

        MaterialToolbar tbOrderDetail = findViewById(R.id.tbOrderDetail);
        tbOrderDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        OrderItemsAdapter adapter = new OrderItemsAdapter(new OrderItemsAdapter.onClickOrderItemProduct() {
            @Override
            public void onClickProduct(String id_produk) {
                Intent productIntent = new Intent(OrderDetailActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id_produk);
                startActivity(productIntent);
            }
        }, new OrderItemsAdapter.onClickOrderItemHotel() {
            @Override
            public void onClickHotel(String id_kamar) {
                Intent hotelIntent = new Intent(OrderDetailActivity.this, HotelDetailActivity.class);
                hotelIntent.putExtra("id_kamar", id_kamar);
                startActivity(hotelIntent);
            }
        });

        RecyclerView rvOrders = findViewById(R.id.rvOrderItems);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        viewModel.getItemPJs().observe(this, vms->{
            adapter.setItemVMs(vms);
            adapter.notifyDataSetChanged();
        });

        LinearLayout partSeller = findViewById(R.id.partOrderDetailSeller);
        viewModel.getSellerPhone().observe(this, phone->{
            partSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sellerIntent = new Intent(OrderDetailActivity.this, SellerProfileActivity.class);
                    sellerIntent.putExtra("hp_seller", phone);
                    startActivity(sellerIntent);
                }
            });
        });

        LinearLayout partResi = findViewById(R.id.partNoResi);
        viewModel.getNoResi().observe(this, resi->{
            partResi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Noresi", resi);
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(OrderDetailActivity.this, "Berhasil Disalin", Toast.LENGTH_SHORT).show();
                }
            });
        });

        ImageView ivSeller = findViewById(R.id.ivOrderDetailSeller);
        viewModel.getSellerPic().observe(this, pic-> {
            Glide.with(this).load(pic).into(ivSeller);
        });

        MaterialButton btnChat = findViewById(R.id.btnOrderDetailChatSeller);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(OrderDetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("lawan_bicara", viewModel.getSellerPhone().getValue());
                chatIntent.putExtra("id_item", getIntent().getStringExtra("id_pj"));
                chatIntent.putExtra("tipe", 1);
                startActivity(chatIntent);
            }
        });

        MaterialButton[] btnActions = {findViewById(R.id.btnOrderDetailNavigate), findViewById(R.id.btnOrderDetailReview), findViewById(R.id.btnOrderDetailFinish),
                findViewById(R.id.btnOrderDetailCancel), findViewById(R.id.btnOrderDetailComplaint), findViewById(R.id.btnOrderDetailLacak)};
        for (int i = 0; i < btnActions.length; i++) {
            final int index = i;
            btnActions[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderActions(index);
                }
            });
        }

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Mengubah Status...");
        loading.setCancelable(true);
        viewModel.isLoading().observe(this, updateOrderLoading->{
            if(updateOrderLoading){
                loading.show();
            }
            else{
                loading.dismiss();
            }
        });

    }

    private void orderActions(int index){
        if(index == 0){
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+viewModel.getKoordinat().getValue());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        else if(index == 1){
            Intent reviewIntent = new Intent(OrderDetailActivity.this, ReviewActivity.class);
            reviewIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
            startActivity(reviewIntent);
        }
        else if(index == 2){
            new MaterialAlertDialogBuilder(OrderDetailActivity.this)
                    .setTitle("Perhatian!")
                    .setMessage("Apakah anda yakin akan menyelesaikan pesanan ini? ")
                    .setNegativeButton("Tidak", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .setPositiveButton("Ya", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        viewModel.finishOrder();
                    })
                    .show();
        }
        else if(index == 3){
            new MaterialAlertDialogBuilder(OrderDetailActivity.this)
                    .setTitle("Perhatian!")
                    .setMessage("Apakah anda yakin akan membatalkan pesanan ini? ")
                    .setNegativeButton("Tidak", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .setPositiveButton("Ya", (dialogInterface, i) -> {
                        dialogInterface.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                        builder.setTitle("Tuliskan Alasan Pembatalan");

                        final TextInputEditText input = new TextInputEditText(OrderDetailActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(input.getText().toString().length() > 3){
                                    viewModel.cancelOrder(input.getText().toString());
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    })
                    .show();
        }
        else if(index == 4){
            Intent compIntent = new Intent(OrderDetailActivity.this, ComplainsActivity.class);
            compIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
            startActivity(compIntent);
        }
        else{
            Intent trackIntent = new Intent(OrderDetailActivity.this, GroomerLocationActivity.class);
            trackIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
            startActivity(trackIntent);
        }
    }
}