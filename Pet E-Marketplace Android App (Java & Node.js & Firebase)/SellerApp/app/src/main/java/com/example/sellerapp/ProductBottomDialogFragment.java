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

import com.example.sellerapp.databinding.BottomDialogProductBinding;
import com.example.sellerapp.viewmodels.ProductListViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class ProductBottomDialogFragment extends BottomSheetDialogFragment {
    private ProductListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        viewModel = new ViewModelProvider(requireActivity()).get(ShopHomeViewModel.class);
        BottomDialogProductBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_product, container,
                false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnApply = view.findViewById(R.id.btnBtmProduct);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.changeFilterSort();
                ProductBottomDialogFragment.this.dismiss();
            }
        });
    }

    @BindingAdapter("android:checked")
    public static void setChecked(CompoundButton chk, boolean checked){
        chk.setChecked(checked);
    }

    public void setViewModel(ProductListViewModel vm){
        this.viewModel = vm;
    }
}