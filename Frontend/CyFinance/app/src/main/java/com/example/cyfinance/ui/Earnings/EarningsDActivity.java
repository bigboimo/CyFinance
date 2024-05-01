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
    private EditText primaryIncomeEditText, secondaryIncomeEditText;
    private Button updateButton;
    private SessionManager session;
    private NavigationBarView navView;
    // UI references for income fields and session management for user data.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earningsd);
        // Initialize the session and UI elements.
        session = new SessionManager(getApplicationContext());
        primaryIncomeEditText = findViewById(R.id.primary);
        secondaryIncomeEditText = findViewById(R.id.secondary);
        updateButton = findViewById(R.id.update_btn);
        navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_earnings);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int primaryIncome = Integer.parseInt(primaryIncomeEditText.getText().toString());
                    int secondaryIncome = Integer.parseInt(secondaryIncomeEditText.getText().toString());
                    postEarnings(primaryIncome, secondaryIncome);
                    getEarnings();
                    updateEarnings(primaryIncome, secondaryIncome);
                } catch (NumberFormatException e) {
                    //toast format basically gives feedback about the operation, here if JSON post isn't working
                    Toast.makeText(EarningsDActivity.this, "Invalid input. Please enter numeric values.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_admin:
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_expenses:
                        startActivity(new Intent(getApplicationContext(), ExpensesDActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_earnings:
                        return true;
                }
                return false;
            }
        });
    }
    // Retrieves earnings data for the current user based on their ID.
    private void postEarnings(int primaryIncome, int secondaryIncome) {
        // The URL includes the user's ID to retrieve their specific earnings data.
        String url = Constants.URL + Constants.CREATE_EARNINGS;

        JSONObject postBody = new JSONObject();
        try {
            // This is where you would handle the JSON response and update the UI accordingly.
            postBody.put("primaryMonthlyIncome", primaryIncome);
            postBody.put("secondaryMonthlyIncome", secondaryIncome);
        } catch (JSONException e) {
            // Error handling, such as showing a toast message if the request fails.
            //toast format basically gives feedback about the operation, here if JSON post isn't working
            Toast.makeText(this, "Error creating JSON for POST request.", Toast.LENGTH_LONG).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postBody,
                response -> Toast.makeText(EarningsDActivity.this, "Earnings created successfully!", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(EarningsDActivity.this, "Error creating earnings: " + error.toString(), Toast.LENGTH_LONG).show()
        ) {
            //This Map below is to authorize headers to secure endpoints
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getUserDetails().get("token"));
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void updateEarnings(int primaryIncome, int secondaryIncome) {
        // Endpoint includes the user's ID to identify which earnings record to update.
        String url = Constants.URL + Constants.UPDATE_EARNINGS;
        // Construct the JSON payload with the updated earnings data.
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("primaryMonthlyIncome", primaryIncome);
            postBody.put("secondaryMonthlyIncome", secondaryIncome);
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating JSON for PUT request.", Toast.LENGTH_LONG).show();
            return;
        }
        // The PUT request is used to update the existing data on the server, which is what the request is for
        JsonObjectRequest request0 = new JsonObjectRequest(Request.Method.PUT, url, postBody,
                response -> Toast.makeText(EarningsDActivity.this, "Earnings updated successfully!", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(EarningsDActivity.this, "Error updating earnings: " + error.toString(), Toast.LENGTH_LONG).show()
        ) {
            //header to authenticate for the endpts secured using the session
            //the session is for: using sessionManager(interface that manages which data is preferred)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getUserDetails().get("token"));
                return headers;
            }
        };
       //This is to manage the different volley requests asynchronously(each at its own time)
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request0);
    }

    private void getEarnings() {
        String url = Constants.URL + Constants.GET_EARNINGS_BY_ID + "?id=" + session.getUserDetails().get("id");

        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int primaryIncome = response.getInt("primaryMonthlyIncome");
                        int secondaryIncome = response.getInt("secondaryMonthlyIncome");
                        primaryIncomeEditText.setText(String.valueOf(primaryIncome));
                        secondaryIncomeEditText.setText(String.valueOf(secondaryIncome));
                        Toast.makeText(EarningsDActivity.this, "Earnings retrieved successfully!", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(EarningsDActivity.this, "Failed to retrieve earnings: " + error.toString(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getUserDetails().get("token"));
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request1);
    }

    private void deleteEarnings() {
        // Assuming 'id' is a String or integer variable that holds the ID of the earnings you want to delete.
        String id = session.getUserDetails().get("id"); // Or however you obtain the 'id'
        String url = Constants.URL + "/earnings/" + id;

        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    // Handle response
                    Toast.makeText(EarningsDActivity.this, "Earnings deleted successfully.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    Toast.makeText(EarningsDActivity.this, "Error deleting earnings: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // Add your headers here, typically Authorization
                headers.put("Authorization", "Bearer " + session.getUserDetails().get("token"));
                return headers;
            }
//            // Assuming 'userId' is already set. Ensure it's the correct user ID for which earnings are to be fetched.
//            postParams.put("id", userId);
//            // Optionally, add primaryMonthlyIncome and secondaryMonthlyIncome if required by the backend, though it seems unusual for a fetch operation.

            @Override
            public int getMethod() {
                return Method.DELETE; // Ensure that DELETE is used as the request method
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteRequest);

    }
}
