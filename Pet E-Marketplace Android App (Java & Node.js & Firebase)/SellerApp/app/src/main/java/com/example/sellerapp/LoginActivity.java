package com.example.sellerapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.sellerapp.databinding.ActivityLoginBinding;
import com.example.sellerapp.viewmodels.LoginViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginViewModel viewModel = new LoginViewModel(getApplication());

        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.checkAccounts();

        ProgressDialog loadScreen = new ProgressDialog(this);
        loadScreen.setCancelable(false);
        loadScreen.setTitle("Proses Login.....");
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                loadScreen.show();
            }
            else{
                loadScreen.dismiss();
            }
        });

        viewModel.getLoginState().observe(this, state -> {
            if(state.equals(0)){
                //register separuh jalan, ada akun tidak ada detail penjual
                startActivity(new Intent(LoginActivity.this, RoleActivity.class));
            }
            else if(state.equals(1)){
                //register sepenuhnya, ada akun dan ada detail penjual

                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                mainIntent.putExtra("Role", viewModel.getSavedRole());
                startActivity(mainIntent);
            }
        });

        TextInputLayout[] tlS = {findViewById(R.id.tlLoginNoHP), findViewById(R.id.tlLoginPass)};
        viewModel.getFieldErrors().observe(this, errors -> {
            for (int i=0; i<errors.length;i++){
                tlS[i].setError(errors[i]);
            }
            tlS[0].requestFocus();
        });

    }

    public void toRegister(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

//    public void facebookLogin(View v){
//        callbackManager = CallbackManager.Factory.create();
//        LoginButton btnFB = findViewById(R.id.btnFacebook);
//        btnFB.setReadPermissions("email", "public_profile", "user_friends");
//        btnFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Toast.makeText(LoginActivity.this, Profile.getCurrentProfile().getId(), Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onCancel() {
//            }
//            @Override
//            public void onError(FacebookException error) {
//                Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}