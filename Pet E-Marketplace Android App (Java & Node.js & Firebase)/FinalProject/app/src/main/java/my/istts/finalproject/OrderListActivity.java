package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.OrderAdapter;

import my.istts.finalproject.databinding.ActivityOrderListBinding;
import my.istts.finalproject.viewmodels.OrderListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

public class OrderListActivity extends AppCompatActivity {

    private int selectedType = 0;
    private int selectedStatus = 0;
    private OrderListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new OrderListViewModel(getApplication());
        ActivityOrderListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbOrders = findViewById(R.id.tbOrders);
        tbOrders.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        OrderAdapter adapter = new OrderAdapter(new OrderAdapter.showDetailOrder() {
            @Override
            public void show(String id_pj, int jenis) {
                Intent detailIntent = new Intent();
                if(jenis < 3){
                    detailIntent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                }
                else{
                    detailIntent = new Intent(OrderListActivity.this, AppointmentDetailActivity.class);
                }
                detailIntent.putExtra("id_pj", id_pj);
                startActivity(detailIntent);
            }
        });

        RecyclerView rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setAdapter(adapter);

        viewModel.getOrderVMs().observe(this, vms ->{
            adapter.setOrderVMs(vms);
            adapter.notifyDataSetChanged();
        });

        selectedType = getIntent().getIntExtra("tipe", 0);
        viewModel.getOrders(selectedType, 0);
        viewModel.setStatuses(0);

        TabLayout tabs = findViewById(R.id.tabsOrderType);
        TabLayout.Tab typeTab = tabs.getTabAt(selectedType);
        tabs.selectTab(typeTab);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedType = tab.getPosition();
                viewModel.getOrders(tab.getPosition(), 0);
                viewModel.setStatuses(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        ChipGroup statusChips = findViewById(R.id.chipsOrderStatus);
        viewModel.getStatuses().observe(this, statuses->{
            statusChips.removeAllViews();
            for (int i=0; i<statuses.length; i++){
                Chip statusChip = new Chip(OrderListActivity.this);
                statusChip.setCheckable(true);
                statusChip.setText(statuses[i]);
                statusChip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
                statusChip.setChipStrokeWidth(3);
                statusChip.setId(i);

                statusChips.addView(statusChip);
            }
            statusChips.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, int checkedId) {
                    selectedStatus = checkedId;
                    viewModel.getOrders(selectedType, checkedId);
                }
            });
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getOrders(selectedType, selectedStatus);
    }
}