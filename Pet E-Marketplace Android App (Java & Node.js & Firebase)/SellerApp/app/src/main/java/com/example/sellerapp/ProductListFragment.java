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

import com.example.sellerapp.adapters.ProductAdapter;
import com.example.sellerapp.databinding.FragmentProductListBinding;
import com.example.sellerapp.viewmodels.ProductListViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class ProductListFragment extends Fragment {

    private ProductListViewModel viewModel;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ProductListViewModel(getActivity().getApplication());

        FragmentProductListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProductAdapter adapter = new ProductAdapter(new ProductAdapter.showProductDetail() {
            @Override
            public void showDetail(String id) {
                Intent updateIntent = new Intent(getActivity(), ProductActivity.class);
                updateIntent.putExtra("id_produk", id);
                startActivity(updateIntent);
            }
        }, new ProductAdapter.showProductPreview() {
            @Override
            public void showPreview(String id) {
                Intent previewIntent = new Intent(getActivity(), PreviewProductActivity.class);
                previewIntent.putExtra("id_produk", id);
                startActivity(previewIntent);
            }
        });

        RecyclerView rvProducts = view.findViewById(R.id.rvProductList);
        rvProducts.setAdapter(adapter);

        viewModel.getProductVMS().observe(this, vms -> {
            adapter.setVM(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getProducts();

        MaterialButton btnAdd = view.findViewById(R.id.btnToAddProduct);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProduct = new Intent(getActivity(), ProductActivity.class);
                startActivity(addProduct);
            }
        });

        LinearLayout filterSort = view.findViewById(R.id.filterSortProduct);
        filterSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductBottomDialogFragment fragment = new ProductBottomDialogFragment();
                fragment.setViewModel(viewModel);
                fragment.show(getActivity().getSupportFragmentManager(), "");
            }
        });

        ChipGroup cgKategori = view.findViewById(R.id.chipGroupFilterKats);
        viewModel.getCategoriesActive().observe(this, categories -> {
            cgKategori.removeAllViews();
            for (int i = 0; i < categories.size(); i++) {
                final int index = i;
                Chip newVariantChip = new Chip(getContext());

                newVariantChip.requestLayout();
                newVariantChip.setText(categories.get(i));
                newVariantChip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
                newVariantChip.setChipStrokeWidth(3);
                newVariantChip.setCloseIconVisible(true);
                newVariantChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewModel.deleteCategoryFilter(index);
                        viewModel.getProducts();
                    }
                });


                cgKategori.addView(newVariantChip);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) newVariantChip.getLayoutParams();
                layoutParams.setMargins(0, 0, 5, 0);
            }
        });

    }
}