package com.example.friendshipforever.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.friendshipforever.R;
import com.example.friendshipforever.databinding.ActivityMainBinding;
import com.example.friendshipforever.fregment.CallFragment;
import com.example.friendshipforever.fregment.ChatsFregment;
import com.example.friendshipforever.fregment.StatusFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
//    ArrayList<User> users;
//    UsersAdapter usersAdapter;

//    ShimmerRecyclerView recyclerView;
//    ArrayList<User> users;
//    UsersAdapter usersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        database=FirebaseDatabase.getInstance();

        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                String bagroundImage=mFirebaseRemoteConfig.getString("bagroundImage");
                Glide.with(MainActivity.this).load(bagroundImage).into(binding.backgroundImage);

                String toolbarcolor=mFirebaseRemoteConfig.getString("toolbarcolor");
                String toolBarImage = mFirebaseRemoteConfig.getString("toolbarImage");
                boolean isToolBarImageEnabled = mFirebaseRemoteConfig.getBoolean("toolBarImageEnabled");


        if(isToolBarImageEnabled) {
            Glide.with(MainActivity.this)
                    .load(toolBarImage)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                            getSupportActionBar()
                                    .setBackgroundDrawable(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }
                    });
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(toolbarcolor)));
        }

    }
});


        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("token",token);
                        database.getReference().child("users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(map);
                    }
                });

        ChatsFregment fm=new ChatsFregment();
        FragmentTransaction tranjaction = getSupportFragmentManager().beginTransaction();
        tranjaction.replace(R.id.fremLayout,fm);
        tranjaction.commit();

//        recyclerView=findViewById(R.id.recyclerView);
//        database=FirebaseDatabase.getInstance();
//        users=new ArrayList<>();
//
//        usersAdapter=new UsersAdapter(this,users);
//        recyclerView.setAdapter(usersAdapter);
//
//        recyclerView.showShimmerAdapter();
//
//        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                users.clear();
//                for (DataSnapshot snapshot1 :snapshot.getChildren()){
//                    User user=snapshot1.getValue(User.class);
//                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
//                        recyclerView.hideShimmerAdapter();
//                        users.add(user);
//                    }
//                }
//                Collections.sort(users,User.nameAtoZ);
//                usersAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.chats:
                        ChatsFregment fm=new ChatsFregment();
                        FragmentTransaction tranjaction = getSupportFragmentManager().beginTransaction();
                        tranjaction.replace(R.id.fremLayout,fm);
                        tranjaction.commit();
                                break;
                            case R.id.status:
                        StatusFragment fm1=new StatusFragment();
                        FragmentTransaction tranjaction1 = getSupportFragmentManager().beginTransaction();
                        tranjaction1.replace(R.id.fremLayout,fm1);
                        tranjaction1.commit();
                      //  recyclerView.setVisibility(View.INVISIBLE);
                        item.setChecked(true);
                                break;
                            case R.id.calls:
                                CallFragment fm2=new CallFragment();
                                FragmentTransaction tranjaction2 = getSupportFragmentManager().beginTransaction();
                                tranjaction2.replace(R.id.fremLayout,fm2);
                                tranjaction2.commit();
                            //    recyclerView.setVisibility(View.INVISIBLE);
                                item.setChecked(true);
                                break;
                    }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.groups:
                startActivity(new Intent(MainActivity.this,GroupchatActivity.class));
                break;
            case R.id.invite:
                Intent share_intent=new Intent();
                share_intent.setAction(Intent.ACTION_SEND);
                share_intent.putExtra(Intent.EXTRA_TEXT,"Check this app via\n"+
                        "https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
                share_intent.setType("text/plain");
                startActivity(Intent.createChooser(share_intent,"Share app via"));
                break;
            case R.id.setting:
                Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile:
                Intent intent=new Intent(MainActivity.this,EditprofileActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentUser= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentUser).setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentUser=FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentUser).setValue("offline");

    }
}