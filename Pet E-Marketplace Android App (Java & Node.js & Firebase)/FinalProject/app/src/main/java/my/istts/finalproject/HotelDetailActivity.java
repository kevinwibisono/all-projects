package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ReviewsAdapter;
import my.istts.finalproject.adapters.ReccHotelAdapter;
import my.istts.finalproject.databinding.ActivityHotelDetailBinding;
import my.istts.finalproject.viewmodels.HotelDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class HotelDetailActivity extends AppCompatActivity {

    private HotelDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new HotelDetailViewModel(getApplication());
        ActivityHotelDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_hotel_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.setKamar(getIntent().getStringExtra("id_kamar"));

        viewModel.getPictures().observe(this, pictures->{
            ImageSlider hotelPicSlider = findViewById(R.id.sliderHotelDetail);
            ArrayList<SlideModel> slideModels = new ArrayList<>();
            for (int i = 0; i < pictures.length; i++) {
                if(!pictures[i].equals("")){
                    slideModels.add(new SlideModel(pictures[i], "", ScaleTypes.FIT));
                }
            }
            hotelPicSlider.setImageList(slideModels);
            hotelPicSlider.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemSelected(int i) {
                    Intent imageIntent = new Intent(HotelDetailActivity.this, ImageViewerActivity.class);
                    imageIntent.putExtra("url", slideModels.get(i).getImageUrl());
                    startActivity(imageIntent);
                }
            });
        });

        TextInputEditText sbHotel = findViewById(R.id.edSearchHotel);
        sbHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(HotelDetailActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        LinearLayout partOwner = findViewById(R.id.partHotelOwner);
        viewModel.getEmailHotel().observe(this, hpHotel->{
            partOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sellerIntent = new Intent(HotelDetailActivity.this, SellerProfileActivity.class);
                    sellerIntent.putExtra("hp_seller", hpHotel);
                    startActivity(sellerIntent);
                }
            });
        });


        MaterialToolbar tbHotel = findViewById(R.id.tbHotelDetail);
        tbHotel.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tbHotel.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeMenuIntent(item.getItemId());
                return false;
            }
        });

        MenuItem itemCart = tbHotel.getMenu().findItem(R.id.headerProductCart);
        View actionView = itemCart.getActionView();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenuIntent(R.id.headerProductCart);
            }
        });
        TextView textCartItemCount = (TextView) actionView.findViewById(R.id.icon_cart_badge);

        viewModel.isItemsInCart().observe(this, itemsInCart->{
            textCartItemCount.setVisibility(View.VISIBLE);
        });


        ReccHotelAdapter adapter = new ReccHotelAdapter(new ReccHotelAdapter.hotelClickCallback() {
            @Override
            public void onHotelClick(String id) {
                Intent hotelIntent = new Intent(HotelDetailActivity.this, HotelDetailActivity.class);
                hotelIntent.putExtra("id_kamar", id);
                startActivity(hotelIntent);
            }
        });
        RecyclerView rvHotelRooms = findViewById(R.id.rvHotelRooms);
        rvHotelRooms.setAdapter(adapter);

        viewModel.getHotelRooms().observe(this, hotelVMs ->{
            adapter.setHotelVMs(hotelVMs);
            adapter.notifyDataSetChanged();
        });


        ReccHotelAdapter reccAdapter = new ReccHotelAdapter(new ReccHotelAdapter.hotelClickCallback() {
            @Override
            public void onHotelClick(String id) {
                Intent hotelIntent = new Intent(HotelDetailActivity.this, HotelDetailActivity.class);
                hotelIntent.putExtra("id_kamar", id);
                startActivity(hotelIntent);
            }
        });
        RecyclerView rvReccRooms = findViewById(R.id.rvReccRooms);
        rvReccRooms.setAdapter(reccAdapter);

        viewModel.getReccRooms().observe(this, reccVMs ->{
            reccAdapter.setHotelVMs(reccVMs);
            reccAdapter.notifyDataSetChanged();
        });

        ReviewsAdapter revAdapter = new ReviewsAdapter();
        RecyclerView rvComments = findViewById(R.id.rvHotelReviews);
        rvComments.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvComments.setAdapter(revAdapter);

        viewModel.getReviews().observe(this, revVMs ->{
            revAdapter.setRevVMs(revVMs);
            revAdapter.notifyDataSetChanged();
        });

        TextView seeDiscuss = findViewById(R.id.petHotelSeeDiscuss);
        seeDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(HotelDetailActivity.this, FullDiscussionActivity.class);
                discussIntent.putExtra("id_kamar", getIntent().getStringExtra("id_kamar"));
                startActivity(discussIntent);
            }
        });

        TextView seeReviews = findViewById(R.id.petHotelSeeReviews);
        seeReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussIntent = new Intent(HotelDetailActivity.this, ReviewListActivity.class);
                discussIntent.putExtra("id_item", getIntent().getStringExtra("id_kamar"));
                discussIntent.putExtra("tipe", 1);
                startActivity(discussIntent);
            }
        });

        ImageView owner = findViewById(R.id.ivHotelOwner);
        viewModel.getOwnerPic().observe(this, picture -> {
            Glide.with(this).load(picture).into(owner);
        });

        ImageView[] stars = {findViewById(R.id.starHotelDetail1), findViewById(R.id.starHotelDetail2), findViewById(R.id.starHotelDetail3),
                findViewById(R.id.starHotelDetail4), findViewById(R.id.starHotelDetail5)};
        viewModel.getScore().observe(this, score -> {
            if(!score.equals("")){
                float numberScore = Float.parseFloat(score);
                for (int i = 0; i < 5; i++) {
                    //karena index dimulai 0, sedangkan skor dimulai dari 1, maka saat membanginkan tambah index(i) dgn 1
                    if((i+1) <= numberScore){
                        stars[i].setImageResource(R.drawable.ic_baseline_star_20);
                    }
                    else{
                        float diff = numberScore - (i);
                        stars[i].setImageResource(determineStar(diff));
                    }
                }
            }
        });


        MaterialButton btnChat = findViewById(R.id.btnHotelChat);
        MaterialButton btnEmptyChat = findViewById(R.id.btnHotelEmptyChat);
        viewModel.getEmailHotel().observe(this, hpHotel->{
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toChat(hpHotel);
                }
            });
            btnEmptyChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toChat(hpHotel);
                }
            });
        });


        TabLayout tabLayoutReview = findViewById(R.id.tabsHotelDetail);
        tabLayoutReview.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.getHotelReviews(getIntent().getStringExtra("id_kamar"), tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        BottomAppBar btmBarHotel = findViewById(R.id.btmBarHotelDetail);
        Snackbar snackbar = Snackbar.make(btmBarHotel, "", Snackbar.LENGTH_LONG);

        viewModel.getToastMsg().observe(this, snackbar::setText);

        viewModel.getError().observe(this, error -> {
            if(error){
                snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_light))
                        .setAction("", null);
            }
            else{
                snackbar.setBackgroundTint(getResources().getColor(android.R.color.black));
                snackbar.setAction("Lihat", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent cartIntent = new Intent(HotelDetailActivity.this, CartActivity.class);
                        cartIntent.putExtra("tipe", 2);
                        startActivity(cartIntent);
                    }
                }).setActionTextColor(Color.WHITE);
            }
            snackbar.setAnchorView(btmBarHotel);
            snackbar.show();
        });
    }

    private int determineStar(float diff){
        if(diff < 0.2){
            return R.drawable.ic_baseline_star_border_20;
        }
        else if(diff > 0.1 && diff < 0.9){
            return R.drawable.ic_baseline_star_half_20;
        }
        else{
            return R.drawable.ic_baseline_star_20;
        }
    }


    private void toChat(String hpHotel){
        Intent chatIntent = new Intent(HotelDetailActivity.this, ChatActivity.class);
        chatIntent.putExtra("lawan_bicara", hpHotel);
        chatIntent.putExtra("id_item", getIntent().getStringExtra("id_kamar"));
        chatIntent.putExtra("tipe", 2);
        startActivity(chatIntent);
    }

    private void changeMenuIntent(int id){
        if(id == R.id.headerProductAkun){
            Intent mainIntent = new Intent(HotelDetailActivity.this, MainActivity.class);
            mainIntent.putExtra("jumpTo", 3);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductChat){
            Intent mainIntent = new Intent(HotelDetailActivity.this, ConversationsActivity.class);
            startActivity(mainIntent);
        }
        else if(id == R.id.headerProductFav){
            Intent favIntent = new Intent(HotelDetailActivity.this, FavoriteActivity.class);
            favIntent.putExtra("tipe", 1);
            startActivity(favIntent);
        }
        else if(id == R.id.headerProductHome){
            Intent homeIntent = new Intent(HotelDetailActivity.this, MainActivity.class);
            startActivity(homeIntent);
        }
        else if(id == R.id.headerProductCart){
            Intent cartIntent = new Intent(HotelDetailActivity.this, CartActivity.class);
            cartIntent.putExtra("tipe", 2);
            startActivity(cartIntent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}