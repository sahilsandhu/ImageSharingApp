package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {



    LinearLayout sandLinear;
    ImageView sandImageView;
    Button loginButton,signupButton;
    public void loginFunc(View view)
    {
        loginButton = (Button)findViewById(R.id.loginButton);
        Intent intent = new Intent(getApplicationContext(),LogInActivity.class);
        startActivity(intent);
        finish();

    }
    public void signupFunc(View view)
    {
        signupButton = (Button)findViewById(R.id.signupButton);
        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        loginButton = (Button)findViewById(R.id.loginButton);
        signupButton = (Button)findViewById(R.id.signupButton);
        sandLinear = (LinearLayout)findViewById(R.id.sandLinear);
        sandLinear.setVisibility(View.INVISIBLE);
        sandImageView = (ImageView)findViewById(R.id.sandImageView);
        sandImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                sandImageView.setVisibility(View.INVISIBLE);
            }
        },2000);
        sandLinear.postDelayed(new Runnable() {
            @Override
            public void run() {
                sandLinear.setVisibility(View.VISIBLE);
            }
        },2000);
    }
}