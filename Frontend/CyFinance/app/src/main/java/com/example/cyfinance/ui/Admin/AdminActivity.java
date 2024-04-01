package com.example.cyfinance.ui.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.handshake.ServerHandshake;

import com.example.cyfinance.HomeActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.ui.Earnings.EarningsDActivity;
import com.example.cyfinance.ui.Expenses.ExpensesDActivity;
import com.google.android.material.navigation.NavigationBarView;

public class AdminActivity extends AppCompatActivity implements WebSocketListener {
    private String BASE_URL = "ws://10.0.2.2:8080/chat/Admin";

    TextView adminMessage;

    public void onCreate(Bundle savedInstanceState) {
        String serverURL = BASE_URL;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        NavigationBarView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_admin);

        adminMessage = findViewById(R.id.text_alert);

        EditText messageText = findViewById(R.id.text_notifications);

        Button sendAlert = findViewById(R.id.alert_button);

        WebSocketManager.getInstance().connectWebSocket(serverURL);
        WebSocketManager.getInstance().setWebSocketListener(this);

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