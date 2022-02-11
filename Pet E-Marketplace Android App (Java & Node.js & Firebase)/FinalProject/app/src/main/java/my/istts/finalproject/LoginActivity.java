package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ActivityLoginBinding;
import my.istts.finalproject.viewmodels.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private int RC_SIGN_IN = 1;
    private LoginViewModel viewModel;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new LoginViewModel(getApplication());
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.checkAccounts();

        ProgressDialog loadingScreen = new ProgressDialog(this);
        loadingScreen.setTitle("Proses Login....");
        loadingScreen.setCancelable(false);
        viewModel.isLoading().observe(this, loading ->{
            if(loading){
                loadingScreen.show();
            }
            else{
                loadingScreen.dismiss();
            }
        });

        viewModel.isLoggedOn().observe(this, loggedOn ->{
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });

        TextInputLayout[] tlS = {findViewById(R.id.tlLoginHP), findViewById(R.id.tlLoginPass)};
        viewModel.getFieldErrors().observe(this, errors -> {
            for (int i=0; i<errors.length;i++){
                tlS[i].setError(errors[i]);
            }
            tlS[0].requestFocus();
        });

        MaterialButton btnRegister = findViewById(R.id.btnToRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}