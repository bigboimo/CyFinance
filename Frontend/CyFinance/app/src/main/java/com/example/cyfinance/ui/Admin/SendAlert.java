package com.example.cyfinance.ui.Admin;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.cyfinance.HomeActivity;
import com.example.cyfinance.R;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.SessionManager;

import org.java_websocket.handshake.ServerHandshake;

public class SendAlert extends AppCompatActivity implements WebSocketListener {
    private String BASE_URL = Constants.WS + "/alerts/";
    SessionManager session;
    TextView adminMessage;
    String CHANNEL_ID = "CHANNEL_NOTIFICATION_ID";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendalert);

        EditText messageText = findViewById(R.id.text_notifications);
        Button sendAlert = findViewById(R.id.alert_button);
        Button back = findViewById(R.id.back_button);

        adminMessage = findViewById(R.id.text_alert);

        //Session
        session = new SessionManager(getApplicationContext());
        String serverURL = BASE_URL + session.getUserDetails().get("id");

        //Websocket
        WebSocketManager.getInstance().connectWebSocket(serverURL);
        WebSocketManager.getInstance().setWebSocketListener(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if(ContextCompat.checkSelfPermission(SendAlert.this,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SendAlert.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }


        back.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(SendAlert.this, AdminActivity.class);
                startActivity(intent);
            } catch(Exception e) {}
        });

        sendAlert.setOnClickListener(view -> {

            try {
                WebSocketManager.getInstance().sendMessage(messageText.getText().toString());
                makeNotification();
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

    private void makeNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentTitle("Admin Message")
                .setContentText(adminMessage.getText().toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(CHANNEL_ID);
            if(notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(CHANNEL_ID, "Something", importance);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0, builder.build());

    }
}
