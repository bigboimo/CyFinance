package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;
    private TextView extraText;
    // define message textview variable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        messageText.setText("Hello there!");
        extraText = findViewById(R.id.extra_txt);
        extraText.setText("General Kenobi!");

    }
}