package com.example.application.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.Fragment.PostDetailFragment;
import com.example.application.Fragment.ProfileFragment;
import com.example.application.Model.Notification;
import com.example.application.Model.Post;
import com.example.application.Model.User;
import com.example.application.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ConcurrentModificationException;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    Context mContext;
    List<Notification>  mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification)
    {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Notification notification = mNotification.get(position);

        getUser(holder.imageProfile,holder.username,notification.getUserid());

        //holder.comment.setText(notification.getText());

        if(notification.isIsPost())
        {
        holder.postImage.setVisibility(View.VISIBLE);
        getPostImage(holder.postImage, notification.getPostid());
        }else
        {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isIsPost())
                {
                    mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postid",notification.getPostid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragmentConstain,new PostDetailFragment()).commit();

                }else
                {
                    mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",notification.getUserid()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragmentConstain,new ProfileFragment()).commit();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    private void getPostImage(final ImageView imageView, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Picasso.get().load(post.getImageurl()).placeholder(R.mipmap.ic_launcher).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getUser(final ImageView imageView, final TextView textView, String userId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageurl().equals("default"))
                {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(imageView);
            }
                textView.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imageProfile;
        public ImageView postImage;
        public TextView username;
        public TextView comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
