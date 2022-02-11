package com.example.sellerapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.sellerapp.adapters.HotelAdapter;
import com.example.sellerapp.databinding.FragmentHotelListBinding;
import com.example.sellerapp.viewmodels.HotelListViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class HotelListFragment extends Fragment {

    private HotelListViewModel viewModel;

    public HotelListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new HotelListViewModel(getActivity().getApplication());

        FragmentHotelListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hotel_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HotelAdapter adapter = new HotelAdapter(new HotelAdapter.showRoomDetail() {
            @Override
            public void roomDetail(String id) {
                Intent detailIntent = new Intent(getContext(), HotelActivity.class);
                detailIntent.putExtra("id_kamar", id);
                startActivity(detailIntent);
            }
        }, new HotelAdapter.showRoomPreview() {
            @Override
            public void roomPreview(String id) {
                Intent previewIntent = new Intent(getContext(), HotelPreviewActivity.class);
                previewIntent.putExtra("id_kamar", id);
                startActivity(previewIntent);
            }
        });

        RecyclerView rvRoom = view.findViewById(R.id.rvHotelList);
        rvRoom.setAdapter(adapter);

        viewModel.getHotelVMs().observe(this, vms->{
            adapter.setHotelVMs(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getHotelRooms();

        MaterialButton btnAdd = view.findViewById(R.id.btnToAddHotel);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(getActivity(), HotelActivity.class);
                startActivity(addIntent);
            }
        });

        LinearLayout filterSort = view.findViewById(R.id.filterSortHotel);
        filterSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HotelBottomDialogFragment fragment = new HotelBottomDialogFragment();
                fragment.setViewModel(viewModel);
                fragment.show(getActivity().getSupportFragmentManager(), "");
            }
        });

        ChipGroup cgFacs = view.findViewById(R.id.chipGroupFilterFacs);
        viewModel.getFacsActive().observe(this, facilities -> {
            cgFacs.removeAllViews();
            for (int i = 0; i < facilities.size(); i++) {
                final int index = i;
                Chip newVariantChip = new Chip(getContext());

                newVariantChip.requestLayout();
                newVariantChip.setText(facilities.get(i));
                newVariantChip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
                newVariantChip.setChipStrokeWidth(3);
                newVariantChip.setCloseIconVisible(true);
                newVariantChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewModel.deleteFacsFilter(index);
                        viewModel.getHotelRooms();
                    }
                });


                cgFacs.addView(newVariantChip);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) newVariantChip.getLayoutParams();
                layoutParams.setMargins(0, 0, 5, 0);
            }
        });
    }
}