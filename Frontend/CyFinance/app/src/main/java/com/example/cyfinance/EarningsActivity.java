//package com.example.cyfinance;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class EarningsActivity extends AppCompatActivity {
//    private EditText PrimaryMonthlyIncome;
//
//    private EditText SecondaryMonthlyIncome;
//
//    private Button Submit;
//
//    private double yourvaluehere1;
//
//    private double yourvaluehere2;
//
//
//
//    private String url = "https://coms-309-038.class.las.iastate.edu/earnings";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.expenses_activity);            // link to Login activity XML
//
//        /* initialize UI elements */
//         PrimaryMonthlyIncomerimary= findViewById(R.id.primary);
//         SecondaryMonthlyIncome= findViewById(R.id.secondary);
//        /* click listener on login button pressed */
//        Submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /* grab strings from user inputs */
//                Submit= Submit.toString();
//
//
//                postRequest();//we just do it because the paper will only ask for data for the backend
//                //sleep(10);
//                /* when login button is pressed, use intent to switch to Login Activity */
//                private void postRequest() {
//
//                    // Convert input to JSONObject
//                    JSONObject postBody = null;
//
//                    JsonObjectRequest request = new JsonObjectRequest(
//                            Request.Method.POST,
//                            url + "?username=" + username + "&password=" + password,
//                            postBody,
//                            new Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    //Response = response.toString();
//                                    try {
//                                        Response = response.getString("message");
//                                        System.out.println(Response);
//                                    }
//                                    catch(JSONException e) {
//
//                                    }
//                                    //System.out.println(Response);
//                                    System.out.println(response);
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    //Response = error.getMessage();
//                                    Response = error.toString();
//                                    System.out.println(error);
//
//                                }
//                            }
//                    ) {
//                        @Override
//                        public Map<String, String> getHeaders() throws AuthFailureError {
//                            HashMap<String, String> headers = new HashMap<String, String>();
//                            //                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                            //                headers.put("Content-Type", "application/json");
//                            return headers;
//                        }
//
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String, String> params = new HashMap<String, String>();
//                            params.put("PrimaryMonthlyIncome", yourvaluehere1);
//                            params.put("SecondaryMonthlyIncome", yourvaluehere2);
//                            return params;
//                        }
//                    };
//
//                    // Adding request to request queue
//                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
//                }
//            }
//
//
//
//
//            }
//        });
//    }
//}
