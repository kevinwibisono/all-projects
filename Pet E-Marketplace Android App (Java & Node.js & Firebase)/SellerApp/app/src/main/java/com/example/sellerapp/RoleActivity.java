package com.example.sellerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class RoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        MaterialCardView[] cards = {findViewById(R.id.cardBannerShop), findViewById(R.id.cardBannerGroom), findViewById(R.id.cardBannerHotel), findViewById(R.id.cardBannerClinic)};
        for (int i=0;i<4;i++){
            final int index = i;
            cards[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mainIntent = new Intent(RoleActivity.this, SellerDetailActivity.class);
                    mainIntent.putExtra("role", index);
                    startActivity(mainIntent);
                }
            });
        }

    }

    @Override
    public void onBackPressed() {

    }
}