package com.example.sellerapp;

import android.content.Context;
import android.content.Intent;
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

import com.example.sellerapp.adapters.ImportantHotelAdapter;
import com.example.sellerapp.databinding.FragmentHotelHomeBinding;
import com.example.sellerapp.databinding.FragmentProductListBinding;
import com.example.sellerapp.interfaces.switchToChat;
import com.example.sellerapp.interfaces.switchToOrder;
import com.example.sellerapp.viewmodels.HotelHomeViewModel;
import com.google.android.material.card.MaterialCardView;

public class HotelHomeFragment extends Fragment {

    private switchToOrder switchOrder;
    private switchToChat switchChat;
    private HotelHomeViewModel viewModel;

    public HotelHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new HotelHomeViewModel(getActivity().getApplication());

        FragmentHotelHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hotel_home, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        switchOrder = null;
        switchChat = null;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout fragmentHotelHome = view.findViewById(R.id.swipeRefreshHotelHome);
        fragmentHotelHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.arrangeImportantThings();
                fragmentHotelHome.setRefreshing(false);
            }
        });

        ImportantHotelAdapter adapter = new ImportantHotelAdapter(new ImportantHotelAdapter.onImportantHotelClicked() {
            @Override
            public void onClicked(String id_hotel) {
                Intent updateIntent = new Intent(getContext(), HotelActivity.class);
                updateIntent.putExtra("id_kamar", id_hotel);
                startActivity(updateIntent);
            }
        });

        RecyclerView rvRooms = view.findViewById(R.id.rvImportantRooms);
        rvRooms.setAdapter(adapter);
        rvRooms.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        viewModel.getHotelVMs().observe(this, vms ->{
            adapter.setHotelVMs(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.arrangeImportantThings();

        MaterialCardView[] importantStuff = {view.findViewById(R.id.cardHotelNew), view.findViewById(R.id.cardHotelScheduled), view.findViewById(R.id.cardHotelActive),
                view.findViewById(R.id.cardHotelChat), view.findViewById(R.id.cardHotelDiscuss), view.findViewById(R.id.cardHotelReview)};

        for (int i = 0; i < 6; i++) {
            final int index = i;
            importantStuff[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(index < 3){
                        switchOrder.showOrders();
                    }
                    else if(index == 3){
                        switchChat.showChats();
                    }
                    else if(index == 4){
                        startActivity(new Intent(getActivity(), DiscussListActivity.class));
                    }
                    else{
                        startActivity(new Intent(getActivity(), ReturnReviewsActivity.class));
                    }
                }
            });
        }
    }
}