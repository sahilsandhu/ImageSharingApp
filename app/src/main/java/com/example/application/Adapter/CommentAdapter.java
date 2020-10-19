package com.example.application.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.DialerKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.Model.Comment;
import com.example.application.Model.User;
import com.example.application.R;
import com.example.application.mainPageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    Context mContext;
    private List<Comment> mComments;
    FirebaseUser fUser;
    String postId;

    public CommentAdapter(Context mContext,List<Comment> mComments, String postId)
    {
        this.mComments = mComments;
        this.postId = postId;
        this.mContext= mContext;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
     return  new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComments.get(position);
        holder.comment.setText(comment.getComment());
        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user  = snapshot.getValue(User.class);
                holder.username.setText(user.getUsername());
                if(user.getImageurl().equals("default"))
                {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else
                {
                    Picasso.get().load(user.getImageurl()).into(holder.imageProfile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, mainPageActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, mainPageActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(comment.getPublisher().endsWith(fUser.getUid()))
                {
                    AlertDialog ad = new AlertDialog.Builder(mContext).create();
                    ad.setTitle("Do you want to delete");
                    ad.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).child(comment.getId()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(mContext,"Comment deleted Successfully!!",Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    });
                    ad.show();
                };
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView imageProfile;
        public TextView username;
        public  TextView comment;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);

        }
    }
}
