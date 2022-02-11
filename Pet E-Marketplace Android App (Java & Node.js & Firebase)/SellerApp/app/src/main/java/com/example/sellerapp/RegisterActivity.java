package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.sellerapp.databinding.ActivityRegisterBinding;
import com.example.sellerapp.viewmodels.RegisterViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RegisterViewModel viewModel = new RegisterViewModel(getApplication());

        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbRegister = findViewById(R.id.tbRegister);
        tbRegister.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ProgressDialog loadScreen = new ProgressDialog(this);
        loadScreen.setCancelable(false);
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                loadScreen.setTitle("Proses Register....");
                loadScreen.show();
            }
            else{
                loadScreen.dismiss();
            }
        });

        TextInputLayout[] tlS = {findViewById(R.id.tlRegEmail), findViewById(R.id.tlRegPass), findViewById(R.id.tlRegConfirmPass)};
        viewModel.getFieldErrors().observe(this, fieldError -> {
            for (int i=0; i<fieldError.length;i++){
                tlS[i].setError(fieldError[i]);
            }
            if(viewModel.getFocusedNumber() > 0){
                tlS[viewModel.getFocusedNumber()].getEditText().requestFocus();
            }
        });

        VerificationBottomDialogFragment verifyDialog = new VerificationBottomDialogFragment();
        viewModel.isValid().observe(this, valid -> {
            verifyDialog.setViewModel(viewModel);
            verifyDialog.show(getSupportFragmentManager(), "");
            verifyDialog.setCancelable(false);
            //start new countdown
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    viewModel.reduceCountDown();
                    handler.postDelayed(this, 1000);
                }
            }, 1000);
        });


        viewModel.isRegistered().observe(this, verified -> {
//            verifyDialog.dismiss();
            startActivity(new Intent(RegisterActivity.this, RoleActivity.class));
        });
    }

}