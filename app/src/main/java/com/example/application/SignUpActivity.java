package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText usernameView;
    EditText oriName;
    EditText emailView;
    EditText passwordView;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    public void signingout(View view)
    {
        if(usernameView.getText().toString().equals("")||oriName.getText().toString().equals("")||emailView.getText().toString().equals("")||passwordView.getText().toString().equals(""))
        {
            Toast.makeText(SignUpActivity.this,"Enter Valid Details", Toast.LENGTH_LONG).show();
        }
        else if (passwordView.getText().toString().length()<6)
        {
            Toast.makeText(SignUpActivity.this,"Password is short!!!",Toast.LENGTH_LONG).show();
        }
        else
        {
            String username = usernameView.getText().toString();
            String name = oriName.getText().toString();
            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();
            Register(username,name,email,password);
        }
    }
    public void Register(final String username, final String name, final String email, final String password)
    {
        pd.setMessage("Please Wait!!");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("username",username);
                map.put("name",name);
                map.put("email",email);
                map.put("password",password);
                map.put("id",mAuth.getCurrentUser().getUid());
                map.put("bio","");
                map.put("imageurl","default");
                FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {pd.dismiss();
                            Toast.makeText(SignUpActivity.this,"Update the profile for better experience",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this,mainPageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this,"Failed",Toast.LENGTH_LONG).show();
            }
        });

    }
    public void movingtoSignIn(View view)
    {
        TextView tosignin = (TextView)findViewById(R.id.tosignin);
        Intent intent = new Intent(getApplicationContext() ,LogInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        usernameView = (EditText)findViewById(R.id.usernameView);
        oriName = (EditText)findViewById(R.id.oriName);
        emailView = (EditText)findViewById(R.id.emailView);
        passwordView = (EditText)findViewById(R.id.passwordView);
    }
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
            signingout(view);
        return false;
    }
    }
