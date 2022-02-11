package com.example.sellerapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sellerapp.adapters.OrderItemsAdapter;
import com.example.sellerapp.databinding.ActivityOrderDetailBinding;
import com.example.sellerapp.viewmodels.OrderDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OrderDetailActivity extends AppCompatActivity {

    private OrderDetailViewModel viewModel;
    private LinearLayout pageOrderDetail;
    private LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new OrderDetailViewModel();
        ActivityOrderDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        pageOrderDetail = findViewById(R.id.pageOrderDetail);

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
                Intent productIntent = new Intent(OrderDetailActivity.this, PreviewProductActivity.class);
                productIntent.putExtra("id_produk", id_produk);
                startActivity(productIntent);
            }
        }, new OrderItemsAdapter.onClickOrderItemHotel() {
            @Override
            public void onClickHotel(String id_kamar) {
                Intent hotelIntent = new Intent(OrderDetailActivity.this, HotelPreviewActivity.class);
                hotelIntent.putExtra("id_kamar", id_kamar);
                startActivity(hotelIntent);
            }
        });

        RecyclerView rvOrders = findViewById(R.id.rvOrderItems);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        viewModel.getItemPJs().observe(this, vms -> {
            adapter.setItemVMs(vms);
            adapter.notifyDataSetChanged();
        });

        ImageView ivSeller = findViewById(R.id.ivOrderDetailSeller);
        viewModel.getBuyerPic().observe(this, pic -> {
            Glide.with(this).load(pic).into(ivSeller);
        });

        viewModel.getOrderDetails(getIntent().getStringExtra("id_pj"));

        MaterialButton btnChat = findViewById(R.id.btnOrderDetailChatSeller);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(OrderDetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("lawan_bicara", viewModel.getBuyerEmail().getValue());
                chatIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
                startActivity(chatIntent);
            }
        });

        MaterialButton[] btnSellerActions = {findViewById(R.id.btnOrderDetailAccept), findViewById(R.id.btnOrderDetailReadyPickup),
                findViewById(R.id.btnOrderDetailSendPackage), findViewById(R.id.btnOrderDetailComplaints),
                findViewById(R.id.btnGroomingDetailOTW), findViewById(R.id.btnGroomingDetailNavigasi), findViewById(R.id.btnGroomingDetailArrive)};

        for (int i = 0; i < btnSellerActions.length; i++) {
            final int index = i;
            btnSellerActions[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sellerActions(index);
                }
            });
        }

        viewModel.getNoResi().observe(this, resi->{
            LinearLayout partResi = findViewById(R.id.partNoResi);
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


        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Mengubah Status...");
        loading.setCancelable(true);
        viewModel.isLoading().observe(this, currentlyLoading -> {
            if (currentlyLoading) {
                loading.show();
            } else {
                loading.dismiss();
            }
        });

        ProgressDialog coorLoading = new ProgressDialog(this);
        coorLoading.setTitle("Mendapatkan Koordinat...");
        coorLoading.setCancelable(true);
        viewModel.isGettingCoordinates().observe(this, currentlyLoading -> {
            if (currentlyLoading) {
                loading.show();
            } else {
                loading.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void reqPermissions(){
//        new MaterialAlertDialogBuilder(OrderDetailActivity.this)
//                .setTitle("Aplikasi Membutuhkan Akses Lokasi!")
//                .setMessage("Agar lokasi groomer dapat dipantau oleh pembeli di aplikasi, maka pihak groomer harus memberikan permission atas lokasinya sepanjang waktu" +
//                        ", meskipun aplikasi sedang tidak digunakan. Dengan menekan tombol setuju, maka pihak groomer menyetujui untuk aplikasi mendapatkan lokasi groomer" +
//                        " dan menampilkannya kepada pelanggan")
//                .setNegativeButton("Tidak", (dialogInterface, i) -> {
//                    dialogInterface.dismiss();
//                    Toast.makeText(this, "Dibutuhkan akses lokasi sepanjang waktu untuk fitur pelacakan posisi groomer", Toast.LENGTH_SHORT).show();
//                })
//                .setPositiveButton("Setuju", (dialogInterface, i) -> {
//                    dialogInterface.dismiss();
//
//                })
//                .show();
        String[] permissions = new String[2];
        permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
        permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
        requestPermissions(permissions, 0);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sellerActions(int index) {
        if (index == 0) {
            viewModel.acceptOrder();
        } else if (index == 1) {
            viewModel.readyOrderForPickup();
        } else if (index == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
            builder.setTitle("Masukkan No Resi");

            final TextInputEditText input = new TextInputEditText(OrderDetailActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (input.getText().toString().length() > 3) {
                        viewModel.deliverOrder(input.getText().toString());
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
        } else if (index == 3) {
            Intent komplainIntent = new Intent(OrderDetailActivity.this, KomplainListActivity.class);
            viewModel.getIdPJ().observe(OrderDetailActivity.this, idPJ -> {
                komplainIntent.putExtra("id_pj", idPJ);
            });
            startActivity(komplainIntent);
        } else if (index == 4) {
            reqPermissions();
        } else if (index == 5) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + viewModel.getCoordinate().getValue());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            stopService(new Intent(this, GroomerLocationService.class));
            viewModel.groomerArrive();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(false);
                criteria.setCostAllowed(true);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        viewModel.setGettingCoordinates(false);
                        // Do work with new location. Implementation of this method will be covered later.
                        viewModel.groomerOtw(location.getLatitude(), location.getLongitude());
                        activateLocationWatcher();
                    }
                };
                assert locationManager != null;
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    locationManager.requestSingleUpdate(criteria, locationListener, null);
                }
                else{
                    Toast.makeText(this, "Lokasi tidak terdeteksi, mohon aktifkan gps anda", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Dibutuhkan akses lokasi sepanjang waktu untuk fitur pelacakan posisi groomer", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void activateLocationWatcher() {
        Intent intent = new Intent(this, GroomerLocationService.class);
        intent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent);
        } else{
            startService(intent);
        }
    }
}