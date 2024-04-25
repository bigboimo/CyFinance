package com.example.cyfinance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.example.cyfinance.util.SessionManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private EditText confirmEditText;   // define confirm edittext variable
    private Button signupButton;       // define signup button variable

    String username;

    String password;

    private String confirm;

    private String Response;
    private SessionManager session;
    private String userId;
    private String url = Constants.URL + "/users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /* initialize UI elements */
        usernameEditText = findViewById(R.id.signup_username_edt);  // link to username edtext in the Signup activity XML
        passwordEditText = findViewById(R.id.signup_password_edt);  // link to password edtext in the Signup activity XML
        confirmEditText = findViewById(R.id.signup_confirm_edt);    // link to confirm edtext in the Signup activity XML
        signupButton = findViewById(R.id.signup_signup_btn);  // link to signup button in the Signup activity XML



        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                confirm = confirmEditText.getText().toString();

                postRequest();
            }
        });
    }

    private void postRequest() {


        // Request a JSONObject response from the provided URL.
        JSONObject adduser = new JSONObject();
        try {

            // This is handled by the server. IDs should not be set by the client
            // adduser.put("id", 1);
            adduser.put("name", confirm);
            adduser.put("email", username);
            adduser.put("password", password);
            adduser.put("role", "user");
            System.out.println(adduser);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        JsonRequest createUser = new JsonRequest(
                Request.Method.POST,
                url,
                adduser, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("[Signup] HTTP Response: " + response);
                    JSONArray data = response.getJSONArray("data");
                    JSONObject headers = response.getJSONObject("headers");

                    Response = data.getJSONObject(0).getString("message");
                    if (Response.equals("User created"))
                        userId = headers.getString("Set-Cookie").split(";")[0].split("=")[1];
                    System.out.println("[login] Response message: " + Response);

                    // Go to main activity if login success
                    if(Response != null && Response.equals("User created")) {
                        // Set the session for the user
                        session = new SessionManager(getApplicationContext());
                        session.createLoginSession(userId);

                        // Go to the main activity
                        Intent intent = new Intent(SignupActivity.this, EarningsActivity.class);
                        startActivity(intent);  // go to MainActivity with the key-value data
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);;
            }
        }) {

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
                //                params.put("param1", "value1");
                //                params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(createUser);
    }
}
