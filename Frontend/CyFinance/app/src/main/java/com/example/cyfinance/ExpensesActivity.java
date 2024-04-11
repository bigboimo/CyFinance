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
        //The line below basically connects this java file to its
        //relevant xml file
        setContentView(R.layout.expenses_activity);
       //In each of the lines below: basically the edit text declared
       //connects to its relevant xml id
        FoodEditText = findViewById(R.id.food);
        RentBillsEditText = findViewById(R.id.Rent);
        SchoolEditText = findViewById(R.id.school);
        OtherNecessitiesEditText = findViewById(R.id.OtherNecessities);
        Extras = findViewById(R.id.Extras);
        submitbutton = findViewById(R.id.submit);
        //Set OnclickListener on the submit button
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This basically gets the text from the EditText
                // and converts it to a string
                Food = FoodEditText.getText().toString();
                RentBills1 = RentBillsEditText.getText().toString();
                School = SchoolEditText.getText().toString();
                OtherNecessities = OtherNecessitiesEditText.getText().toString();

                postRequest();

            }
        });
    }
   //this does 2 things: create and send the postRequest
    private void postRequest() {
        //This JSON below holds the data we'll send
        JSONObject postBody = new JSONObject();
        try {
            //These key value pairs correspond to the user input
            postBody.put("food", Food);
            postBody.put("Rent/Bills", RentBills1);
            postBody.put("school", School);
            postBody.put("Other Necessities", OtherNecessities);
        } catch (JSONException e) {
            //This prints a list of active method calls when the error is thrown
            e.printStackTrace();
        }
        //Request that gets in return a JSON response
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //This is when the server responds to the post request
                        try {
                            //parses the message from the response
                            Response = response.getString("message");
                            Log.d("Response", Response);
                            if (Response != null && Response.equals("success")) {
                                //if we do get a response we'll be taken to the homeactivity
                                Intent intent = new Intent(ExpensesActivity.this, HomeActivity.class);
                                intent.putExtra("Food", Food);
                                intent.putExtra("Rent/Bills", RentBills1);
                                intent.putExtra("School", School);
                                intent.putExtra("Other Necessities", OtherNecessities);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            //when parsing the JSON and we get an error, print the methods that were running
                            e.printStackTrace();
                        }
                        System.out.println(response);
                        if(Response.equals("success")){
                            System.out.println("Data saved");
                            Intent intent = new Intent(ExpensesActivity.this, HomeActivity.class);
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
                        //This is called when there's error with network request(helps narrow down the issue)
                        String errorMessage = error.toString();
                        Log.e("Error", errorMessage);
                        //This makes the error show up in logcat
                    }
                }
        );
        //in this context the JSON object request is added to volley queue for execution
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
