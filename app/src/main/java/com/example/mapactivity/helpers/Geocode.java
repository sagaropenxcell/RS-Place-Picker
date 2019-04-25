package com.example.mapactivity.helpers;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mapactivity.controller.AppController;
import com.example.mapactivity.controller.RequestJson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Geocode {

    public static final String premise = "premise";
    public static final String street_number = "street_number";
    public static final String route = "route";
    public static final String locality = "locality";
    public static final String sublocality_level_3 = "sublocality_level_3";
    public static final String sublocality_level_2 = "sublocality_level_2";
    public static final String sublocality_level_1 = "sublocality_level_1";
    public static final String administrative_area_level_3 = "administrative_area_level_3";
    public static final String administrative_area_level_2 = "administrative_area_level_2";
    public static final String administrative_area_level_1 = "administrative_area_level_1";
    public static final String country = "country";
    public static final String postal_code = "postal_code";
    public static final String lat = "lat";
    public static final String lng = "lng";
    public static final String formatted_address = "formatted_address";


    public static String getAddressFromGeoCoder(double latitude, double longitude) {
        List<Address> addresses;
        try {

            Geocoder geocoder = new Geocoder(AppController.getInstance().getApplicationContext(), Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String line1 = address.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();


                StringBuilder sb = new StringBuilder();
                sb.append(getValidString(line1));
                if (sb.length() > 0) {
                    sb.append(" , ");
                }
                sb.append(getValidString(city));

                return sb.toString();
            }


        } catch (Exception e) {
            Log.d("Exception", "getAddressFromGeoCoder() returned: " + e.toString());
            return "";
        }
        return "";
    }

    private static String getValidString(String line) {
        return line == null || line.length() <= 0 ? "" : line;
    }

    public static void getAddressFromGoogle(String address, Response.Listener<JSONObject> jsonObjectListener) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyDP7GmYr0Om-EuTqFUkqhMRQmxlbCG6-3I";
        query(url, jsonObjectListener);
    }

    public static void getAddressFromGoogle(double latitude, double longitude, Response.Listener<JSONObject> jsonObjectListener) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyDP7GmYr0Om-EuTqFUkqhMRQmxlbCG6-3I";;
        query(url, jsonObjectListener);
    }



    public static HashMap<String, String> getFormattedAddress(JSONObject response) {
        HashMap<String, String> address = new HashMap<>();
        if (response != null) {
            try {
                if (response.getString("status").equalsIgnoreCase("OK")) {
                    JSONArray jsonArray = response.getJSONArray("results");

//                    for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String full_address = jsonObject.getString(formatted_address);
                    address.put(formatted_address, full_address);

                    JSONObject jsonObjectLocation = jsonObject.getJSONObject("geometry").getJSONObject("location");
                    address.put(lat, String.valueOf(jsonObjectLocation.getString(lat)));
                    address.put(lng, String.valueOf(jsonObjectLocation.getString(lng)));


                    JSONArray jsonArrayAddress = jsonObject.getJSONArray("address_components");
                    for (int j = 0; j < jsonArrayAddress.length(); j++) {
                        JSONObject jsonObjectAddress = jsonArrayAddress.getJSONObject(j);

                        String type = jsonObjectAddress.getString("types");
                        String name = jsonObjectAddress.getString("long_name");

                        if (type.contains(premise)) {
                            address.put(premise, name);
                        } else if (type.contains(street_number)) {
                            address.put(street_number, name);
                        } else if (type.contains(route)) {
                            address.put(route, name);
                        } else if (type.contains(sublocality_level_3)) {
                            address.put(sublocality_level_3, name);
                        } else if (type.contains(sublocality_level_2)) {
                            address.put(sublocality_level_2, name);
                        } else if (type.contains(sublocality_level_1)) {
                            address.put(sublocality_level_1, name);
                        } else if (type.contains(administrative_area_level_3)) {
                            address.put(administrative_area_level_3, name);
                        } else if (type.contains(administrative_area_level_2)) {
                            address.put(administrative_area_level_2, name);
                        } else if (type.contains(administrative_area_level_1)) {
                            address.put(administrative_area_level_1, name);
                        } else if (type.contains(locality)) {
                            address.put(locality, name);
                        } else if (type.contains(country)) {
                            address.put(country, name);
                        } else if (type.contains(postal_code)) {
                            address.put(postal_code, name);
                        }
                    }
//                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return address;
    }


    private static void query(String url, final Response.Listener<JSONObject> listener) {
        RequestJson requestJson = new RequestJson(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                listener.onResponse(null);
            }
        });

        AppController.getInstance().addToRequestQueue(requestJson);
    }
}
