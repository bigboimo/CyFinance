package com.example.cyfinance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cyfinance.MainActivity;
import com.example.cyfinance.WebSocketManager;

import org.java_websocket.handshake.ServerHandshake;

import java.text.BreakIterator;

public class ChatActivity extends AppCompatActivity implements WebSocketListener{
    String url= "ws://10.10.2.2:8080/chat/1/Hussein";
    private Button sendBtn, backMainBtn;
    private EditText msgEtx;
    private TextView msgTv;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        /* initialize UI elements */
        sendBtn = (Button) findViewById(R.id.sendBtn);
        msgEtx = (EditText) findViewById(R.id.msgEdt);
        msgTv = (TextView) findViewById(R.id.tx1);
        msg = (TextView) findViewById(R.id.txt);

        /* connect this activity to the websocket instance */
        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {

            try {
                // send message
                WebSocketManager.getInstance().sendMessage(msgEtx.getText().toString());
                //after getting the message typed in string  form:
                //1. you need to send it to the backend to save it
                //2. you need to make a request to get the saved string from the backend
                //3. once you call the saved string, you need to set it in the text view similarly to:
                msg.setText(msgEtx.getText().toString());
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());

            }
        });

        /* back button listener */

    }


    @Override
    public void onWebSocketMessage(String message) {
        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "\n"+message);
        });
    }


    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---connection closed by " + closedBy + "reason: " + reason);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}

