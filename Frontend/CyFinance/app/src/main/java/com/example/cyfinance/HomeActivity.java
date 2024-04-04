package com.example.cyfinance;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.cyfinance.ui.Admin.AdminActivity;
import com.example.cyfinance.ui.Change.AssetChange;
import com.example.cyfinance.ui.Change.LiabilityChange;
import com.example.cyfinance.ui.Earnings.EarningsDActivity;

import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;


import com.example.cyfinance.ui.Expenses.ExpensesDActivity;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.example.cyfinance.util.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    SessionManager session;
    String AssetResponse;
    int NetworthResponse;
    String LiabilitiesResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        session = new SessionManager(getApplicationContext());

        totalNetworth = findViewById(R.id.text_networth);

        Button refresh = findViewById(R.id.button_refresh);

        FloatingActionButton change = findViewById(R.id.change_options);

        totalAssets = findViewById(R.id.text_assets);

        totalLiabilities = findViewById(R.id.text_liabilities);

        NavigationBarView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);

        registerForContextMenu(findViewById(R.id.change_options));


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequest();
            }
        });
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_earnings:
                        startActivity(new Intent(getApplicationContext(), EarningsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_admin:
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_expenses:
                        startActivity(new Intent(getApplicationContext(), ExpensesActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_options, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.change_assets:
                startActivity(new Intent(getApplicationContext(), AssetChange.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.change_liability:
                startActivity(new Intent(getApplicationContext(), LiabilityChange.class));
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void getRequest() {

        // Convert input to JSONObject
        JSONObject postBody = null;

        JsonRequest request = new JsonRequest(
                Request.Method.GET,
                Constants.URL + "/users/" + session.getUserDetails().get("id"),
                postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("[Login] HTTP Response: " + response);
                            JSONArray data = response.getJSONArray("data");
                            JSONObject headers = response.getJSONObject("headers");

                            LiabilitiesResponse = Integer.toString(data.getJSONObject(0).getInt("liabilitiesTotal"));
                            AssetResponse = Integer.toString(data.getJSONObject(0).getInt("assetsTotal"));
                            NetworthResponse = (data.getJSONObject(0).getInt("assetsTotal") - data.getJSONObject(0).getInt("liabilitiesTotal"));

                            totalAssets.setText("$" + AssetResponse);
                            totalLiabilities.setText("$" + LiabilitiesResponse);
                            totalNetworth.setText("$" + NetworthResponse);

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
                headers.put("Cookie", "user-id=" + session.getUserDetails().get("id"));
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put();
                //params.put();
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
