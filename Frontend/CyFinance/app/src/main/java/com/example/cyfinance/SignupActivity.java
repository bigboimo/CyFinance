package com.example.cyfinance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private EditText confirmEditText;   // define confirm edittext variable
    private Button signupButton;       // define signup button variable

    private String username;

    private String password;

    private String confirm;

    private String Response;
    private String url = "http://coms-309-038.class.las.iastate.edu:8080/users";
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

                if(Response != null && Response.equals("User created")) {
                    System.out.println("Hello");
                    Intent intent = new Intent(SignupActivity.this, NetworthActivity.class);
                    startActivity(intent);
                }
//                if (password.equals(confirm)) {
//                    Toast.makeText(getApplicationContext(), "Signing up", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    private void postRequest() {


        // Request a JSONObject response from the provided URL.
        JSONObject adduser = new JSONObject();
        try {

            adduser.put("id", 1);
            adduser.put("name", confirm);
            adduser.put("email", username);
            adduser.put("password", password);
            adduser.put("role", "user");
            System.out.println(adduser);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest createUser = new JsonObjectRequest(
                Request.Method.POST,
                url,
                adduser, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // String response can be converted to JSONObject via
                        //JSONObject object = response;
                        try {
                            Response = response.getString("message");
                            System.out.println(Response);
                        }
                        catch(JSONException e) {

                        }

                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //tvResponse.setText("That didn't work!" + error.toString());
                        System.out.println(error);
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
