package com.example.cyfinance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cyfinance.EarningsActivity;
import com.example.cyfinance.NetworthActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.ui.Admin.AdminActivity;
import com.example.cyfinance.ui.Admin.WebSocketListener;
import com.example.cyfinance.ui.Admin.WebSocketManager;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.SessionManager;

import org.java_websocket.handshake.ServerHandshake;

public class SendAlert extends AppCompatActivity implements WebSocketListener {
    private String BASE_URL = Constants.WS + "/alerts/";
    SessionManager session;
    TextView adminMessage;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendalert);

        EditText messageText = findViewById(R.id.text_notifications);
        Button sendAlert = findViewById(R.id.alert_button);
        Button back = findViewById(R.id.back_button);

        adminMessage = findViewById(R.id.text_alert);
        session = new SessionManager(getApplicationContext());
        String serverURL = BASE_URL + session.getUserDetails().get("id");

        WebSocketManager.getInstance().connectWebSocket(serverURL);
        WebSocketManager.getInstance().setWebSocketListener(this);

        back.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(SendAlert.this, AdminActivity.class);
                startActivity(intent);
            } catch(Exception e) {

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
