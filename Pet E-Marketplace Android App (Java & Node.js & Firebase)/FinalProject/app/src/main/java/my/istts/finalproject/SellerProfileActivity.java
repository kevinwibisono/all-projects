package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.HotelListAdapter;
import my.istts.finalproject.adapters.PaketGroomingAdapter;
import my.istts.finalproject.adapters.ReccHotelAdapter;
import my.istts.finalproject.adapters.ReccProductsAdapter;
import my.istts.finalproject.adapters.ReccProductsSliderAdapter;
import my.istts.finalproject.adapters.ReviewsAdapter;
import my.istts.finalproject.adapters.ShopVoucherAdapter;

import my.istts.finalproject.databinding.ActivitySellerProfileBinding;
import my.istts.finalproject.viewmodels.SellerProfileViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class SellerProfileActivity extends AppCompatActivity {
    private SellerProfileViewModel viewModel;
    private String hp_seller = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new SellerProfileViewModel(getApplication());
        ActivitySellerProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_profile);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbShopProfile = findViewById(R.id.tbShopProfile);
        tbShopProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tbShopProfile.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeMenuIntent(item.getItemId());
                return false;
            }
        });

        MaterialToolbar tbKlinik = findViewById(R.id.tbClinicProfile);
        tbKlinik.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewModel.isItemsInCartAppo().observe(this, thereIsItem->{
            MenuItem itemCart = tbShopProfile.getMenu().findItem(R.id.headerProductCart);
            View actionView = itemCart.getActionView();
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeMenuIntent(R.id.headerProductCart);
                }
            });
            TextView textCartItemCount = (TextView) actionView.findViewById(R.id.icon_cart_badge);

            MenuItem itemAppo = tbKlinik.getMenu().findItem(R.id.menuAppoinment);
            actionView = itemAppo.getActionView();
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent orderIntent = new Intent(SellerProfileActivity.this, OrderListActivity.class);
                    orderIntent.putExtra("tipe", 3);
                    startActivity(orderIntent);
                }
            });
            TextView textAppoItemCount = (TextView) actionView.findViewById(R.id.icon_appo_badge);


            if(thereIsItem){
                textCartItemCount.setVisibility(View.VISIBLE);
                textAppoItemCount.setVisibility(View.VISIBLE);
            }
            else{
                textCartItemCount.setVisibility(View.GONE);
                textAppoItemCount.setVisibility(View.GONE);
            }
        });

        //set Recycler View for Top Selling Products
        ReccProductsSliderAdapter sellerproadapter = new ReccProductsSliderAdapter(new ReccProductsSliderAdapter.onProductClickCallback() {
            @Override
            public void onProductClick(String id) {
                Intent productIntent = new Intent(SellerProfileActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id);
                startActivity(productIntent);
            }
        });
        RecyclerView rvTopSelling = findViewById(R.id.rvShopTop);
        rvTopSelling.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvTopSelling.setAdapter(sellerproadapter);

        viewModel.getTopProducts().observe(this, vms->{
            sellerproadapter.setProductVMs(vms);
            sellerproadapter.notifyDataSetChanged();
        });

        //set Recycler View for All Products
        ReccProductsAdapter reccProductsAdapter = new ReccProductsAdapter(new ReccProductsAdapter.onProductClickCallback() {
            @Override
            public void onProductClick(String id) {
                Intent productIntent = new Intent(SellerProfileActivity.this, PetProductActivity.class);
                productIntent.putExtra("id_produk", id);
                startActivity(productIntent);
            }
        });
        RecyclerView rvAll = findViewById(R.id.rvShopAll);
        rvAll.setLayoutManager(new GridLayoutManager(this, 2));
        rvAll.setAdapter(reccProductsAdapter);

        viewModel.getAllProducts().observe(this, vms->{
            reccProductsAdapter.setProductVMs(vms);
            reccProductsAdapter.notifyDataSetChanged();
        });

        ShopVoucherAdapter adapter = new ShopVoucherAdapter(new ShopVoucherAdapter.detailVoucher() {
            @Override
            public void showDetail(String id_promo) {
                Intent promoIntent = new Intent(SellerProfileActivity.this, VoucherDetailActivity.class);
                promoIntent.putExtra("id_promo", id_promo);
                startActivity(promoIntent);
            }
        });

        viewModel.getShopPromos().observe(this, vms->{
            adapter.setPromoVMs(vms);
            adapter.notifyDataSetChanged();
        });


        ReccHotelAdapter topHotelAdapter = new ReccHotelAdapter(new ReccHotelAdapter.hotelClickCallback() {
            @Override
            public void onHotelClick(String id_kamar) {
                Intent hotel_intent = new Intent(SellerProfileActivity.this, HotelDetailActivity.class);
                hotel_intent.putExtra("id_kamar", id_kamar);
                startActivity(hotel_intent);
            }
        });

        RecyclerView rvTopHotels = findViewById(R.id.rvSellerRoomTop);
        rvTopHotels.setAdapter(topHotelAdapter);

        viewModel.getTopHotel().observe(this, vms->{
            topHotelAdapter.setHotelVMs(vms);
            topHotelAdapter.notifyDataSetChanged();
        });

        HotelListAdapter allHotelAdapter = new HotelListAdapter(new HotelListAdapter.hotelClickCallback() {
            @Override
            public void onHotelClick(String id_hotel) {
                Intent hotel_intent = new Intent(SellerProfileActivity.this, HotelDetailActivity.class);
                hotel_intent.putExtra("id_kamar", id_hotel);
                startActivity(hotel_intent);
            }
        });

        RecyclerView rvAllHotels = findViewById(R.id.rvSellerRoomAll);
        rvAllHotels.setAdapter(allHotelAdapter);

        viewModel.getAllHotel().observe(this, vms->{
            allHotelAdapter.setHotelVMs(vms);
            allHotelAdapter.notifyDataSetChanged();
        });


        PaketGroomingAdapter groomingAdapter = new PaketGroomingAdapter(new PaketGroomingAdapter.onPaketQtyChanged() {
            @Override
            public void onQtyChanged() {
                viewModel.checkCart(1);
            }
        });

        RecyclerView rvGrooming = findViewById(R.id.rvGroomerPackages);
        rvGrooming.setAdapter(groomingAdapter);

        viewModel.getGroomingPacks().observe(this, vms->{
            groomingAdapter.setPacksVM(vms);
            groomingAdapter.notifyDataSetChanged();
        });


        ImageView ivSellerProfile = findViewById(R.id.ivSellerProfile);
        viewModel.getSellerPic().observe(this, pic->{
            Glide.with(this).load(pic).into(ivSellerProfile);
        });

        viewModel.getSellerPosters().observe(this, pictures->{
            ImageSlider sellerPosterSlide = findViewById(R.id.slideSellerPoster);
            ArrayList<SlideModel> slideModels = new ArrayList<>();
            for (int i = 0; i < pictures.length; i++) {
                if(!pictures[i].equals("")){
                    slideModels.add(new SlideModel(pictures[i], "", ScaleTypes.FIT));
                }
            }
            sellerPosterSlide.setImageList(slideModels);
            sellerPosterSlide.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemSelected(int i) {
                    Intent imageIntent = new Intent(SellerProfileActivity.this, ImageViewerActivity.class);
                    imageIntent.putExtra("url", slideModels.get(i).getImageUrl());
                    startActivity(imageIntent);
                }
            });
        });


        TextInputEditText sbShopProfile = findViewById(R.id.edSearchShopProfile);
        viewModel.getRole().observe(this, role->{
            sbShopProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent searchIntent = new Intent(SellerProfileActivity.this, SearchSellerItemsActivity.class);
                    searchIntent.putExtra("tipe", role);
                    searchIntent.putExtra("hp_seller", getIntent().getStringExtra("hp_seller"));
                    startActivity(searchIntent);
                }
            });
        });

        MaterialButton btnChat = findViewById(R.id.btnChatProfile);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(SellerProfileActivity.this, ChatActivity.class);
                chatIntent.putExtra("lawan_bicara", getIntent().getStringExtra("hp_seller"));
                startActivity(chatIntent);
            }
        });

        RecyclerView rvVouchers = findViewById(R.id.rvVoucherToko);
        rvVouchers.setAdapter(adapter);
        rvVouchers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        hp_seller = getIntent().getStringExtra("hp_seller");

        MaterialButton btnNavigation = findViewById(R.id.btnClinicAddrNavigation);
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+viewModel.getClinicCoors().getValue());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        MaterialButton btnAppointment = findViewById(R.id.btnClinicAppo);
        btnAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAppoIntent = new Intent(SellerProfileActivity.this, AppointmentActivity.class);
                addAppoIntent.putExtra("hp_klinik", getIntent().getStringExtra("hp_seller"));
                startActivityForResult(addAppoIntent, 1);
            }
        });

        ReviewsAdapter revsAdapter = new ReviewsAdapter();
        RecyclerView rvReviews = findViewById(R.id.rvSellerReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(revsAdapter);

        viewModel.getReviews().observe(this, revVMs ->{
            revsAdapter.setRevVMs(revVMs);
            revsAdapter.notifyDataSetChanged();
        });

        TextView seeReviews = findViewById(R.id.sellerProfileSeeReviews);
        seeReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(SellerProfileActivity.this, ReviewListActivity.class);
                discussIntent.putExtra("id_item", getIntent().getStringExtra("hp_seller"));
                discussIntent.putExtra("tipe", 2);
                startActivity(discussIntent);
            }
        });

        TabLayout tabLayoutReview = findViewById(R.id.tabsSellerReviews);
        tabLayoutReview.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.countScoreGroomerClinic(getIntent().getStringExtra("hp_seller"), tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void btnFollowClick(View v){
        viewModel.changeFollow(getIntent().getStringExtra("hp_seller"));
    }

    private void changeMenuIntent(int id){
        if(id == R.id.headerProductCart){
            Intent cartIntent = new Intent(SellerProfileActivity.this, CartActivity.class);
            cartIntent.putExtra("tipe", viewModel.getRole().getValue());
            startActivity(cartIntent);
        }
        else if(id == R.id.headerProductAkun){
            Intent mainIntent = new Intent(SellerProfileActivity.this, MainActivity.class);
            mainIntent.putExtra("jumpTo", 3);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductChat){
            Intent mainIntent = new Intent(SellerProfileActivity.this, ConversationsActivity.class);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductFav){
            Intent favIntent = new Intent(SellerProfileActivity.this, FollowActivity.class);
            favIntent.putExtra("tipe", viewModel.getRole().getValue());
            startActivity(favIntent);
        }
        else if(id == R.id.headerProductHome){
            Intent homeIntent = new Intent(SellerProfileActivity.this, MainActivity.class);
            startActivity(homeIntent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getSellerDetail(hp_seller);
    }
}