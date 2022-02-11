package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.AdoptionAdapter;
import my.istts.finalproject.databinding.ActivityPetAdoptBinding;

import my.istts.finalproject.viewmodels.AdoptListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class PetAdoptActivity extends AppCompatActivity {

    private AdoptListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AdoptListViewModel(getApplication());
        ActivityPetAdoptBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_adopt);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        AdoptionAdapter adapter = new AdoptionAdapter(new AdoptionAdapter.petClickCallback() {
            @Override
            public void onPetClick(String id_pet) {
                Intent detailIntent = new Intent(PetAdoptActivity.this, PetAdoptDetailActivity.class);
                detailIntent.putExtra("id_pet", id_pet);
                startActivity(detailIntent);
            }
        });
        RecyclerView rvAdopt = findViewById(R.id.rvPetAdopt);
        rvAdopt.setLayoutManager(new GridLayoutManager(this, 2));
        rvAdopt.setAdapter(adapter);

        viewModel.getPetAdopts().observe(this, vms->{
            adapter.setAdoptsVMs(vms);
            adapter.notifyDataSetChanged();
        });

        MaterialToolbar tbAdopt = findViewById(R.id.tbAdopt);
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
                    Intent favIntent = new Intent(PetAdoptActivity.this, FavoriteActivity.class);
                    favIntent.putExtra("tipe", 2);
                    startActivity(favIntent);
                }
                return false;
            }
        });

        LinearLayout filter = findViewById(R.id.filterSortAdopt);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdoptBottomDialogFragment fragment = new AdoptBottomDialogFragment();
                fragment.setViewModel(viewModel);
                fragment.show(getSupportFragmentManager(), "");
            }
        });

        Chip chipGender = findViewById(R.id.chipAdoptGender);
        chipGender.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeGenderFilter();
            }
        });

        ChipGroup cgTypes = findViewById(R.id.chipGroupFilterPetTypes);
        viewModel.getActivePetTypes().observe(this, pettypes -> {
            cgTypes.removeAllViews();
            for (int i = 0; i < pettypes.size(); i++) {
                final int index = i;
                Chip newVariantChip = new Chip(this);

                newVariantChip.requestLayout();
                newVariantChip.setText(pettypes.get(i));
                newVariantChip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
                newVariantChip.setChipStrokeWidth(3);
                newVariantChip.setCloseIconVisible(true);
                newVariantChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewModel.removePetTypes(index);
                    }
                });


                cgTypes.addView(newVariantChip);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) newVariantChip.getLayoutParams();
                layoutParams.setMargins(0, 0, 5, 0);
            }
        });

        FloatingActionButton btnAdd = findViewById(R.id.btnAddAdopt);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(PetAdoptActivity.this, AddAdoptionActivity.class);
                startActivity(addIntent);
            }
        });

        TabLayout tabs = findViewById(R.id.tabLayoutPetAdopt);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    viewModel.setHpCriteria(false);
                }
                else{
                    viewModel.setHpCriteria(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.initFilters();
    }
}