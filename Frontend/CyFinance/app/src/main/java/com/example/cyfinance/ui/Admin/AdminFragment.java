package com.example.cyfinance.ui.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.handshake.ServerHandshake;
import org.w3c.dom.Text;

import com.example.cyfinance.MainActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

public class AdminFragment extends AppCompatActivity implements WebSocketListener {
    private String BASE_URL = "ws://10.0.2.2:8080/chat/Admin";

    TextView adminMessage;

    public void onCreate(Bundle savedInstanceState) {
        String serverURL = BASE_URL;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_admin);
        NavigationBarView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_admin);

        adminMessage = findViewById(R.id.text_alert);

        EditText messageText = findViewById(R.id.text_notifications);

        Button sendAlert = findViewById(R.id.alert_button);

        WebSocketManager.getInstance().connectWebSocket(serverURL);
        WebSocketManager.getInstance().setWebSocketListener(this);

        sendAlert.setOnClickListener(view -> {

            try {
                WebSocketManager.getInstance().sendMessage(messageText.getText().toString());
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());
            }


        });

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), HomeFragment.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_admin:
                        return true;
                }
                return false;
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