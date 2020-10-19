package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    EditText usernameView;
    EditText passwordView;
    Button logButton;
    TextView goingtosignup;
    FirebaseAuth mAuth;
    public void loggingIn(View view)
    {

        if(usernameView.getText().toString().equals("")||passwordView.getText().toString().equals("")){
            Toast.makeText(LogInActivity.this,"Enter Username/Password",Toast.LENGTH_LONG).show();
        }
        else
        {
            String name = usernameView.getText().toString();
            String pass = passwordView.getText().toString();
            login(name,pass);
        }

    }
    public void login(String name,String pass)
    {
        mAuth.signInWithEmailAndPassword(name,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {   Toast.makeText(LogInActivity.this,"Welcome",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LogInActivity.this,mainPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText((LogInActivity.this), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void movingtoSignUp(View view)
    {
        goingtosignup = (TextView)findViewById(R.id.goingtosignup);
        Intent intent = new Intent(getApplicationContext() ,SignUpActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        usernameView = (EditText)findViewById(R.id.usernameView);
        passwordView = (EditText)findViewById(R.id.passwordView);
    }
    public boolean onKey(View view, int i, KeyEvent keyEvent)
    {
        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN)
            loggingIn(view);
        return false;}
    }
