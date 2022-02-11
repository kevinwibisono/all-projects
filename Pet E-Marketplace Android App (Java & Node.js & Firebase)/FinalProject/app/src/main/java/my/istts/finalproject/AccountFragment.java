package my.istts.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.databinding.FragmentAccountBinding;
import my.istts.finalproject.viewmodels.AccountFragmentViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AccountFragment extends Fragment {

    private AccountFragmentViewModel viewModel;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new AccountFragmentViewModel(getActivity().getApplication());

        FragmentAccountBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar tbAccount = view.findViewById(R.id.tbAccount);
        tbAccount.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeMenuIntent(item.getItemId());
                return false;
            }
        });

        LinearLayout saldoPart = view.findViewById(R.id.akunSaldoPart);
        saldoPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SaldoActivity.class));
            }
        });


        MenuItem itemCart = tbAccount.getMenu().findItem(R.id.headerCart);
        View actionView = itemCart.getActionView();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenuIntent(R.id.headerCart);
            }
        });
        TextView textCartItemCount = (TextView) actionView.findViewById(R.id.icon_cart_badge);
        viewModel.isItemsInCart().observe(this, itemsInCart->{
            if(itemsInCart){
                textCartItemCount.setVisibility(View.VISIBLE);
            }
            else{
                textCartItemCount.setVisibility(View.GONE);
            }
        });


        MenuItem itemChat = tbAccount.getMenu().findItem(R.id.headerChat);
        View actionViewChat = itemChat.getActionView();
        actionViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenuIntent(R.id.headerChat);
            }
        });
        TextView textChatItemCount = (TextView) actionViewChat.findViewById(R.id.icon_chat_badge);
        viewModel.thereisUnreadChats().observe(this, unreadChats->{
            if(unreadChats){
                textChatItemCount.setVisibility(View.VISIBLE);
            }
            else{
                textChatItemCount.setVisibility(View.GONE);
            }
        });

        viewModel.getAccountDetail();

        ImageView ppUser = view.findViewById(R.id.ivAkunUser);
        viewModel.getUserPic().observe(this, pic ->{
            Glide.with(getContext()).load(pic).into(ppUser);
        });

        ProgressDialog loading = new ProgressDialog(getContext());
        loading.setTitle("Proses Keluar Akun....");
        loading.setCancelable(false);
        viewModel.isLoggingOut().observe(this, loggingOut->{
            if(loggingOut){
                loading.show();
            }
            else{
                loading.dismiss();
            }
        });

        viewModel.isDoneLogout().observe(this, done->{
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Perhatian!")
                        .setMessage("Apakah anda yakin akan keluar?")
                        .setNegativeButton("Tidak", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            viewModel.logout();
                        })
                        .show();
            }
        });

//        LinearLayout[] favs = {view.findViewById(R.id.favOpt1), view.findViewById(R.id.favOpt2), view.findViewById(R.id.favOpt3)};
        LinearLayout[] favs = {view.findViewById(R.id.favOpt1), view.findViewById(R.id.favOpt2)};
        for (int i = 0; i < 2; i++) {
            final int index = i;
            favs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeFavIntent(index);
                }
            });
        }

        LinearLayout[] orders = {view.findViewById(R.id.orderOpt1), view.findViewById(R.id.orderOpt2),
                view.findViewById(R.id.orderOpt3), view.findViewById(R.id.orderOpt4)};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            orders[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent orderIntent = new Intent(getContext(), OrderListActivity.class);
                    orderIntent.putExtra("tipe", index);
                    startActivity(orderIntent);
                }
            });
        }

        TextView tvToUpdate = view.findViewById(R.id.tvAkunUpdate);
        tvToUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewModel.getEmailUser() != null){
                    Intent editIntent = new Intent(getContext(), RegisterActivity.class);
                    editIntent.putExtra("email", viewModel.getEmailUser());
                    startActivity(editIntent);
                }
            }
        });
    }

    private void changeMenuIntent(int id){
        if(id == R.id.headerChat){
            startActivity(new Intent(getContext(), ConversationsActivity.class));
        }
        else if(id == R.id.headerCart){
            startActivity(new Intent(getContext(), CartActivity.class));
        }
        else{
            startActivity(new Intent(getContext(), FavoriteActivity.class));
        }
    }

    private void changeFavIntent(int index){
        if(index == 0){
            startActivity(new Intent(getContext(), FavoriteActivity.class));
        }
        else if(index == 1){
            startActivity(new Intent(getContext(), FollowActivity.class));
        }
//        else{
//            startActivity(new Intent(getContext(), FavoriteActivity.class));
//        }
    }
}