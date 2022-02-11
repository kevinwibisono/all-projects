package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.SellerAdapter;

import my.istts.finalproject.databinding.ActivityFollowBinding;
import my.istts.finalproject.viewmodels.FollowViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class FollowActivity extends AppCompatActivity {

    private int tipe = 0;
    private FollowViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new FollowViewModel(getApplication());
        ActivityFollowBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_follow);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        tipe = getIntent().getIntExtra("tipe", 0);

        MaterialToolbar tbFollow = findViewById(R.id.tbFollow);
        tbFollow.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SellerAdapter sellerAdapter = new SellerAdapter(new SellerAdapter.sellerItemInterface() {
            @Override
            public void onSellerClicked(String hp_seller) {
                Intent clinicDetailntent = new Intent(FollowActivity.this, SellerProfileActivity.class);
                clinicDetailntent.putExtra("hp_seller", hp_seller);
                startActivity(clinicDetailntent);
            }

            @Override
            public void onSellerFavClicked(String hp_seller) {
                viewModel.unfollowSeller(hp_seller, tipe);
            }
        });

        viewModel.getFollowedSellers().observe(this, sellers->{
            sellerAdapter.setSellerVMs(sellers);
            sellerAdapter.notifyDataSetChanged();
        });

        RecyclerView rvFollow = findViewById(R.id.rvFollow);
        rvFollow.setAdapter(sellerAdapter);

        TabLayout tabsFollow = findViewById(R.id.tabsFollow);
        TabLayout.Tab selectedTab = tabsFollow.getTabAt(tipe);
        tabsFollow.selectTab(selectedTab);

        tabsFollow.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tipe = tab.getPosition();
                viewModel.getFollowed(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getFollowed(tipe);
    }
}