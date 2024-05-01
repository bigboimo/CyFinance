package com.example.cyfinance;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.cyfinance.util.Constants;
import com.example.cyfinance.util.MultipartRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    private Button selectBtn, uploadBtn, deleteBtn;
    private ImageView profileImageView;
    private Uri selectedUri;

    // Base URL for the backend server
    private static final String userEmail = "user@email.com";  // User email for API endpoint


    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);
        profileImageView = findViewById(R.id.imageViewProfile);
        selectBtn = findViewById(R.id.buttonSelectProfile);
        uploadBtn = findViewById(R.id.buttonUploadImage);
        deleteBtn = findViewById(R.id.buttonDeleteImage);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedUri = uri;
                        profileImageView.setImageURI(uri);
                    }
                });

        selectBtn.setOnClickListener(v -> mGetContent.launch("image/*"));
        uploadBtn.setOnClickListener(v -> {
            try {
                uploadImage();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error preparing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        deleteBtn.setOnClickListener(v -> deleteImage());
    }

    private void uploadImage() throws IOException {
        byte[] imageData = Utility.convertImageUriToBytes(getContentResolver(), selectedUri);

        MultipartRequest uploadRequest = new MultipartRequest(
                        Request.Method.PUT,
                        Constants.URL + "/users/" + userEmail + "/profilepicture",
                imageData,  // No additional headers are required
                response -> Toast.makeText(getApplicationContext(), "Upload successful!", Toast.LENGTH_LONG).show(),
                null
                );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(uploadRequest);
    }

    public JsonObjectRequest deleteImage() {
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE,
                Constants.URL + "/users/" + userEmail + "/profilepicture",
                response -> Toast.makeText(getApplicationContext(), "Image deleted successfully!", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteImage());
        return null;
    }

    // Nested static class for utility methods
    static class Utility {
        public static byte[] convertImageUriToBytes(ContentResolver contentResolver, Uri imageUri) throws IOException {
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            inputStream.close();  // Ensure resources are freed
            return byteBuffer.toByteArray();
        }
    }
}



