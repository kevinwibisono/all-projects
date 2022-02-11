package com.example.sellerapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sellerapp.adapters.ConversationsAdapter;
import com.example.sellerapp.databinding.FragmentConversationsBinding;
import com.example.sellerapp.databinding.FragmentProductListBinding;
import com.example.sellerapp.viewmodels.ConversationViewModel;

public class ConversationsFragment extends Fragment {

    private ConversationViewModel viewModel;
    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ConversationViewModel.class);

        FragmentConversationsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversations, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConversationsAdapter adapter = new ConversationsAdapter(new ConversationsAdapter.openConversation() {
            @Override
            public void onOpen(String id) {
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                chatIntent.putExtra("id_room", id);
                startActivity(chatIntent);
            }
        });

        RecyclerView rvConvs = view.findViewById(R.id.rvConversations);
        rvConvs.setAdapter(adapter);

        viewModel.getConvsVM().observe(this, vms -> {
            adapter.setConvVM(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getConversations();

    }
}