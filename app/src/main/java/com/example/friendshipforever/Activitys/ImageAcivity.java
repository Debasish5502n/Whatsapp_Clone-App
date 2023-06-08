package com.example.friendshipforever.Activitys;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.friendshipforever.R;

public class ImageAcivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_acivity);
//        getSupportActionBar().hide();

        imageView=findViewById(R.id.imageView);

        String name=getIntent().getStringExtra("photo");
        String profileImage=getIntent().getStringExtra("image");

        Glide.with(getApplicationContext()).load(name).into(imageView);
      //  Glide.with(getApplicationContext()).load(profileImage).into(imageView);
    }
}