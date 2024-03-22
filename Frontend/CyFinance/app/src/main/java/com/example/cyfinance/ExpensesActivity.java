//package com.example.cyfinance;
//import android.util.Log;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.navigation.Navigator;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ExpensesActivity extends AppCompatActivity {
//    private EditText Food;
//    private EditText params;
//    private EditText headers;
//    private EditText RentBills;
//
//    private EditText School;
//
//    private EditText OtherNecessities;
//
//    private EditText Extras;
//    private String primary;
//    private String secondary;
//    private String Submit;
//    private String url = "https://coms-309-038.class.las.iastate.edu/expenses";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.expenses_activity);            // link to Login activity XML
//
//        /* initialize UI elements */
//        Food = findViewById(R.id.food);
//        RentBills = findViewById(R.id.Rent);
//        School= findViewById(R.id.school);    // link to login button in the Login activity XML
//        OtherNecessities = findViewById(R.id.OtherNecessities);  // link to signup button in the Login activity XML
//        Extras = findViewById(R.id.extras)
//        /* click listener on login button pressed */
//       Submit1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /* grab strings from user inputs */
//                Food= Food.toString();
//                RentBills1 = RentBills1.toString();
//                School1 = School1.toString();
//                OtherNecessities1= OtherNecessities1.toString();
//
//                postRequest();//we just do it because the paper will only ask for data for the backend
//                //sleep(10);
//                /* when login button is pressed, use intent to switch to Login Activity */
//
//                if(Response != null && Response.equals("success")) {
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("USERNAME", username);  // key-value to pass to the MainActivity
//                    intent.putExtra("PASSWORD", password);  // key-value to pass to the MainActivity
//                    startActivity(intent);  // go to MainActivity with the key-value data
//                }
//
//            }
//        });
//
//
//        /* click listener on signup button pressed */
//        signupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /* when signup button is pressed, use intent to switch to Signup Activity */
//                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
//                startActivity(intent);  // go to SignupActivity
//
//            }
//        });
//    }
//
//    private void postRequest() {
//
//        // Convert input to JSONObject
//        JSONObject postBody = null;
//
//        JsonObjectRequest request = new JsonObjectRequest(
//                Request.Method.POST,
//                url + "?username=" + username + "&password=" + password,
//                postBody,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //Response = response.toString();
//                        try {
//                            Response = response.getString("message");
//                            System.out.println(Response);
//                        }
//                        catch(JSONException e) {
//
//                        }
//                        //System.out.println(Response);
//                        System.out.println(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // Store the error message in a variable
//                        String errorMessage = error.toString();
//
//                        // Print the error message using Android's logging system
//                        Log.e("$ Error", errorMessage);
//                    }
//
//
//                }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                //                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                //                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", username);
//                params.put("password", password);
//                return params;
//            }
//        };
//
//        // Adding request to request queue
//        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

