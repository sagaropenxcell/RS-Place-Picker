package com.example.mapactivity.controller;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sagar Limbani on 6/26/2015.
 */
public class RequestJson extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    public RequestJson(String url, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        Log.e("Request", url);

    }

    public RequestJson(String url, Map<String, String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        Log.e("Request", url + "/" + params.toString());
        this.listener = responseListener;
        this.params = params;
    }

    public RequestJson(int method, String url, Map<String, String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        if (params != null)
            Log.e("Request", url + "/" + params.toString());
        this.listener = responseListener;
        this.params = params;
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.e("Response", jsonString);
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<>();
        }
        return headers;
    }
}