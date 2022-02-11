package com.example.sellerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sellerapp.adapters.ImportantAdapter;
import com.example.sellerapp.databinding.FragmentShopHomeBinding;
import com.example.sellerapp.viewmodels.ShopHomeViewModel;
import com.google.android.material.card.MaterialCardView;
import com.example.sellerapp.interfaces.switchToOrder;
import com.example.sellerapp.interfaces.switchToChat;

public class ShopHomeFragment extends Fragment {

    private switchToOrder switchOrder;
    private switchToChat switchChat;
    private ShopHomeViewModel viewModel;

    public ShopHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ShopHomeViewModel.class);

        FragmentShopHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop_home, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout fragmentShopHome = view.findViewById(R.id.swipeRefreshShopHome);
        fragmentShopHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.arrangeImportantThings();
                fragmentShopHome.setRefreshing(false);
            }
        });

        ImportantAdapter adapter = new ImportantAdapter(new ImportantAdapter.onImportantItemClicked() {
            @Override
            public void onClicked(String id_product) {
                Intent updateIntent = new Intent(getContext(), ProductActivity.class);
                updateIntent.putExtra("id_produk", id_product);
                startActivity(updateIntent);
            }
        });

        RecyclerView rvProducts = view.findViewById(R.id.rvImportantProducts);
        rvProducts.setAdapter(adapter);
        rvProducts.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        viewModel.getProductVMs().observe(this, problemProds -> {
            adapter.setProdVMs(problemProds);
            adapter.notifyDataSetChanged();
        });

        viewModel.arrangeImportantThings();

        MaterialCardView[] homeCards = {view.findViewById(R.id.cardShopNew), view.findViewById(R.id.cardShopDeliver),
                view.findViewById(R.id.cardShopPickup), view.findViewById(R.id.cardShopComplaints), view.findViewById(R.id.cardShopChat),
                view.findViewById(R.id.cardShopDiskusi), view.findViewById(R.id.cardShopUlasan)};

        for (int i=0;i<homeCards.length;i++){
            final int index = i;
            homeCards[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(index < 3){
                        switchOrder.showOrders();
                    }
                    else if(index == 4){
                        switchChat.showChats();
                    }
                    else{
                        moveIntent(index);
                    }
                }
            });

        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof switchToOrder && context instanceof switchToChat) {
            switchOrder = (switchToOrder) context;
            switchChat = (switchToChat) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement the interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        switchChat = null;
        switchOrder = null;
    }

    public void moveIntent(int idx){
        if(idx == 3){
            startActivity(new Intent(getActivity(), KomplainListActivity.class));
        }
        else if(idx == 5){
            startActivity(new Intent(getActivity(), DiscussListActivity.class));
        }
        else if(idx == 6){
            startActivity(new Intent(getActivity(), ReturnReviewsActivity.class));
        }
    }
}