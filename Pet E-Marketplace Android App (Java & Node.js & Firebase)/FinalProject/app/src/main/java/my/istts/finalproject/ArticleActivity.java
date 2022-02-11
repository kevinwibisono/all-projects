package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.CommentAdapter;
import my.istts.finalproject.databinding.ActivityArticleBinding;
import my.istts.finalproject.viewmodels.ArticleDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class ArticleActivity extends AppCompatActivity {

    private String id_artikel;
    private ArticleDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ArticleDetailViewModel(getApplication());
        ActivityArticleBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_article);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        id_artikel = getIntent().getStringExtra("id_artikel");

        MaterialToolbar tbArticle = findViewById(R.id.tbArticle);
        tbArticle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CommentAdapter adapter = new CommentAdapter(new CommentAdapter.openFullDiscussion() {
            @Override
            public void openFull(String id_komentar) {
                Intent fullDiscussion = new Intent(ArticleActivity.this, FullDiscussionActivity.class);
                fullDiscussion.putExtra("id_komentar", id_komentar);
                startActivity(fullDiscussion);
            }
        });

        RecyclerView rvComments = findViewById(R.id.rvArticleComments);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getComments().observe(this, comments->{
            adapter.setComments(comments);
            adapter.notifyDataSetChanged();
        });

        TextView tvLink = findViewById(R.id.tvArticleLink);
        viewModel.getLink().observe(this, link->{
            tvLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
            });
        });

        ImageView ivArtikel = findViewById(R.id.ivArticle);
        viewModel.getPicture().observe(this, picture->{
            Glide.with(this).load(picture).into(ivArtikel);
        });

        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Menambahkan Komentar...");
        loading.setCancelable(false);
        viewModel.isAddCommentsLoading().observe(this, addloading->{
            if(addloading){
                loading.show();
            }
            else{
                loading.dismiss();
            }
        });

        LinearLayout layout = findViewById(R.id.pageArticle);
        EditText edComment = findViewById(R.id.edSendCommentArticle);

        edComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                edComment.requestFocus();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.setArticleDetail(id_artikel);
    }
}