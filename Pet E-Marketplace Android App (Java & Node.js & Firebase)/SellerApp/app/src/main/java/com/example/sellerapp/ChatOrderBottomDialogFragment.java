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

import com.example.sellerapp.adapters.ChatOrderAdapter;
import com.example.sellerapp.databinding.BottomDialogChatorderBinding;
import com.example.sellerapp.viewmodels.ChatViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.sellerapp.interfaces.chatChooseOrder;

public class ChatOrderBottomDialogFragment extends BottomSheetDialogFragment {
    private chatChooseOrder chooseOrderAdapter;
    private ChatViewModel viewModel;

    public void setViewModel(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogChatorderBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_chatorder, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ChatOrderAdapter adapter = new ChatOrderAdapter(chooseOrderAdapter, getActivity());

        RecyclerView rvProdRooms = view.findViewById(R.id.rvOrderChat);
        rvProdRooms.setAdapter(adapter);

        viewModel.getOrderVMs().observe(this, orders->{
            adapter.setOrderVMs(orders);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof chatChooseOrder){
            chooseOrderAdapter = (chatChooseOrder) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        chooseOrderAdapter = null;
    }
}
