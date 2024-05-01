package com.example.cyfinance;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
<<<<<<< Updated upstream
=======
import com.android.volley.toolbox.JsonObjectRequest;
>>>>>>> Stashed changes
import com.android.volley.toolbox.Volley;
import com.example.cyfinance.util.MultipartRequest;

public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

<<<<<<< Updated upstream
    public <T> void addToRequestQueue(Request<T> req) {
=======
    public <T> void addToRequestQueue(JsonObjectRequest req) {
>>>>>>> Stashed changes
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
<<<<<<< Updated upstream
=======

    public void addToRequestQueue(MultipartRequest uploadRequest) {
        getRequestQueue().add(uploadRequest);
    }
>>>>>>> Stashed changes
}