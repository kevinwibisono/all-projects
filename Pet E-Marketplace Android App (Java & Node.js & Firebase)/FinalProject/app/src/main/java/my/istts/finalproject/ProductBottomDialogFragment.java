package my.istts.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.BottomDialogProductsBinding;
import my.istts.finalproject.viewmodels.ProductListViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class ProductBottomDialogFragment extends BottomSheetDialogFragment {

    private ProductListViewModel viewModel;

    public void setViewModel(ProductListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogProductsBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_products, container,
                false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnApply = view.findViewById(R.id.btnFilterProductsActivate);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setFilters();
                ProductBottomDialogFragment.this.dismiss();
            }
        });

    }

    @BindingAdapter("android:checked")
    public static void setChecked(CompoundButton chk, boolean checked){
        chk.setChecked(checked);
    }
}
