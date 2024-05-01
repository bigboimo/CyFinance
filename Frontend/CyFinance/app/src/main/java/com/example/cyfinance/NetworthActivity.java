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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//Requests need to be fixed
public class NetworthActivity extends AppCompatActivity {

    private EditText assetEditText;

    private String Response;

    private EditText liabiltiesEditText;

    private Button nextButton;

    private int assetNum;

    private int liabilityNum;

    private String url = Constants.URL + "/networth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networth);

        assetEditText = findViewById(R.id.asset_edt_txt);
        liabiltiesEditText = findViewById(R.id.liabilities_edt_txt);
        nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                assetNum = Integer.parseInt(assetEditText.getText().toString());
                liabilityNum = Integer.parseInt(liabiltiesEditText.getText().toString());

                postRequest();

                if(Response != null && Response.equals("Net worth created")) {
                    System.out.println("Hello");
                    Intent intent = new Intent(NetworthActivity.this, EarningsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void postRequest() {


        // Request a JSONObject response from the provided URL.
        JSONObject networth = new JSONObject();
        try {

            networth.put("assets", assetNum);
            networth.put("liabilities", liabilityNum);
            System.out.println(networth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest createUser = new JsonObjectRequest(
                Request.Method.POST,
                url,
                networth, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                // String response can be converted to JSONObject via
                //JSONObject object = response;
                try {
                    Response = response.getString("message");
                    System.out.println(Response);
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

