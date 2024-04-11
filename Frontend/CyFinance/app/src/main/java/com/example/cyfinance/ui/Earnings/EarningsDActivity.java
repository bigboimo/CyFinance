package com.example.cyfinance.ui.Earnings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.example.cyfinance.ChatConnect;
import com.example.cyfinance.HomeActivity;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cyfinance.LoginActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.VolleySingleton;
import com.example.cyfinance.ui.Admin.AdminActivity;
import com.example.cyfinance.ui.Change.LiabilityChange;
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

public class EarningsDActivity extends AppCompatActivity {

    private EditText primaryIncomeEditText;  // define username edittext variable
    private EditText secondaryIncomeEditText;  // define password edittext variable

    private String primary;
    private String url = "https://coms-309-038.class.las.iastate.edu/earnings";
    private String income1;

    private String income2;
    private String secondary;
    private String primary1;

    private String secondary1;
    private String id;
    private String Response;
    private String Earnings;
    // user Id set by cookie on login
    private String userId;
    Button Submit;

    // User session for other classes to track
    SessionManager session;
    boolean success = false;
    private String url = Constants.URL + "/earnings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earningsd);            // link to Login activity XML

        /* initialize UI elements */
        primaryIncomeEditText = findViewById(R.id.primary);
        secondaryIncomeEditText = findViewById(R.id.secondary);
        // define login button variable
        Button loginButton = findViewById(R.id.submit);    // link to login button in the Login activity XML
        // define signup button variable

        /* click listener on login button pressed */
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* grab strings from user inputs */
                primary = primaryIncomeEditText.getText().toString();
                secondary = secondaryIncomeEditText.getText().toString();
                postRequest();
                Intent intent = new Intent(EarningsDActivity.this, ExpensesDActivity.class);
                startActivity(intent);  // go to expenses
            }
        });

        /* click listener on signup button pressed */
        private void postRequest () {
            JSONObject postBody = new JSONObject();
            try {
                postBody.put("primaryMonthlyIncome", primary);
                postBody.put("secondaryMonthlyIncome", secondary);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            // Assuming 'userId' is already set. Ensure it's the correct user ID for which earnings are to be fetched.
//            postParams.put("id", userId);
//            // Optionally, add primaryMonthlyIncome and secondaryMonthlyIncome if required by the backend, though it seems unusual for a fetch operation.

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.URL + "/earnings",
                    postBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Response = response.getString("message");
                                System.out.println(Response);

                            } catch (JSONException e) {
                                e.printStackTrace();//if the JSON is malformed like if the data didn't have
                            }
                            System.out.println(response);
                            if (Response.equals("success")) {
                                System.out.println("Data saved");
                                Intent intent = new Intent(EarningsDActivity.this, ExpensesDActivity.class);
                                startActivity(intent);
                            } else {
                                System.out.println("Failed to set earnings");
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Response = error.toString();
                            System.out.println(error);//if authentication fails/or any other issue
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    // headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                    // headers.put("Content-Type", "application/json");
                    return headers;
                }

            });

        }

        private void getRequest () {

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

                                income1 = Integer.toString(data.getJSONObject(0).getInt("primaryIncomeEditText"));
                                income2 = (data.getJSONObject(0).getInt("secondaryMonthlyIncome");

                                primaryIncomeEditText.setText("$" + income1);
                                secondaryIncomeEditText.setText("$" + income2);
                            } catch (JSONException e) {
                                System.out.println(e);
                            }
                        }
                    });
        }


    }
}
