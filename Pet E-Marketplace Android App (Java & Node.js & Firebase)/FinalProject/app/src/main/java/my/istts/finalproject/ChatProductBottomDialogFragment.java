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

import my.istts.finalproject.adapters.ChatProductAdapter;
import my.istts.finalproject.databinding.BottomDialogChatproductBinding;
import my.istts.finalproject.interfaces.chatChooseProduct;
import my.istts.finalproject.viewmodels.ChatViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
