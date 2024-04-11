package com.example.cyfinance.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.cyfinance.R;
import com.example.cyfinance.VolleySingleton;

public class imagereq extends AppCompatActivity {

    private Button btnImageReq;
    private ImageView imageView;

    // URL to the image to be loaded via HTTP request
    public static final String URL_IMAGE = "http://10.0.2.2:8080/images/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the layout defined in chat.xml
        setContentView(R.layout.chat);

        // Initializing the button from the layout file using its ID
        btnImageReq = (Button) findViewById(R.id.btnImageReq);
        // Uncomment the following line and ensure the layout file has an ImageView with id=imgView to use it
        //imageView = (ImageView) findViewById(R.id.imgView);

        // Setting an onClickListener for the button to trigger the image loading process
        btnImageReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeImageRequest();
                // Showing a toast message when the button is clicked
                Toast.makeText(imagereq.this, "Loading image...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method creates an image request from the URL_IMAGE and sends it to the Volley queue.
     */
    private void makeImageRequest() {
        ImageRequest imageRequest = new ImageRequest(
                URL_IMAGE,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView, assuming imageView is initialized
                        imageView.setImageBitmap(response);
                        // Optionally, display a toast message upon successful image loading
                        Toast.makeText(imagereq.this, "Image Loaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                0, // Width of the image to scale. 0 means to take the width as it comes.
                0, // Height of the image to scale. 0 means to take the height as it comes.
                ImageView.ScaleType.FIT_XY, // ScaleType to use for formatting the image
                Bitmap.Config.RGB_565, // Bitmap config to decode the image
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log and display error on Volley error
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(imagereq.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Adding the request to the Volley request queue managed by VolleySingleton
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }
}
