package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ArticleAdapter;
import my.istts.finalproject.adapters.DiscussListAdapter;

import my.istts.finalproject.databinding.ActivityLikedBinding;
import my.istts.finalproject.viewmodels.LikedViewModel;

public class LikedActivity extends AppCompatActivity {

    private LikedViewModel viewModel;
    private int jenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new LikedViewModel(getApplication());
        ActivityLikedBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_liked);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        jenis = getIntent().getIntExtra("jenis", 3);

        MaterialToolbar tbFav = findViewById(R.id.tbLiked);
        tbFav.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArticleAdapter adapter = new ArticleAdapter(new ArticleAdapter.articleClickCallback() {
            @Override
            public void onClick(String id_artikel) {
                Intent articleIntent = new Intent(LikedActivity.this, ArticleActivity.class);
                articleIntent.putExtra("id_artikel", id_artikel);
                startActivity(articleIntent);
            }
        });

        DiscussListAdapter discAdapter = new DiscussListAdapter(new DiscussListAdapter.toDiscussPage() {
            @Override
            public void showPage(String id_diskusi) {
                Intent discussIntent = new Intent(LikedActivity.this, DiscussActivity.class);
                discussIntent.putExtra("id_diskusi", id_diskusi);
                startActivity(discussIntent);
            }
        });

        RecyclerView rvLiked = findViewById(R.id.rvLiked);
        if(jenis == 3){
            rvLiked.setAdapter(adapter);
        }
        else{
            rvLiked.setAdapter(discAdapter);
        }

        viewModel.getArticleVMs().observe(this, articles->{
            adapter.setArticlesVMs(articles);
            adapter.notifyDataSetChanged();
        });

        viewModel.getDiscussVMs().observe(this, discuss->{
            discAdapter.setDiscuss(discuss);
            discAdapter.notifyDataSetChanged();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.searchLikedItems(jenis);
    }
}