package com.example.cyfinance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ChatConnect extends AppCompatActivity{
    String url= "ws://10.10.2.2:8080/chat/1/Hussein";
    private Button connectBtn, connectBtn2, backBtn, backBtn2;
    private EditText serverEtx, usernameEtx, serverEtx2, usernameEtx2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatconnect1);
        WebSocketManager.getInstance().init(this);
        /* initialize UI elements */
        connectBtn = (Button) findViewById(R.id.connectBtn);
        serverEtx = (EditText) findViewById(R.id.serverEdt);
        usernameEtx = (EditText) findViewById(R.id.unameEdt);


        /* connect button listener */
        connectBtn.setOnClickListener(view -> {
            String serverUrl = serverEtx.getText().toString() + usernameEtx.getText().toString();
            String url = "ws://10.10.2.2:8080/chat/1/Hussein";
            // Establish WebSocket connection and set listener
            WebSocketManager.getInstance().connectWebSocket(url);

            // got to chat activity
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });


        /* back button listener */

        /* back button listener */

    }
}

