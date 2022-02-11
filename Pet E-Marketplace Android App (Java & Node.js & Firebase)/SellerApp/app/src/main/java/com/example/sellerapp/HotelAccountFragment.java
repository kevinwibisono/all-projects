package com.example.sellerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.sellerapp.databinding.FragmentHotelAccountBinding;
import com.example.sellerapp.interfaces.switchToChat;
import com.example.sellerapp.interfaces.switchToOrder;
import com.example.sellerapp.viewmodels.HotelAccountViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HotelAccountFragment extends Fragment {

    private switchToOrder switchOrder;
    private switchToChat switchChat;
    private HotelAccountViewModel viewModel;

    public HotelAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new HotelAccountViewModel(getActivity().getApplication());

        FragmentHotelAccountBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hotel_account, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.arrangeImportantThings();

        viewModel.isLoggedOut().observe(this, out -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        ImageView logoHotel = view.findViewById(R.id.ivHotelAccount);
        viewModel.getHotelPic().observe(this, pic ->{
            Glide.with(getContext()).load(pic).into(logoHotel);
        });

        MaterialToolbar tbHotel = view.findViewById(R.id.tbHotelAccount);
        tbHotel.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuSetting){
                    startActivity(new Intent(getActivity(), SellerDetailActivity.class));
                }
                return true;
            }
        });

        LinearLayout[] parts = {view.findViewById(R.id.partHotelNew), view.findViewById(R.id.partHotelScheduled), view.findViewById(R.id.partHotelActive),
                view.findViewById(R.id.partHotelChat), view.findViewById(R.id.partHotelDiscuss), view.findViewById(R.id.partHotelReview)};

        for (int i = 0; i < 6; i++) {
            final int index = i;
            parts[i].setOnClickListener(new View.OnClickListener() {
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

        LinearLayout saldo = view.findViewById(R.id.partHotelSaldo);
        saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SaldoActivity.class));
            }
        });

        MaterialButton btnLogout = view.findViewById(R.id.btnHotelLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Perhatian!")
                        .setMessage("Apakah anda yakin akan keluar?")
                        .setNegativeButton("Tidak", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            viewModel.logout();
                        })
                        .show();
            }
        });
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
}