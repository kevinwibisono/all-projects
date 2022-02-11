package com.example.sellerapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp.adapters.ChatProductAdapter;
import com.example.sellerapp.databinding.BottomDialogChatproductBinding;
import com.example.sellerapp.viewmodels.ChatViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.sellerapp.interfaces.chatChooseProduct;

public class ChatProductBottomDialogFragment extends BottomSheetDialogFragment {
    private chatChooseProduct chooseItemAdapter;
    private ChatViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogChatproductBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_chatproduct, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ChatProductAdapter adapter = new ChatProductAdapter(chooseItemAdapter, getContext());

        RecyclerView rvProd = view.findViewById(R.id.rvChatProducts);
        rvProd.setAdapter(adapter);

        viewModel.getProductVMs().observe(this, vms -> {
            adapter.setProdVMs(vms);
            adapter.notifyDataSetChanged();
        });

    }

    public void setViewModel(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof chatChooseProduct){
            chooseItemAdapter = (chatChooseProduct) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        chooseItemAdapter = null;
    }
}
