package com.example.sellerapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sellerapp.adapters.OrderAdapter;
import com.example.sellerapp.databinding.FragmentOrderListBinding;
import com.example.sellerapp.viewmodels.OrderFragmentViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class OrderListFragment extends Fragment {

    private OrderFragmentViewModel viewModel;
    private int lastStatus = -1;

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new OrderFragmentViewModel(getActivity().getApplication());

        FragmentOrderListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ChipGroup statuses = view.findViewById(R.id.chipsOrderStatus);

        viewModel.getStatuses().observe(this, statusesStr->{
            statuses.removeAllViews();
            for (int i=0; i<statusesStr.length; i++){
                statuses.addView(makeNewChip(statusesStr[i], i));
            }
        });

        statuses.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                lastStatus = checkedId;
                viewModel.getOrdersWithStatus(checkedId);
            }
        });

        OrderAdapter adapter = new OrderAdapter(new OrderAdapter.onShowOrderDetail() {
            @Override
            public void onShow(String id_pj, int jenis_pj) {
                Intent detailIntent = new Intent();
                if(jenis_pj < 3){
                    detailIntent = new Intent(getContext(), OrderDetailActivity.class);
                }
                else{
                    detailIntent = new Intent(getContext(), AppointmentDetailActivity.class);
                }
                detailIntent.putExtra("id_pj", id_pj);
                startActivity(detailIntent);
            }
        });

        RecyclerView rvList = view.findViewById(R.id.rvOrderList);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(adapter);

        viewModel.getOrderVMs().observe(this, vms->{
            adapter.setOrders(vms);
            adapter.notifyDataSetChanged();
        });

        SwipeRefreshLayout pageOrdersFragment = view.findViewById(R.id.swipeRefreshOrderFragment);
        pageOrdersFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(lastStatus < 0){
                    viewModel.getOrdersBeginning();
                }
                else{
                    viewModel.getOrdersWithStatus(lastStatus);
                }
                pageOrdersFragment.setRefreshing(false);
            }
        });

    }

    private Chip makeNewChip(String text, int pos){
        Chip newVariant = new Chip(getActivity());
        newVariant.setId(pos);
        newVariant.setCheckable(true);
        newVariant.setText(text);
        newVariant.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
        newVariant.setChipStrokeWidth(3);
        return newVariant;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(lastStatus < 0){
            viewModel.getOrdersBeginning();
        }
        else{
            viewModel.getOrdersWithStatus(lastStatus);
        }
    }
}