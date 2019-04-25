package com.example.mapactivity.controller;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class AppController extends Application {

    private static final int MY_SOCKET_TIMEOUT_MS = 0;
    private DefaultRetryPolicy defaultRetryPolicy = new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private File audio_file;
    private boolean available;

    public static synchronized AppController getInstance() {
        if (mInstance == null) {
            mInstance = new AppController();
        }
        return mInstance;
    }

    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setRetryPolicy(defaultRetryPolicy);
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(defaultRetryPolicy);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}
