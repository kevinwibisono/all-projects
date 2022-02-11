package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import my.istts.finalproject.adapters.DiscussListAdapter;
import my.istts.finalproject.databinding.ActivityDiscussListBinding;

import my.istts.finalproject.viewmodels.DiscussListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class DiscussListActivity extends AppCompatActivity {

    private DiscussListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new DiscussListViewModel(getApplication());
        ActivityDiscussListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_discuss_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.initFilters();
        viewModel.getDiscussFiltered();

        DiscussListAdapter adapter = new DiscussListAdapter(new DiscussListAdapter.toDiscussPage() {
            @Override
            public void showPage(String id_discuss) {
                Intent discussIntent = new Intent(DiscussListActivity.this, DiscussActivity.class);
                discussIntent.putExtra("id_diskusi", id_discuss);
                startActivity(discussIntent);
            }
        });

        RecyclerView rvDiscusses = findViewById(R.id.rvDiscussList);
        rvDiscusses.setLayoutManager(new LinearLayoutManager(this));
        rvDiscusses.setAdapter(adapter);

        viewModel.getPetDiscussions().observe(this, discussions->{
            adapter.setDiscuss(discussions);
            adapter.notifyDataSetChanged();
        });

        MaterialToolbar tbDiscusses = findViewById(R.id.tbDiscussList);
        tbDiscusses.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tbDiscusses.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuLike){
                    Intent likedIntent = new Intent(DiscussListActivity.this, LikedActivity.class);
                    likedIntent.putExtra("jenis", 4);
                    startActivity(likedIntent);
                }
                return false;
            }
        });

        LinearLayout filter = findViewById(R.id.filterSortDiscuss);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiscussBottomDialogFragment fragment = new DiscussBottomDialogFragment();
                fragment.setViewModel(viewModel);
                fragment.show(getSupportFragmentManager(), "");
            }
        });

        Chip chipSort = findViewById(R.id.chipDiscussSort);
        chipSort.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeSort();
            }
        });

        ChipGroup cgTypes = findViewById(R.id.chipGroupFilterPetTypesDiscuss);
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

        FloatingActionButton btnAdd = findViewById(R.id.btnAddDiscuss);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(DiscussListActivity.this, AddDiscussActivity.class);
                startActivity(addIntent);
            }
        });

        TabLayout tabs = findViewById(R.id.tabLayoutPetDiscuss);
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


    }
}