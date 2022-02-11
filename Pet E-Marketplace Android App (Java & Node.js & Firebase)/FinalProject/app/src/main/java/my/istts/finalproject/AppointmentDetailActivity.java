package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.PetAppointmentAdapter;
import my.istts.finalproject.databinding.ActivityAppointmentDetailBinding;
import my.istts.finalproject.viewmodels.AppointmentDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AppointmentDetailActivity extends AppCompatActivity {

    private AppointmentDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AppointmentDetailViewModel();
        ActivityAppointmentDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAppoDetail = findViewById(R.id.tbAppoDetail);
        tbAppoDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PetAppointmentAdapter adapter = new PetAppointmentAdapter();
        RecyclerView rvPets = findViewById(R.id.rvAppoDetail);
        rvPets.setAdapter(adapter);

        viewModel.getAppointmentDetail(getIntent().getStringExtra("id_pj"));

        viewModel.getPetsName().observe(this, names->{
            adapter.setAges(viewModel.getPetsAge().getValue());
            adapter.setKinds(viewModel.getPetsKind().getValue());
            adapter.setNames(names);
            adapter.notifyDataSetChanged();
        });

        ImageView ivClinic = findViewById(R.id.ivAppoDetailClinic);
        viewModel.getClinicPic().observe(this, picture->{
            Glide.with(this).load(picture).into(ivClinic);
        });
        viewModel.getClinicEmail().observe(this, email->{
            ivClinic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sellerIntent = new Intent(AppointmentDetailActivity.this, SellerProfileActivity.class);
                    sellerIntent.putExtra("hp_seller", email);
                    startActivity(sellerIntent);
                }
            });
        });

        MaterialButton btnChat = findViewById(R.id.btnAppoDetailChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(AppointmentDetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("lawan_bicara", viewModel.getClinicEmail().getValue());
                chatIntent.putExtra("id_item", getIntent().getStringExtra("id_pj"));
                chatIntent.putExtra("tipe", 1);
                startActivity(chatIntent);
            }
        });

        MaterialButton[] btnActions = {findViewById(R.id.btnAppoDetailNavigate), findViewById(R.id.btnAppoDetailCancel),
                findViewById(R.id.btnAppoDetailFinish), findViewById(R.id.btnAppoDetailReview)};

        for (int i = 0; i < btnActions.length; i++) {
            final int index = i;
            btnActions[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actions(index);
                }
            });
        }

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Mengubah Status...");
        loading.setCancelable(true);
        viewModel.isLoading().observe(this, updateOrderLoading->{
            if(updateOrderLoading){
                loading.show();
            }
            else{
                loading.dismiss();
            }
        });

        LinearLayout partSeller = findViewById(R.id.partOrderDetailSeller);

    }

    private void actions(int index){
        if(index == 0){
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+viewModel.getCoordinate().getValue());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        else if(index == 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentDetailActivity.this);
            builder.setTitle("Tuliskan Alasan Pembatalan");

            final TextInputEditText input = new TextInputEditText(AppointmentDetailActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(input.getText().toString().length() > 3){
                        viewModel.cancelAppointment(input.getText().toString());
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else if(index == 2){
            viewModel.finishAppointment();
        }
        else{
            Intent reviewIntent = new Intent(AppointmentDetailActivity.this, ReviewActivity.class);
            reviewIntent.putExtra("id_pj", getIntent().getStringExtra("id_pj"));
            startActivity(reviewIntent);
        }
    }
}