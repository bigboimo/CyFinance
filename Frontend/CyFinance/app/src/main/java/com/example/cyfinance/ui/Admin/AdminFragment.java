package com.example.cyfinance.ui.Admin;

import android.os.Bundle;
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

public class AdminFragment extends Fragment implements WebSocketListener {

    private FragmentAdminBinding binding;

    private String BASE_URL = "ws://10.0.2.2:8080/chat/";

    private String ERROR = "";

    private String message = "";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AdminViewModel adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        binding = FragmentAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button sendAlert = binding.alertButton;

        final EditText notification = binding.textNotifications;

        sendAlert.setOnClickListener(view -> {
            String serverURL = BASE_URL;


            adminViewModel.onButtonClick();
            WebSocketManager.getInstance().connectWebSocket(serverURL);
            WebSocketManager.getInstance().setWebSocketListener(this);

        });


        adminViewModel.getText().observe(getViewLifecycleOwner(), notification::setText);


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
//        getActivity().runOnUiThread(() -> {
//
//        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onWebSocketError(Exception ex) {
    }
}