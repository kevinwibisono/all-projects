package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.CommentAdapter;

import my.istts.finalproject.databinding.ActivityDiscussBinding;
import my.istts.finalproject.viewmodels.DiscussDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;


public class DiscussActivity extends AppCompatActivity {

    private String id_discuss;
    private DiscussDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new DiscussDetailViewModel(getApplication());
        ActivityDiscussBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_discuss);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        id_discuss = getIntent().getStringExtra("id_diskusi");

        SwipeRefreshLayout swipeDiscuss = findViewById(R.id.swipeDiscussDetail);
        swipeDiscuss.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.setDiscussDetail(id_discuss);
                swipeDiscuss.setRefreshing(false);
            }
        });

        CommentAdapter adapter = new CommentAdapter(new CommentAdapter.openFullDiscussion() {
            @Override
            public void openFull(String id_komentar) {
                Intent fullDiscussion = new Intent(DiscussActivity.this, FullDiscussionActivity.class);
                fullDiscussion.putExtra("id_komentar", id_komentar);
                startActivity(fullDiscussion);
            }
        });

        RecyclerView rvDiscussComment = findViewById(R.id.rvDiscussComments);
        rvDiscussComment.setAdapter(adapter);
        rvDiscussComment.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getComments().observe(this, comment->{
            adapter.setComments(comment);
            adapter.notifyDataSetChanged();
        });

        MaterialToolbar tbDisc = findViewById(R.id.tbDiscuss);
        tbDisc.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
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

        LinearLayout layout = findViewById(R.id.pageDiscuss);
        EditText edComment = findViewById(R.id.edAddCommentDiscuss);

        edComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                edComment.requestFocus();
            }
        });

        ImageView ivDiskusi = findViewById(R.id.ivDiscussDetail);
        viewModel.getPicture().observe(this, picture->{
            Glide.with(this).load(picture).into(ivDiskusi);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.setDiscussDetail(id_discuss);
    }
}