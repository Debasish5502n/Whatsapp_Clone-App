package com.example.friendshipforever.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendshipforever.Activitys.ChatActivity;
import com.example.friendshipforever.Activitys.ImageprofileActivity;
import com.example.friendshipforever.R;
import com.example.friendshipforever.Model.User;
import com.example.friendshipforever.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Viewholder> {

    Context context;
    ArrayList<User> users;

    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        User user=users.get(position);

        String senderId= FirebaseAuth.getInstance().getUid();
        String senderroom=senderId+user.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderroom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String lastMsg=snapshot.child("lastMsg").getValue(String.class);
                            long time=snapshot.child("lastMsgTime").getValue(Long.class);
                            SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
                            holder.binding.time.setText(dateFormat.format(new Date(time)));
                            holder.binding.lastsms.setText(lastMsg);
                        }else {
                            holder.binding.lastsms.setText("Tap to chat");
                            holder.binding.time.setText(" ");
                            holder.binding.online.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.name.setText(user.getName());
        Glide
                .with(context)
                .load(user.getProfileImage())
                .into(holder.binding.profile);

        holder.binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Intent intent=new Intent(context, ImageprofileActivity.class);
                  intent.putExtra("image",user.getProfileImage());
                  context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.online.setVisibility(View.GONE);
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("uid",user.getUid());
                intent.putExtra("profileImage",user.getProfileImage());
                intent.putExtra("token",user.getToken());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        RowConversationBinding binding;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            binding=RowConversationBinding.bind(itemView);
        }
    }
}
