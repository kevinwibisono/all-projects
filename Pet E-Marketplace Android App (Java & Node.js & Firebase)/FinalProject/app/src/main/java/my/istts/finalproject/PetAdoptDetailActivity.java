package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ActivityPetAdoptDetailBinding;
import my.istts.finalproject.viewmodels.AdoptionDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class PetAdoptDetailActivity extends AppCompatActivity {

    private AdoptionDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AdoptionDetailViewModel(getApplication());
        ActivityPetAdoptDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_adopt_detail);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getPetDetails(getIntent().getStringExtra("id_pet"));

        MaterialToolbar tbAdopt = findViewById(R.id.tbAdoptDetail);
        tbAdopt.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tbAdopt.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuFav){
                    Intent favIntent = new Intent(PetAdoptDetailActivity.this, FavoriteActivity.class);
                    favIntent.putExtra("tipe", 2);
                    startActivity(favIntent);
                }
                return false;
            }
        });

        ImageView ivAdopt = findViewById(R.id.ivPetAdoptDetail);
        viewModel.getPicture().observe(this, picture->{
            Glide.with(this).load(picture).into(ivAdopt);
        });

        MaterialButton btnChat = findViewById(R.id.btnChatAdoptOwner);
        viewModel.getPetOwner().observe(this, ownerHP->{
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chatIntent = new Intent(PetAdoptDetailActivity.this, ChatActivity.class);
                    chatIntent.putExtra("lawan_bicara", ownerHP);
                    chatIntent.putExtra("id_item", getIntent().getStringExtra("id_pet"));
                    chatIntent.putExtra("tipe", 3);
                    startActivity(chatIntent);
                }
            });
        });

        MaterialButton btnEdit = findViewById(R.id.btnEditAdopt);
        viewModel.getPetId().observe(this, id->{
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chatIntent = new Intent(PetAdoptDetailActivity.this, AddAdoptionActivity.class);
                    chatIntent.putExtra("id_pet", id);
                    startActivity(chatIntent);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getPetDetails(getIntent().getStringExtra("id_pet"));
    }
}