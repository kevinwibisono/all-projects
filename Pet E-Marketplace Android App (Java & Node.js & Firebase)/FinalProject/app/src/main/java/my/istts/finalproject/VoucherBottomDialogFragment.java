package my.istts.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.VoucherAdapter;

import my.istts.finalproject.databinding.BottomDialogVouchersBinding;
import my.istts.finalproject.interfaces.setVoucher;
import my.istts.finalproject.viewmodels.ProductCheckoutViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class VoucherBottomDialogFragment extends BottomSheetDialogFragment {
    private setVoucher voucherFunc;
    private ProductCheckoutViewModel viewModel;

    public void setViewModel(ProductCheckoutViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogVouchersBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_vouchers, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VoucherAdapter adapter = new VoucherAdapter(new VoucherAdapter.chooseVoucher() {
            @Override
            public void onChosen(int position) {
                voucherFunc.onChooseVoucher(position);
            }
        }, new VoucherAdapter.seeVoucherDetail() {
            @Override
            public void onDetail(String id) {
                Intent detailIntent = new Intent(getContext(), VoucherDetailActivity.class);
                detailIntent.putExtra("id_promo", id);
                startActivity(detailIntent);
            }
        }, getContext());

        RecyclerView rvVouchers = view.findViewById(R.id.rvVoucherOptions);
        rvVouchers.setAdapter(adapter);

        viewModel.getPromoVMs().observe(getViewLifecycleOwner(), promos -> {
            adapter.setVoucherVMs(promos);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof setVoucher){
            voucherFunc = (setVoucher) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement the interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        voucherFunc = null;
    }
}
