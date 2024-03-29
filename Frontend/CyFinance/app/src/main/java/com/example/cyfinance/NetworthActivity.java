package com.example.cyfinance;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cyfinance.util.Constants;

public class NetworthActivity extends AppCompatActivity {


    private EditText assetEditText;

    private EditText liabiltiesEditText;

    private Button nextButton;

    private String assetNum;

    private String liabilityNum;

    private String url = Constants.URL + "/networth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networth);

        assetEditText = findViewById(R.id.asset_edt_txt);
        liabiltiesEditText = findViewById(R.id.liabilities_edt_txt);
        nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                assetNum = assetEditText.getText().toString();
                liabilityNum = liabiltiesEditText.getText().toString();

            }
        });
    }
}
