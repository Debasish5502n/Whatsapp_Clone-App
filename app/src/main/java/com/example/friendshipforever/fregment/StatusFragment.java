package com.example.friendshipforever.fregment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.ProcessedData;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.friendshipforever.Adapters.StatusAdapter;
import com.example.friendshipforever.Model.Status;
import com.example.friendshipforever.Model.User;
import com.example.friendshipforever.Model.UserStatus;
import com.example.friendshipforever.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class StatusFragment extends Fragment {

    StatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    RecyclerView recyclerView;
    FloatingActionButton statusbtn,deletebtn;
    ProgressDialog progressDialog;

    User user;
    FirebaseDatabase database;
    public StatusFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_status, container, false);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading image");
        progressDialog.setMessage("Status will be remove at 11:59pm");
        progressDialog.setCancelable(false);
        database=FirebaseDatabase.getInstance();

        recyclerView=view.findViewById(R.id.recyclerView);
        statusbtn=view.findViewById(R.id.statusbtn);
        deletebtn=view.findViewById(R.id.deletebtn);
        userStatuses=new ArrayList<>();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user=snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStatuses.clear();
                    for (DataSnapshot storysnapshot : snapshot.getChildren()){
                        UserStatus status=new UserStatus();
                        status.setName(storysnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storysnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storysnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses=new ArrayList<>();
                        for (DataSnapshot statussnapshot : storysnapshot.child("statuses").getChildren()){
                            Status sampleStatus=statussnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        statusAdapter=new StatusAdapter(getContext(),userStatuses);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(statusAdapter);

        statusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,75);
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference()
                        .child("stories")
                        .child(FirebaseAuth.getInstance().getUid())
                        .removeValue();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data !=null){
            if (data.getData() !=null){
                progressDialog.show();
                FirebaseStorage storage=FirebaseStorage.getInstance();

                Date date=new Date();
                StorageReference reference=storage.getReference().child("Status").child(date.getTime()+"");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus=new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String,Object> obj=new HashMap<>();
                                    obj.put("name",userStatus.getName());
                                    obj.put("profileImage",userStatus.getProfileImage());
                                    obj.put("lastUpdated",userStatus.getLastUpdated());

                                    String imageUrl=uri.toString();
                                    Status status=new Status(imageUrl,userStatus.getLastUpdated());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    try {
                                        database.getReference()
                                                .child("stories")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("statuses")
                                                .push()
                                                .setValue(status);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}