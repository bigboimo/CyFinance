package com.example.cyfinance.ui.Change;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.cyfinance.HomeActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.VolleySingleton;
import com.example.cyfinance.ui.Admin.AdminActivity;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.example.cyfinance.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PasswordChange extends AppCompatActivity {
    SessionManager session;
    String Response;
    EditText nPassword;
    String password;
    String username;
    Bundle extras;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordchange);

        extras = getIntent().getExtras();

        if(extras != null) {
            username = extras.getString("username");
        }

        session = new SessionManager(getApplicationContext());

        Button next = findViewById(R.id.next_button);

        nPassword = findViewById(R.id.pass_edt_txt);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = nPassword.getText().toString();
                putRequest();
            }
        });
    }
    private void putRequest() {

        // Convert input to JSONObject
        JSONObject postBody = new JSONObject();
        try {

            // This is handled by the server. IDs should not be set by the client
            // adduser.put("id", 1);
            postBody.put("email", username);
            postBody.put("password", password);
            System.out.println(postBody);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        JsonRequest request = new JsonRequest(
                Request.Method.PUT,
                Constants.URL + "/users",
                postBody,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("[Login] HTTP Response: " + response);
                            JSONArray data = response.getJSONArray("data");
                            JSONObject headers = response.getJSONObject("headers");
                            Response = data.getJSONObject(0).getString("message");

                            if (Response != null && Response.equals("User modified")) {
                                Intent intent = new Intent(PasswordChange.this, AdminActivity.class);
                                //Toast.makeText(PasswordChange.this, "Password Change");
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
                headers.put("Cookie", "user-id=" + session.getUserDetails().get("id"));
                return headers;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
