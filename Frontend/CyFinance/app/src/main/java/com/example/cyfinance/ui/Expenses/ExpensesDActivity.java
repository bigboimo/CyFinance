package com.example.cyfinance.ui.Expenses;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cyfinance.ExpensesActivity;
import com.example.cyfinance.HomeActivity;
import com.example.cyfinance.VolleySingleton;
import com.example.cyfinance.util.SessionManager;
import com.example.cyfinance.R;
import com.example.cyfinance.ui.Admin.AdminActivity;
import com.example.cyfinance.ui.Earnings.EarningsDActivity;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExpensesDActivity extends AppCompatActivity {
    //It was interesting to see how these are implemented in different classes(the session variable)
    private String Response2;
    private EditText Food;
    private EditText RentBills;
    private EditText School;
    private EditText Other;
    private EditText Extra;
    private Button submitbutton;
    SessionManager session;//instance of SessionManager

    private String ExpensesActivityResponse;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expensesd);
        session = new SessionManager(getApplicationContext());
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
    private void postExpenses() {
        // Endpoint URL for posting the expenses data, including the user ID as a query parameter
        String url = Constants.URL + "/expenses/" + session.getUserDetails().get("id");

        // Gather the data to be sent to the server from the EditText fields
        Map<String, String> params = new HashMap<>();
        params.put("food", Food.getText().toString());
        params.put("rentandbills", RentBills.getText().toString());
        params.put("school", School.getText().toString());
        params.put("other", Other.getText().toString());
        params.put("misc", Extra.getText().toString());

        // Convert the Map to a JSONObject
        JSONObject postBody = new JSONObject(params);

        // Create a JsonObjectRequest for the POST request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postBody,
                response -> {
                    // Handle the server response for a successful request
                    Toast.makeText(ExpensesDActivity.this, "Expenses updated successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle any errors encountered during the request
                    Toast.makeText(ExpensesDActivity.this, "Error updating expenses: " + error.toString(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set any required headers for the request, such as Authorization headers
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getUserDetails().get("token"));
                headers.put("Content-Type", "application/json"); // Set the content type to JSON
                return headers;
            }
        };

        // Add the request to the Volley request queue for execution
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
        private void updateExpenses() {
            // Endpoint URL for updating the expenses data
            String url = Constants.URL + "/expenses";

            // Create a JSONObject with the expenses data
            JSONObject postBody = new JSONObject();
            try {
                // Here we assume the EditTexts are properly initialized and contain the expenses data
                postBody.put("food", Integer.parseInt(Food.getText().toString()));
                postBody.put("rentandbills", Integer.parseInt(RentBills.getText().toString()));
                postBody.put("school", Integer.parseInt(School.getText().toString()));
                postBody.put("otherneeds", Integer.parseInt(Other.getText().toString())); // Note: Ensure the key matches with your backend expectation
                postBody.put("misc", Integer.parseInt(Extra.getText().toString()));
            } catch (JSONException e) {
                // Handle the JSONException, possibly by showing an error message to the user
                Toast.makeText(this, "Error creating JSON for PUT request.", Toast.LENGTH_LONG).show();
                return;
            }

            // Create a JsonObjectRequest for the PUT request
            JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.PUT, url, postBody,
                    response -> {
                        // Handle the server response for a successful request
                        Toast.makeText(ExpensesDActivity.this, "Expenses updated successfully!", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        // Handle any errors encountered during the request
                        Toast.makeText(ExpensesDActivity.this, "Error updating expenses: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
            ) {
                //This map is to get Headers and it authorizes that with the session thing
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    // Set any required headers for the request, such as Authorization headers
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + session.getUserDetails().get("token"));
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Add the request to the Volley request queue for execution
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request1);
        }



    private void getRequest() {
        // The URL for the GET request is constructed using the base URL and appending the user ID.
        String url = Constants.URL + "/expenses/" + session.getUserDetails().get("id");

        // Create a JsonObjectRequest because we expect a JSONObject response from the server.
        JsonObjectRequest request2 = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, // For a GET request, the third parameter (the request body) is null.
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Here, we assume that the response JSON includes an array named "data".
                            // We extract the "earnings" integer from the first object in this array.
                            JSONArray data = response.getJSONArray("data");
                            // If your JSON response structure is different, you need to adjust this line accordingly.
                            ExpensesActivityResponse = data.getJSONObject(0).getString("earnings");
                            // Now you can update the UI or perform other actions with the "earnings" data.
                        } catch (JSONException e) {
                            // It's good practice to log the exception or show an error message to the user.
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there is a network error or the server responds with an error,
                        // you can notify the user that something went wrong.
                        Toast.makeText(ExpensesDActivity.this, "Error fetching expenses: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                    //the length short
                }
        ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                    // If your request requires custom headers, set them here.
                    // For example, you might need to set an authorization token header.
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + session.getUserDetails().get("token"));
                    return headers;
            }
        };

        // Add the request to the Volley request queue to be executed.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request2);
    }
    private void deleteExpenses(){
        String id = session.getUserDetails().get("id"); // Or however you obtain the 'id'
        String url = Constants.URL + "/expenses/" + id;
        JsonObjectRequest delete =  new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    // Handle response
                    Toast.makeText(ExpensesDActivity.this, "Expenses deleted successfully.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    Toast.makeText(ExpensesDActivity.this, "Error deleting expenses: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // Add your headers here, typically Authorization
                headers.put("Authorization", "Bearer " + session.getUserDetails().get("token"));
                return headers;
            }

            @Override
            public int getMethod() {
                return Method.DELETE; // Ensure that DELETE is used as the request method
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(delete);

    }

}

