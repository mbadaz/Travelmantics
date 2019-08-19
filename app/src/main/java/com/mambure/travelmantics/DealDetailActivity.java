package com.mambure.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class DealDetailActivity extends AppCompatActivity {

    TravelDeal mDeal;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_detail);

        Intent intent = getIntent();
        mDeal = (TravelDeal) intent.getSerializableExtra("travelDeal");

        image = findViewById(R.id.image_deal);
        TextView title = findViewById(R.id.tv_title);
        TextView price = findViewById(R.id.tv_detail_price);
        TextView description = findViewById(R.id.tv_dealDescription);

        if (mDeal != null) {
            title.setText(mDeal.getTitle());
            price.setText("R" + mDeal.getPrice());
            description.setText(mDeal.getDescription());
            loadImage(mDeal.getImageUrl());
        }

    }

    private void loadImage(String imageUrl) {
        if (!imageUrl.isEmpty()){
            GlideApp.with(image).
                    load(imageUrl).
                    diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).
                    centerCrop().
                    into(image);
        }
    }
}
