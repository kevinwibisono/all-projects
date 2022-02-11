package my.istts.finalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.OrderAdapter;

import my.istts.finalproject.databinding.FragmentOrdersBinding;
import my.istts.finalproject.viewmodels.OrderFragmentViewModel;
import com.google.android.material.chip.ChipGroup;

public class OrdersFragment extends Fragment {
    private OrderFragmentViewModel viewModel;
    private int lastStatus = -1;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new OrderFragmentViewModel(getActivity().getApplication());

        FragmentOrdersBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout pageOrdersFragment = view.findViewById(R.id.pageActiveOrders);
        pageOrdersFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(lastStatus < 0){
                    viewModel.getActiveOrders();
                }
                else{
                    viewModel.getActiveOrdersByType(lastStatus);
                }
                pageOrdersFragment.setRefreshing(false);
            }
        });

        OrderAdapter adapter = new OrderAdapter(new OrderAdapter.showDetailOrder() {
            @Override
            public void show(String id_pj, int jenis) {
                Intent detailIntent = new Intent();
                if(jenis < 3){
                    detailIntent = new Intent(getContext(), OrderDetailActivity.class);
                }
                else{
                    detailIntent = new Intent(getContext(), AppointmentDetailActivity.class);
                }
                detailIntent.putExtra("id_pj", id_pj);
                startActivity(detailIntent);
            }
        });

        RecyclerView rvActiveOrders = view.findViewById(R.id.rvActiveOrders);
        rvActiveOrders.setAdapter(adapter);

        viewModel.getOrderVMs().observe(this, vms->{
            adapter.setOrderVMs(vms);
            adapter.notifyDataSetChanged();
        });

        TextView tvAllOrder = view.findViewById(R.id.tvAllOrder);
        tvAllOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent allIntent = new Intent(getActivity(), OrderListActivity.class);
                startActivity(allIntent);
            }
        });

        LinearLayout partPayment = view.findViewById(R.id.partOrderPayments);
        partPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent paymentActivity = new Intent(getActivity(), UnfinishedPaymentListActivity.class);
                startActivity(paymentActivity);
            }
        });

        ChipGroup cgType = view.findViewById(R.id.cgActiveOrders);
        cgType.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                checkChipId(checkedId);
            }
        });
    }

    private void checkChipId(int id){
        if(id == R.id.chipActiveOrdersShop){
            lastStatus = 0;
            viewModel.getActiveOrdersByType(0);
        }
        else if(id == R.id.chipActiveOrderGroom){
            lastStatus = 1;
            viewModel.getActiveOrdersByType(1);
        }
        else if(id == R.id.chipActiveOrdersHotel){
            lastStatus = 2;
            viewModel.getActiveOrdersByType(2);
        }
        else if(id == R.id.chipActiveOrdersClinic){
            lastStatus = 3;
            viewModel.getActiveOrdersByType(3);
        }
        else{
            viewModel.getActiveOrders();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(lastStatus < 0){
            viewModel.getActiveOrders();
        }
        else{
            viewModel.getActiveOrdersByType(lastStatus);
        }
    }
}