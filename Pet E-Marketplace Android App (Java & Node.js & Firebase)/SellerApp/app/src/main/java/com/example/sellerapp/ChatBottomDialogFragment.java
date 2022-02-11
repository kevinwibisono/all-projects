package com.example.sellerapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.sellerapp.interfaces.chatListItem;
import com.example.sellerapp.interfaces.chatListPictures;
import com.example.sellerapp.interfaces.chatListOrder;

public class ChatBottomDialogFragment extends BottomSheetDialogFragment {
    private chatListPictures showPicCallback;
    private chatListItem showProdRoomCallback;
    private chatListOrder showOrdersCallback;
    private int role = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_dialog_chat, container,
                false);

        // get the views and attach the listener

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout partProdRoom = view.findViewById(R.id.partChatProdRoom);
        ImageView ivChatItem = view.findViewById(R.id.ivChatItemOpt);
        TextView tvChatItem = view.findViewById(R.id.tvChatItemOpt);
        TextView tvChatOrder = view.findViewById(R.id.tvBtmChatPJ);

        if(role == 0){
            ivChatItem.setImageResource(R.drawable.producticon);
            tvChatItem.setText("Produk");
            tvChatOrder.setText("Pesanan");
            partProdRoom.setVisibility(View.VISIBLE);
        }
        else if(role == 2){
            ivChatItem.setImageResource(R.drawable.roomicon);
            tvChatItem.setText("Kamar");
            tvChatOrder.setText("Booking");
            partProdRoom.setVisibility(View.VISIBLE);
        }
        else if(role == 1){
            tvChatOrder.setText("Grooming");
            partProdRoom.setVisibility(View.GONE);
        }
        else{
            tvChatOrder.setText("JanjiTemu");
            partProdRoom.setVisibility(View.GONE);
        }

        LinearLayout partPic = view.findViewById(R.id.partChatPicture);
        partPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatBottomDialogFragment.this.dismiss();
                showPicCallback.showPictures();
            }
        });

        partProdRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatBottomDialogFragment.this.dismiss();
                showProdRoomCallback.showItems();
            }
        });

        LinearLayout partOrder = view.findViewById(R.id.partChatOrder);
        partOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatBottomDialogFragment.this.dismiss();
                showOrdersCallback.showOrders();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof chatListPictures && context instanceof chatListOrder && context instanceof chatListItem){
            showPicCallback = (chatListPictures) context;
            showProdRoomCallback = (chatListItem) context;
            showOrdersCallback = (chatListOrder) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        showOrdersCallback = null;
        showProdRoomCallback = null;
        showPicCallback = null;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
