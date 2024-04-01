package com.example.cyfinance;

import android.os.Bundle;

import com.example.cyfinance.ui.Admin.AdminActivity;
import com.example.cyfinance.ui.Earnings.EarningsDActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;


import com.example.cyfinance.ui.Expenses.ExpensesDActivity;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationBarView navView = findViewById(R.id.nav_view);

        navView.setSelectedItemId(R.id.navigation_home);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_earnings:
                        startActivity(new Intent(getApplicationContext(), EarningsDActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_admin:
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_expenses:
                        startActivity(new Intent(getApplicationContext(), ExpensesDActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }
}