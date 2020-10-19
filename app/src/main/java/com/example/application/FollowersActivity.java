package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.application.Adapter.UserAdapter;
import com.example.application.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    private String id;
    private String title;
    private List<String> idList;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        Toolbar toolbar;
        toolbar = findViewById( R.id.toolbar);
        //getSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this,mUsers,false);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();
        switch (title)
        {
            case "followers":
                getfollowers();
                break;
            case "followings":
                getfollowing();
                break;
            case "likes":
                getLikes();
                break;

        }


    }

    private void getfollowers() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("followers").addValueEventListener(new ValueEventListener() {
            @Override

                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    idList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        idList.add(snapshot1.getKey());
                    }
                    showUsers();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getfollowing() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("following").addValueEventListener(new ValueEventListener() {
            @Override

                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    idList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        idList.add(snapshot1.getKey());
                    }
                    showUsers();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes() {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    idList.add(snapshot1.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    User user =  snapshot1.getValue(User.class);
                    for(String id: idList)
                    {
                        if(user.getId().equals(id))
                        {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}