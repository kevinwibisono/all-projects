package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.CartItemAdapter;
import my.istts.finalproject.databinding.ActivityCartBinding;
import my.istts.finalproject.viewmodels.CartViewModel;
import my.istts.finalproject.viewmodels.itemviewmodels.CartItemViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

public class CartActivity extends AppCompatActivity {

    private String jenis = " produk";
    private int tipe = 0;
    private CartViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new CartViewModel(getApplication());
        ActivityCartBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbCart = findViewById(R.id.tbCart);
        tbCart.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tbCart.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuCartDelete){
                    new MaterialAlertDialogBuilder(CartActivity.this)
                            .setTitle("Perhatian!")
                            .setMessage("Apakah anda yakin akan menghapus "+viewModel.getTotalCheckedItems()+jenis+" ?")
                            .setNegativeButton("Tidak", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .setPositiveButton("Ya", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                viewModel.deleteAllCheckedCarts();
                            })
                            .show();
                }
                return false;
            }
        });

        CartItemAdapter adapter = new CartItemAdapter(new CartItemAdapter.cartEventsListener() {

            @Override
            public void onCartDeleteClick(int position) {
                viewModel.deleteCart(position);
            }

            @Override
            public void onCartItemClick(String id_item, int tipe) {
                if (tipe == 0) {
                    Intent productIntent = new Intent(CartActivity.this, PetProductActivity.class);
                    productIntent.putExtra("id_produk", id_item);
                    startActivity(productIntent);
                } else if (tipe == 2) {
                    Intent hotelIntent = new Intent(CartActivity.this, HotelDetailActivity.class);
                    hotelIntent.putExtra("id_kamar", id_item);
                    startActivity(hotelIntent);
                }
            }

            @Override
            public void onSellerClick(String seller) {
                Intent sellerIntent = new Intent(CartActivity.this, SellerProfileActivity.class);
                sellerIntent.putExtra("hp_seller", seller);
                startActivity(sellerIntent);
            }

            @Override
            public void onCheckSeller(CartItemViewModel cartVM) {
                viewModel.clickCBSeller(cartVM);
            }

            @Override
            public void onCheckChange(CartItemViewModel cartVM) {
                viewModel.checkCartItem(cartVM);
            }

            @Override
            public void onQtyChange(CartItemViewModel cartVM) {
                viewModel.addReduceCart(cartVM);
            }
        });

        RecyclerView rvCart = findViewById(R.id.rvCart);
        rvCart.setAdapter(adapter);

        viewModel.getCartVMs().observe(this, vms ->{
            adapter.setCartVMs(vms);
            adapter.notifyDataSetChanged();
        });

        int selectedCartType = getIntent().getIntExtra("tipe", 0);
        tipe = selectedCartType;
        TabLayout tabs = findViewById(R.id.tabsCart);
        TabLayout.Tab tab = tabs.getTabAt(selectedCartType);
        tabs.selectTab(tab);
        changeButtonAndIntent(selectedCartType);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.getCartItems(tab.getPosition());
                tipe = tab.getPosition();
                changeButtonAndIntent(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        CheckBox cbAll = findViewById(R.id.cbCartAll);
        cbAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.switchAllChecked();
                viewModel.clickCBAll();
            }
        });

        ProgressDialog loadingScreen = new ProgressDialog(this);
        viewModel.getLoadingTitle().observe(this, loadingScreen::setTitle);
        loadingScreen.setCancelable(false);
        viewModel.isLoading().observe(this, loading ->{
            if(loading){
                loadingScreen.show();
            }
            else{
                loadingScreen.dismiss();
            }
        });
    }

    private void changeButtonAndIntent(int tipe){
        MaterialButton btnNext = findViewById(R.id.btnCartNext);
        if(tipe == 0){
            jenis = " produk";
            btnNext.setText("Beli");
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productCheckout = new Intent(CartActivity.this, ProductCheckoutActivity.class);
                    productCheckout.putExtra("dipilih", viewModel.getSelectedIds());
                    productCheckout.putExtra("daftar_penjual", viewModel.getSelectedSeller());
                    startActivity(productCheckout);
                }
            });
        }
        else if(tipe == 1){
            jenis = " paket grooming";
            btnNext.setText("Pesan");
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent groomCheckout = new Intent(CartActivity.this, GroomingCheckoutActivity.class);
                    groomCheckout.putExtra("dipilih", viewModel.getSelectedIds());
                    groomCheckout.putExtra("daftar_penjual", viewModel.getSelectedSeller());
                    startActivity(groomCheckout);
                }
            });
        }
        else if(tipe == 2){
            jenis = " kamar";
            btnNext.setText("Pesan");
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent hotelCheckout = new Intent(CartActivity.this, HotelCheckoutActivity.class);
                    hotelCheckout.putExtra("dipilih", viewModel.getSelectedIds());
                    hotelCheckout.putExtra("daftar_penjual", viewModel.getSelectedSeller());
                    startActivity(hotelCheckout);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getCartItems(tipe);
    }
}