package com.example.cyfinance.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cyfinance.MainActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.java_websocket.handshake.ServerHandshake;

import com.example.cyfinance.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment implements WebSocketListener{

    private FragmentNotificationsBinding binding;

    private TextView notification;
    private String BASE_URL = "";

    private String ERROR = "";

    private String message = "";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notification = binding.textNotifications;

        //final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(),  notification::setText);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketMessage(String message) {
        getActivity().runOnUiThread(() -> {
           String alert = notification.getText().toString();
           notification.setText("\n" + message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onWebSocketError(Exception ex) {
        getActivity().runOnUiThread(() ->{

        });
    }
}