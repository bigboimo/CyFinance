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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.example.cyfinance.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable

    String username;
    String password;

    String Response;

    // user Id set by cookie on login
    String userId;

    // User session for other classes to track
    SessionManager session;
    boolean success = false;
    private String url = Constants.URL + "/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);            // link to Login activity XML

        /* initialize UI elements */
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        // define login button variable
        Button loginButton = findViewById(R.id.login_btn);    // link to login button in the Login activity XML
        // define signup button variable
        Button signupButton = findViewById(R.id.login_signup_btn);  // link to signup button in the Login activity XML
        Button chatButton = findViewById(R.id.chat_btn); //link to chat button within the xml
        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                postRequest();

            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);  // go to SignupActivity
            }

        });

        chatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                Intent intent = new Intent(LoginActivity.this, ChatConnect.class);
                startActivity(intent);  // go to SignupActivity
            }


        });

    }

    private void postRequest() {

        // Convert input to JSONObject
        JSONObject postBody = null;

        JsonRequest request = new JsonRequest(
                Request.Method.POST,
                url + "?email=" + username + "&password=" + password,
                postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("[Login] HTTP Response: " + response);
                            JSONArray data = response.getJSONArray("data");
                            JSONObject headers = response.getJSONObject("headers");

                            Response = data.getJSONObject(0).getString("message");
                            if (Response.equals("success"))
                                userId = headers.getString("Set-Cookie").split(";")[0].split("=")[1];
                            System.out.println("[login] Response message: " + Response);

                            // Go to main activity if login success
                            if (Response != null && Response.equals("success")) {
                                // Set the session for the user
                                session = new SessionManager(getApplicationContext());
                                session.createLoginSession(userId);

                                // Go to the main activity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
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
                params.put("email", username);
                params.put("password", password);
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

}


