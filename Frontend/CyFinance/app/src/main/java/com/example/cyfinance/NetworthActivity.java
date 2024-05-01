package com.example.cyfinance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//Requests need to be fixed
public class NetworthActivity extends AppCompatActivity {

    private EditText assetEditText;

    private String ResponseAsset;
    private String ResponseLiability;

    private EditText liabiltiesEditText;

    private Button nextButton;

    private int assetNum;

    private int liabilityNum;

    SessionManager session;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networth);
        session = new SessionManager(getApplicationContext());

        assetEditText = findViewById(R.id.asset_edt_txt);
        liabiltiesEditText = findViewById(R.id.liabilities_edt_txt);
        nextButton = findViewById(R.id.next_button);

        url = Constants.URL + "/users/" + session.getUserDetails().get("id");

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                assetNum = Integer.parseInt(assetEditText.getText().toString());
                liabilityNum = Integer.parseInt(liabiltiesEditText.getText().toString());

                postRequestAsset();
                postRequestLiability();

                if(ResponseAsset != null && ResponseLiability != null
                        && ResponseAsset.equals("User modified")
                        && ResponseLiability.equals("User modified")) {
                    System.out.println("Hello");
                    Intent intent = new Intent(NetworthActivity.this, EarningsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void postRequestAsset() {


        // Request a JSONObject response from the provided URL.
        JsonObjectRequest assetChange = new JsonObjectRequest(
                Request.Method.PUT,
                url + "/assettotal/" + assetNum,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                // String response can be converted to JSONObject via
                //JSONObject object = response;
                try {
                    ResponseAsset = response.getString("message");
                    System.out.println(ResponseAsset);
                } catch (JSONException e) {
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
                headers.put("Cookie", "user-id=" + session.getUserDetails().get("id"));
                return headers;
            }
        };
        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(assetChange);
    }

    private void postRequestLiability() {


        // Request a JSONObject response from the provided URL.
        JsonObjectRequest liabilitychange = new JsonObjectRequest(
                Request.Method.PUT,
                url + "/liabilitiestotal/" + liabilityNum,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                // String response can be converted to JSONObject via
                //JSONObject object = response;
                try {
                    ResponseLiability = response.getString("message");
                    System.out.println(ResponseLiability);
                } catch (JSONException e) {}

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
                headers.put("Cookie", "user-id=" + session.getUserDetails().get("id"));
                return headers;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(liabilitychange);
    }
}


