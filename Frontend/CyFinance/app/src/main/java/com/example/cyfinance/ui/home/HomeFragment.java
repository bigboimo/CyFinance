package com.example.cyfinance.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import com.example.cyfinance.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.cyfinance.LoginActivity;
import com.example.cyfinance.MainActivity;
import com.example.cyfinance.VolleySingleton;
import com.example.cyfinance.ui.Admin.AdminFragment;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.JsonRequest;
import com.example.cyfinance.util.SessionManager;

import android.view.MenuItem;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationBarView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends AppCompatActivity {

    private Button testButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard);

        NavigationBarView navView = findViewById(R.id.nav_view);

        navView.setSelectedItemId(R.id.navigation_dashboard);


        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_admin:
                        startActivity(new Intent(getApplicationContext(), AdminFragment.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


//        if (getActivity() != null) {
//            context = getActivity().getApplicationContext();
//        } else {
//            System.out.println("No valid activity");
//            Intent intent = new Intent(getContext(), LoginActivity.class);
//            startActivity(intent);
//        }
//
//        session = new SessionManager(context);

//        System.out.println("User id " + session.getUserDetails());

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

//        // Example button for sending a request with session headers
//        testButton = binding.testBtn;
//
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("Test clicked");
//                JsonRequest request = new JsonRequest(
//                        Request.Method.GET,
//                        Constants.URL + "/users", new JSONObject(),
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                System.out.println("[Testing] HTTP Response: " + response);
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                System.out.println(error);
//
//                            }
//                        }
//                ) {
//
//                    /**
//                     * Passing some request headers
//                     */
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        HashMap<String, String> headers = new HashMap<String, String>();
//                        headers.put("Cookie", "user-id=" + session.getUserDetails().get("id"));
//                        return headers;
//                    }
//                };
//                // Adding request to request queue
//                VolleySingleton.getInstance(context).addToRequestQueue(request);
//            }
//        });

//        return root;
    }
}
