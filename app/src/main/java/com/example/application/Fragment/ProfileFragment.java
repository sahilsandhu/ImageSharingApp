package com.example.application.Fragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application.Adapter.PhotoAdapter;
import com.example.application.Adapter.PostAdapter;
import com.example.application.EditProfileActivity;
import com.example.application.FollowersActivity;
import com.example.application.Model.Post;
import com.example.application.Model.User;
import com.example.application.OptionsActivity;
import com.example.application.R;
import com.google.api.HttpRule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.internal.$Gson$Preconditions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.ObjLongConsumer;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewSaves;
    PostAdapter postAdapterSaves;
    private List<Post> mySavedPost;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    List<Post> myPhotoList;
    CircleImageView imageProfile;
    ImageView options;
    TextView posts;
    TextView followers;
    TextView following;
    TextView fullname;
    TextView bio;
    TextView username;

    ImageView myPictures;
    ImageView savedPictures;
    FirebaseUser fUser;
    String profileId;
    Button editProfile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        String data= getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","none");
        if(data.equals("none"))
        {
            profileId = fUser.getUid();
        }
        else
        {
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }
        imageProfile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following= view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        editProfile = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recycler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new GridLayoutManager(getContext(),3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(),myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewSaves =  view.findViewById(R.id.recycler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPost = new ArrayList<>();
        postAdapterSaves = new PostAdapter(getContext(),mySavedPost);
        recyclerViewSaves.setAdapter(postAdapterSaves);

        userInfo();
        getfollowerAndfollowingCount();
        getPostCount();
        myphotos();
        getSavedPosts();

        if(profileId.equals(fUser.getUid()))
        {
            editProfile.setText("Edit Profile");
        }
        else
        {
            checkFollowingStatus();
        }
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();
                if(btnText.equals("Edit Profile"))
                {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));

                    //goto edit profile;
                }else
                {
                 if(btnText.equals("follow"))
                 {
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following")
                             .child(profileId).setValue(true);
                 FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(fUser.getUid()).setValue(true);
                 }
                 else
                 {
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following")
                             .child(profileId).removeValue();
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(fUser.getUid()).removeValue();

                 }
                }
            }
        });
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);

            }
        });
        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title", "followings");
                startActivity(intent);
            }
        });
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        return view;
    }

    private void getSavedPosts() {
        final List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    savedIds.add(snapshot1.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        mySavedPost.clear();
                        for(DataSnapshot snapshot2 : snapshot1.getChildren())
                        {
                            Post post  = snapshot2.getValue(Post.class);
                            for(String id : savedIds)
                            {
                                if(post.getPostid().equals(id))
                                {
                                    mySavedPost.add(post);
                                }
                            }
                        }
                        postAdapterSaves.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myphotos() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Post post = snapshot1.getValue(Post.class);
                    if(post.getPublisher().equals(profileId))
                    {
                        myPhotoList.add(post);
                    }
                }
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()){
                editProfile.setText("following");
            }else
            {
                editProfile.setText("follow");
            }}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Post post = snapshot1.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) counter++;
                }
                //Log.i("Value is ",String.valueOf(counter));
               posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getfollowerAndfollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user  = snapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(imageProfile);
                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}