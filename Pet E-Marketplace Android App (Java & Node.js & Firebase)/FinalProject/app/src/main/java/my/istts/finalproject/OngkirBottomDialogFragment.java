package my.istts.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.OngkirAdapter;

import my.istts.finalproject.databinding.BottomOngkirOptionsBinding;
import my.istts.finalproject.interfaces.setOngkir;
import my.istts.finalproject.viewmodels.ProductCheckoutViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class OngkirBottomDialogFragment extends BottomSheetDialogFragment {
    private ProductCheckoutViewModel viewModel;
    private setOngkir ongkirFunc;

    public void setViewModel(ProductCheckoutViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomOngkirOptionsBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_ongkir_options, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OngkirAdapter adapter = new OngkirAdapter(new OngkirAdapter.chooseOngkir() {
            @Override
            public void onChosen(int position) {
                ongkirFunc.onChooseOngkir(position);
            }
        });

        RecyclerView rvOngkir = view.findViewById(R.id.rvOngkirOptions);
        rvOngkir.setAdapter(adapter);

        viewModel.getOngkirVMs().observe(this, vms ->{
            adapter.setOngkirVMs(vms);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof setOngkir){
            ongkirFunc = (setOngkir) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement the interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ongkirFunc = null;
    }
}
