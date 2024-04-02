package com.example.cyfinance;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.cyfinance.ui.Admin.AdminActivity;
import com.example.cyfinance.ui.Earnings.EarningsDActivity;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;


import com.example.cyfinance.ui.Expenses.ExpensesDActivity;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.example.cyfinance.util.SessionManager;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    TextView totalNetworth;
    TextView totalAssets;
    TextView totalLiabilities;
    String Response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        TextView totals = findViewById(R.id.text_totals);
        totalNetworth = findViewById(R.id.text_networth);

        totalAssets = findViewById(R.id.text_assets);

        totalLiabilities = findViewById(R.id.text_liabilities);

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
        private void getRequest() {

            // Convert input to JSONObject
            JSONObject postBody = null;

            JsonRequest request = new JsonRequest(
                    Request.Method.GET,
                    Constants.URL + "/networth",
                    postBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                            } catch (JSONException e) {
                                System.out.println(e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Response = error.getMessage();
                            Response = error.toString();
                            System.out.println(error);

                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    //                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                    //                headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //params.put();

                    return params;
                }
            };

            // Adding request to request queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
    }
}