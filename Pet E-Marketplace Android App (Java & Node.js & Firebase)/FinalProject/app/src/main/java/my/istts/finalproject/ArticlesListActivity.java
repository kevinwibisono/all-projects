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

import my.istts.finalproject.adapters.ArticleAdapter;
import my.istts.finalproject.databinding.ActivityArticlesListBinding;
import my.istts.finalproject.viewmodels.ArticleListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

public class ArticlesListActivity extends AppCompatActivity {

    private ArticleListViewModel viewModel;
    private boolean beginNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ArticleListViewModel();
        ActivityArticlesListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_articles_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        if(getIntent().hasExtra("topik_hewan")){
            viewModel.setTopikHewan(getIntent().getIntExtra("topik_hewan", 0));
        }
        else if(getIntent().hasExtra("kategori")){
            viewModel.setCategory(getIntent().getIntExtra("kategori", 0));
        }
        else if(getIntent().hasExtra("target_pembaca")){
            viewModel.setTargetReader(getIntent().getIntExtra("target_pembaca", 0));
        }
        else if(getIntent().hasExtra("search")){
            viewModel.setSearchKeyword(getIntent().getStringExtra("search"));
        }
        else{
            viewModel.initFilters();
            viewModel.getArticleFiltered();
        }

        ArticleAdapter adapter = new ArticleAdapter(new ArticleAdapter.articleClickCallback() {
            @Override
            public void onClick(String id_artikel) {
                Intent articleIntent = new Intent(ArticlesListActivity.this, ArticleActivity.class);
                articleIntent.putExtra("id_artikel", id_artikel);
                startActivity(articleIntent);
            }
        });
        RecyclerView rvArticles = findViewById(R.id.rvArticles);
        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(adapter);

        viewModel.getArticles().observe(this, articles->{
            adapter.setArticlesVMs(articles);
            adapter.notifyDataSetChanged();
        });

        TextInputEditText sbArticles = findViewById(R.id.edSearchArticle);
        sbArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(ArticlesListActivity.this, SearchActivity.class);
                searchIntent.putExtra("tipe", 2);
                startActivity(searchIntent);
            }
        });

        MaterialToolbar tbArticles = findViewById(R.id.tbArticles);
        tbArticles.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tbArticles.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuLike){
                    Intent likedIntent = new Intent(ArticlesListActivity.this, LikedActivity.class);
                    likedIntent.putExtra("jenis", 3);
                    startActivity(likedIntent);
                }
                return false;
            }
        });

        Chip chipSort = findViewById(R.id.chipArticleSort);
        chipSort.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeSort();
            }
        });

        Chip chipCategory = findViewById(R.id.chipArticleCategory);
        chipCategory.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeCategory();
            }
        });

        Chip chipTargetReader = findViewById(R.id.chipArticleTargetReader);
        chipTargetReader.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeTarget();
            }
        });

        ChipGroup cgArticlesPetTypes = findViewById(R.id.chipGroupFilterPetTypesArticle);
        viewModel.getActivePetTypes().observe(this, petTypes -> {
            cgArticlesPetTypes.removeAllViews();
            for (int i = 0; i < petTypes.size(); i++) {
                final int index = i;
                Chip newVariantChip = new Chip(this);

                newVariantChip.requestLayout();
                newVariantChip.setText(petTypes.get(i));
                newVariantChip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
                newVariantChip.setChipStrokeWidth(3);
                newVariantChip.setCloseIconVisible(true);
                newVariantChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewModel.removePetType(index);
                    }
                });


                cgArticlesPetTypes.addView(newVariantChip);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) newVariantChip.getLayoutParams();
                layoutParams.setMargins(0, 0, 5, 0);
            }
        });

        LinearLayout filter = findViewById(R.id.filterSortArticles);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArticleBottomDialogFragment fragment = new ArticleBottomDialogFragment();
                fragment.setViewModel(viewModel);
                fragment.show(getSupportFragmentManager(), "");
            }
        });
    }
}