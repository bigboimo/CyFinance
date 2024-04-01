package com.example.cyfinance.ui.Expenses;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.cyfinance.HomeActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.ui.Admin.AdminActivity;
import com.example.cyfinance.ui.Earnings.EarningsDActivity;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ExpensesDActivity extends AppCompatActivity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expensesd);
        NavigationBarView navView = findViewById(R.id.nav_view);

        navView.setSelectedItemId(R.id.navigation_expenses);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_earnings:
                        startActivity(new Intent(getApplicationContext(), EarningsDActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_admin:
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_expenses:
                        return true;
                }
                return false;
            }
        });
    }

}