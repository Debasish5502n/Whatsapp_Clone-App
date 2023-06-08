package com.example.friendshipforever.fregment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.friendshipforever.Adapters.UsersAdapter;
import com.example.friendshipforever.Model.User;
import com.example.friendshipforever.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ChatsFregment extends Fragment {

    ShimmerRecyclerView recyclerView;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    FirebaseRecyclerOptions<User> options;
    public ChatsFregment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats_fregment, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        database=FirebaseDatabase.getInstance();
        users=new ArrayList<>();

        usersAdapter=new UsersAdapter(getContext(),users);
        recyclerView.setAdapter(usersAdapter);

        recyclerView.showShimmerAdapter();

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 :snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        recyclerView.hideShimmerAdapter();
                        users.add(user);
                    }
                }
                Collections.sort(users,User.nameAtoZ);
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}