package com.example.friendshipforever.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.friendshipforever.R;

public class ImageprofileActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageprofile);
        imageView=findViewById(R.id.imageView);

        String profileImage=getIntent().getStringExtra("image");

        Glide.with(getApplicationContext()).load(profileImage).into(imageView);
    }
}