package com.example.sellerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.example.sellerapp.databinding.BottomDialogHotelBinding;
import com.example.sellerapp.databinding.BottomDialogProductBinding;
import com.example.sellerapp.viewmodels.HotelListViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class HotelBottomDialogFragment extends BottomSheetDialogFragment {
    private HotelListViewModel viewModel;

    public void setViewModel(HotelListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogHotelBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_hotel, container,
                false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnApply = view.findViewById(R.id.btnBtmHotel);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.changeFilterSort();
                HotelBottomDialogFragment.this.dismiss();
            }
        });
    }

    @BindingAdapter("android:checked")
    public static void setChecked(CompoundButton chk, boolean checked){
        chk.setChecked(checked);
    }
}
