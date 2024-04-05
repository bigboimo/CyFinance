package com.example.cyfinance.ui.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.example.cyfinance.HomeActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.VolleySingleton;
import com.example.cyfinance.ui.Earnings.EarningsDActivity;
import com.example.cyfinance.ui.Expenses.ExpensesDActivity;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.example.cyfinance.util.SessionManager;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;


/*
    Push Notifications for WS in DEMO 4
 */
public class AdminActivity extends AppCompatActivity implements WebSocketListener {
    private String BASE_URL = Constants.WS + "/alerts/";
    TextView adminMessage;
    SessionManager session;
    String Response;
    ConstraintLayout adminLayout;
    public void onCreate(Bundle savedInstanceState) {
        session = new SessionManager(getApplicationContext());
        String serverURL = BASE_URL + session.getUserDetails().get("id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        NavigationBarView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_admin);

        adminMessage = findViewById(R.id.text_alert);

        EditText messageText = findViewById(R.id.text_notifications);

        Button sendAlert = findViewById(R.id.alert_button);

        Button refresh = findViewById(R.id.button_refresh);

        WebSocketManager.getInstance().connectWebSocket(serverURL);
        WebSocketManager.getInstance().setWebSocketListener(this);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequest();
            }
        });
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_earnings:
                        startActivity(new Intent(getApplicationContext(), EarningsDActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_admin:
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_expenses:
                        startActivity(new Intent(getApplicationContext(), ExpensesDActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        sendAlert.setOnClickListener(view -> {

            try {
                WebSocketManager.getInstance().sendMessage(messageText.getText().toString());
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());
            }


        });
    }

    private void getRequest() {
        JSONObject postBody = null;

        JsonRequest request = new JsonRequest(
                Request.Method.GET,
                Constants.URL + "/users",
                postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("[Login] HTTP Response: " + response);
                            JSONArray data = response.getJSONArray("data");
                            JSONObject headers = response.getJSONObject("headers");


                            adminLayout = findViewById(R.id.admin_layout);
                            for(int i = 0; i < data.length(); i++) {
                                String user = data.getJSONObject(i).getString("email");
                                TextView newUser = new TextView(AdminActivity.this);

                                newUser.setLayoutParams(new ConstraintLayout.LayoutParams(
                                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                                        ConstraintLayout.LayoutParams.MATCH_PARENT
                                ));

                                newUser.setText(user);
                                adminLayout.addView(newUser);
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
                //params.put();
                //params.put();
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            String s = adminMessage.getText().toString();
            adminMessage.setText(message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = adminMessage.getText().toString();
            adminMessage.setText(s + " " + closedBy + reason);
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {
    }
}