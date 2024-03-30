package com.example.cyfinance.ui.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.java_websocket.handshake.ServerHandshake;
import org.w3c.dom.Text;

import com.example.cyfinance.MainActivity;
import com.example.cyfinance.databinding.FragmentAdminBinding;

import java.util.Objects;

public class AdminFragment extends Fragment implements WebSocketListener {

    private static final Object CHANNEL_ID = 1;
    private FragmentAdminBinding binding;
    private String BASE_URL = "ws://10.0.2.2:8080/chat/Admin";
    private TextView adminMessage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AdminViewModel adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        String serverURL = BASE_URL;
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adminMessage = binding.textAlert;

        Button sendAlert = binding.alertButton;

        final EditText notification = binding.textNotifications;

        WebSocketManager.getInstance().connectWebSocket(serverURL);
        WebSocketManager.getInstance().setWebSocketListener(this);

        sendAlert.setOnClickListener(view -> {

            try {
                WebSocketManager.getInstance().sendMessage(notification.getText().toString());
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());
            }


        });


        adminViewModel.getText().observe(getViewLifecycleOwner(), notification::setText);
        //adminViewModel.getText().observe(getViewLifecycleOwner(), adminMessage::setText);


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onWebSocketMessage(String message) {
        getActivity().runOnUiThread(() -> {
            String s = adminMessage.getText().toString();
            adminMessage.setText(message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        getActivity().runOnUiThread(() ->{
            String s = adminMessage.getText().toString();
            adminMessage.setText(s + " " + closedBy + reason);
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {
    }
}