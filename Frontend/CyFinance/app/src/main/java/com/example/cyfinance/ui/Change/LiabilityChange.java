package com.example.cyfinance.ui.Change;

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
import com.example.cyfinance.HomeActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.VolleySingleton;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.example.cyfinance.util.SessionManager;
import com.example.cyfinance.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LiabilityChange extends AppCompatActivity {

    String Response;
    SessionManager session;

    String liabilityChange;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liabiltieschange);

        session = new SessionManager(getApplicationContext());
        Button next = findViewById(R.id.next_button);

        EditText liabilites = findViewById(R.id.liabilities_edt_txt);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liabilityChange = liabilites.getText().toString();
                putRequest();
            }
        });
    }

    private void putRequest() {

        // Convert input to JSONObject
        JSONObject postBody = null;

        JsonRequest request = new JsonRequest(
                Request.Method.PUT,
                Constants.URL + "/users/" + session.getUserDetails().get("id"),
                postBody,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("[Login] HTTP Response: " + response);
                            JSONArray data = response.getJSONArray("data");
                            JSONObject headers = response.getJSONObject("headers");

                            if (Response != null && Response.equals("User modified")) {
                                Intent intent = new Intent(LiabilityChange.this, HomeActivity.class);
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

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("liabiltiestotal", liabilityChange);
                //params.put();
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}

