package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.example.application.Fragment.HomeFragment;
import com.example.application.Fragment.NotificationFragment;
import com.example.application.Fragment.ProfileFragment;
import com.example.application.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class mainPageActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_page);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;
                    case R.id.nav_add:
                        selectorFragment = null;
                        startActivity(new Intent(mainPageActivity.this,PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        selectorFragment = new NotificationFragment();
                        break;
                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        break;
                }
                if(selectorFragment!=null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentConstain,selectorFragment).commit();

                }
                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if(intent != null)
        {
            String profileId = intent.getString("publisherId");
            getSharedPreferences("Profile", MODE_PRIVATE).edit().putString("profileId",profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentConstain,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }
        else
        {



        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentConstain,new HomeFragment()).commit();
    }
    }}
