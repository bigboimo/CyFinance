package com.example.cyfinance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ExpensesActivity extends AppCompatActivity {
    private EditText FoodEditText;
    private EditText RentBillsEditText;
    private EditText SchoolEditText;
    private EditText OtherNecessitiesEditText;
    private EditText Extras;
    private Button submitbutton;

    private String Food;
    private String RentBills1;
    private String School;
    private String OtherNecessities;
    private String Response;
    private String url = "https://coms-309-038.class.las.iastate.edu/expenses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_activity);

        FoodEditText = findViewById(R.id.food);
        RentBillsEditText = findViewById(R.id.Rent);
        SchoolEditText = findViewById(R.id.school);
        OtherNecessitiesEditText = findViewById(R.id.OtherNecessities);
        Extras = findViewById(R.id.Extras);
        submitbutton = findViewById(R.id.submit);

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Food = FoodEditText.getText().toString();
                RentBills1 = RentBillsEditText.getText().toString();
                School = SchoolEditText.getText().toString();
                OtherNecessities = OtherNecessitiesEditText.getText().toString();

                postRequest();
            }
        });
    }

    private void postRequest() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("food", Food);
            postBody.put("Rent/Bills", RentBills1);
            postBody.put("school", School);
            postBody.put("Other Necessities", OtherNecessities);
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
                            Log.d("Response", Response);
                            if (Response != null && Response.equals("success")) {
                                Intent intent = new Intent(ExpensesActivity.this, HomeActivity.class);
                                intent.putExtra("Food", Food);
                                intent.putExtra("Rent/Bills", RentBills1);
                                intent.putExtra("School", School);
                                intent.putExtra("Other Necessities", OtherNecessities);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.toString();
                        Log.e("Error", errorMessage);
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
