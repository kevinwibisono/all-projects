package com.example.sellerapp;

import android.app.ProgressDialog;
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
import com.example.sellerapp.databinding.FragmentAccountShopBinding;
import com.example.sellerapp.interfaces.switchToChat;
import com.example.sellerapp.interfaces.switchToOrder;
import com.example.sellerapp.viewmodels.ShopAccountViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ShopAccountFragment extends Fragment {

    private switchToOrder switchOrder;
    private switchToChat switchChat;
    private ShopAccountViewModel viewModel;

    public ShopAccountFragment() {
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
        viewModel = new ShopAccountViewModel(getActivity().getApplication());

        FragmentAccountShopBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_shop, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressDialog loadScreen = new ProgressDialog(getContext());
        loadScreen.setCancelable(false);
        viewModel.isDoneLogout().observe(this, done -> {
            if(!done){
                loadScreen.setTitle("Sedang Diproses....");
                loadScreen.show();
            }
            else{
                loadScreen.dismiss();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        viewModel.getSellerPic().observe(this, pic -> {
            ImageView ivShopPic = view.findViewById(R.id.ivAccountShop);
            Glide.with(getContext()).load(pic).into(ivShopPic);
        });

        viewModel.getSellerDetail();

        MaterialToolbar tbAccount = view.findViewById(R.id.tbAccount);
        tbAccount.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuSetting){
                    startActivity(new Intent(getContext(), SellerDetailActivity.class));
                }
                return false;
            }
        });

        LinearLayout saldo = view.findViewById(R.id.accountSaldo);
        saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent saldoIntent = new Intent(getActivity(), SaldoActivity.class);
                startActivity(saldoIntent);
            }
        });

        LinearLayout vouchers = view.findViewById(R.id.accountVoucher);
        vouchers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent voucherIntent = new Intent(getActivity(), ListPromoActivity.class);
                startActivity(voucherIntent);
            }
        });

        MaterialButton btnLogout = view.findViewById(R.id.btnLogoutShop);
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

        LinearLayout[] homeCards = {view.findViewById(R.id.partShopNew), view.findViewById(R.id.partShopDeliver),
                view.findViewById(R.id.partShopPickup), view.findViewById(R.id.partShopComplain), view.findViewById(R.id.partShopChat),
                view.findViewById(R.id.partShopDiscuss), view.findViewById(R.id.partShopReview)};

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