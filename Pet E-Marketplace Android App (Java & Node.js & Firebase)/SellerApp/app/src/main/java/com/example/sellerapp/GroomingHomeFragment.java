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

import com.example.sellerapp.adapters.BookingGroomingAdapter;
import com.example.sellerapp.databinding.FragmentClinicHomeBinding;
import com.example.sellerapp.databinding.FragmentGroomingHomeBinding;
import com.example.sellerapp.viewmodels.GroomingHomeViewModel;

public class GroomingHomeFragment extends Fragment {

    private GroomingHomeViewModel viewModel;

    public GroomingHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new GroomingHomeViewModel(getActivity().getApplication());

        FragmentGroomingHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_grooming_home, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout fragmentGroomingHome = view.findViewById(R.id.swipeRefreshGroomHome);
        fragmentGroomingHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getGroomerDetails();
                fragmentGroomingHome.setRefreshing(false);
            }
        });

        BookingGroomingAdapter adapter = new BookingGroomingAdapter(new BookingGroomingAdapter.onGroomingClicked() {
            @Override
            public void onClicked(String id_pj) {
                Intent appoDetail = new Intent(getContext(), OrderDetailActivity.class);
                appoDetail.putExtra("id_pj", id_pj);
                startActivity(appoDetail);
            }
        });

        RecyclerView rvGroomings = view.findViewById(R.id.rvGroomings);
        rvGroomings.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvGroomings.setAdapter(adapter);

        viewModel.getActiveGroomings().observe(this, groomings->{
            adapter.setGroomings(groomings);
            adapter.notifyDataSetChanged();
        });

        viewModel.getGroomerDetails();
    }
}