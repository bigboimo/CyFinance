package com.example.cyfinance;

import android.os.Bundle;

import com.example.cyfinance.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationBarView navView = findViewById(R.id.nav_view);

        navView.setSelectedItemId(R.id.navigation_home);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), HomeFragment.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_home:
                        return true;
//                    case R.id.about:
//                        startActivity(new Intent(getApplicationContext(), About.class));
//                        overridePendingTransition(0, 0);
//                        return true;
                }
                return false;
            }
        });

    }
}