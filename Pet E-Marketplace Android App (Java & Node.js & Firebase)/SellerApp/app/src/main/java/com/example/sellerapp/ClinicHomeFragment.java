package com.example.sellerapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sellerapp.adapters.HeadlineApposAdapter;
import com.example.sellerapp.databinding.FragmentClinicHomeBinding;
import com.example.sellerapp.databinding.FragmentConversationsBinding;
import com.example.sellerapp.viewmodels.ClinicHomeViewModel;

public class ClinicHomeFragment extends Fragment {

    private ClinicHomeViewModel viewModel;
    public ClinicHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new ClinicHomeViewModel(getActivity().getApplication());

        FragmentClinicHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clinic_home, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout fragmentClinicHome = view.findViewById(R.id.swipeRefreshClinicHome);
        fragmentClinicHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getClinicDetails();
                fragmentClinicHome.setRefreshing(false);
            }
        });

        HeadlineApposAdapter adapter = new HeadlineApposAdapter(new HeadlineApposAdapter.onHeadlineAppoClicked() {
            @Override
            public void onAppoClicked(String id_pj) {
                Intent appoDetail = new Intent(getContext(), AppointmentDetailActivity.class);
                appoDetail.putExtra("id_pj", id_pj);
                startActivity(appoDetail);
            }
        });

        RecyclerView rvAppos = view.findViewById(R.id.rvAppos);
        rvAppos.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvAppos.setAdapter(adapter);

        viewModel.getActiveAppos().observe(this, appos->{
            adapter.setAppos(appos);
            adapter.notifyDataSetChanged();
        });

        viewModel.getClinicDetails();

    }
}