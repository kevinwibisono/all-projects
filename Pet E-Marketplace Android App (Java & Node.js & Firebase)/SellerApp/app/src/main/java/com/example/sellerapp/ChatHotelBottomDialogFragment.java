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

import com.example.sellerapp.adapters.ChatHotelAdapter;
import com.example.sellerapp.databinding.BottomDialogChathotelBinding;
import com.example.sellerapp.interfaces.chatChooseHotel;
import com.example.sellerapp.interfaces.chatChooseProduct;
import com.example.sellerapp.viewmodels.ChatViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChatHotelBottomDialogFragment extends BottomSheetDialogFragment {
    private chatChooseHotel chooseItemAdapter;
    private ChatViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogChathotelBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_chathotel, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ChatHotelAdapter adapter = new ChatHotelAdapter(chooseItemAdapter, getContext());

        RecyclerView rvProd = view.findViewById(R.id.rvChatHotels);
        rvProd.setAdapter(adapter);

        viewModel.getHotelVMs().observe(this, vms -> {
            adapter.setHotelVMs(vms);
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
            chooseItemAdapter = (chatChooseHotel) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        chooseItemAdapter = null;
    }
}
