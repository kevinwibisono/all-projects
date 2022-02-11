package com.example.sellerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.sellerapp.databinding.BottomDialogVerificationBinding;
import com.example.sellerapp.viewmodels.RegisterViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class VerificationBottomDialogFragment extends BottomSheetDialogFragment {
    private RegisterViewModel viewModel;

    public void setViewModel(RegisterViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogVerificationBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_verification, container,
                false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
