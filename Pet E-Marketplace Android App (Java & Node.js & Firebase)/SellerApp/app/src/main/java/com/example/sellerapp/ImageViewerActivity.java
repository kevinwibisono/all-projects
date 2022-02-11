package com.example.sellerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ProgressBar pbImageViewer = findViewById(R.id.pbImageViewer);
        ImageView ivImageViewer = findViewById(R.id.ivImageViewer);
        Glide
            .with(this)
            .load(getIntent().getStringExtra("url"))
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    pbImageViewer.setVisibility(View.GONE);
                    ivImageViewer.setVisibility(View.VISIBLE);
                    return false;
                }
            }).into(ivImageViewer);

        ImageView ivClose = findViewById(R.id.ivImageClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}