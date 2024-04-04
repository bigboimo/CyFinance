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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EarningsActivity extends AppCompatActivity {
    private EditText PrimaryMonthlyIncome;
    private EditText SecondaryMonthlyIncome;
    private Button Submit;

    private String primary = "primaryMonthlyIncome";
    private String secondary = "secondaryMonthlyIncome";
    private String Response;
    private String getPrimary;
    private String getSecondary;
    private String getConfirm;
    private String url = "https://coms-309-038.class.las.iastate.edu/earnings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earnings_activity);

        PrimaryMonthlyIncome = findViewById(R.id.primary);
        SecondaryMonthlyIncome = findViewById(R.id.secondary);
        Button Submit = findViewById(R.id.submit); //links between submit in java code and then to submitin xml
        // (actual id of the submit button)
        //Then I need the clickListener which is needed to callback if the button(which is
        //the specific example of view in this case
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrimary = PrimaryMonthlyIncome.getText().toString();
                getSecondary = SecondaryMonthlyIncome.getText().toString();
                getConfirm = Submit.getText().toString();
                postRequest();
                Intent intent = new Intent(EarningsActivity.this, ExpensesActivity.class);
                startActivity(intent);
            }
        });

    }

    private void postRequest() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("primaryMonthlyIncome", primary);
            postBody.put("secondaryMonthlyIncome", secondary);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Response = response.getString("message");
                            System.out.println(Response);

                        } catch (JSONException e) {
                            e.printStackTrace();//if the JSON is malformed like if the data didn't have
                        }
                        System.out.println(response);
                        if(Response.equals("success")){
                            System.out.println("Data saved");
                            Intent intent = new Intent(EarningsActivity.this, ExpensesActivity.class);
                            startActivity(intent);
                        }
                        else{
                            System.out.println("Failed to set earnings");
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Response = error.toString();
                        System.out.println(error);//if authentication fails/or any other issue
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                // headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("PrimaryMonthlyIncome", primary);
                params.put("SecondaryMonthlyIncome", secondary);
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
