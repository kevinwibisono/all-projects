package com.example.sellerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.sellerapp.adapters.HeadlineApposAdapter;
import com.example.sellerapp.databinding.FragmentClinicAccountBinding;
import com.example.sellerapp.databinding.FragmentHotelAccountBinding;
import com.example.sellerapp.interfaces.switchToChat;
import com.example.sellerapp.interfaces.switchToOrder;
import com.example.sellerapp.viewmodels.ClinicAccountViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ClinicAccountFragment extends Fragment {

    private ClinicAccountViewModel viewModel;
    private switchToChat switchChat;

    public ClinicAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new ClinicAccountViewModel(getActivity().getApplication());

        FragmentClinicAccountBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clinic_account, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HeadlineApposAdapter adapter = new HeadlineApposAdapter(new HeadlineApposAdapter.onHeadlineAppoClicked() {
            @Override
            public void onAppoClicked(String id_pj) {
                Intent appoDetail = new Intent(getContext(), AppointmentDetailActivity.class);
                appoDetail.putExtra("id_pj", id_pj);
                startActivity(appoDetail);
            }
        });

        RecyclerView rvToday = view.findViewById(R.id.rvAccountAppos);
        rvToday.setAdapter(adapter);
        rvToday.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        viewModel.getActiveAppos().observe(this, appos->{
            adapter.setAppos(appos);
            adapter.notifyDataSetChanged();
        });

        viewModel.getClinicDetails();

        ImageView ivClinic = view.findViewById(R.id.ivClinicAccount);
        viewModel.getSellerPic().observe(this, pic->{
            Glide.with(this).load(pic).into(ivClinic);
        });

        MaterialToolbar tbAccount = view.findViewById(R.id.tbClinicAccount);
        tbAccount.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuSetting){
                    Intent settingIntent = new Intent(getActivity(), SellerDetailActivity.class);
                    startActivity(settingIntent);
                }
                return true;
            }
        });

        MaterialButton btnLogout = view.findViewById(R.id.btnClinicLogout);
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

        viewModel.isDoneLogout().observe(this, out -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        LinearLayout partChat = view.findViewById(R.id.partClinicChat);
        partChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchChat.showChats();
            }
        });

        LinearLayout partReview = view.findViewById(R.id.partClinicReview);
        partReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ReturnReviewsActivity.class));
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof switchToOrder && context instanceof switchToChat) {
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
    }
}