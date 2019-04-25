package com.example.mapactivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.example.mapactivity.databinding.ActivityMapsBinding;
import com.example.mapactivity.helpers.Geocode;
import com.example.mapactivity.model.Address;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraMoveListener,
        OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener,
        View.OnClickListener, TextView.OnEditorActionListener, GoogleMap.OnMapClickListener,
        EasyPermissions.PermissionCallbacks, LocationListener, ResultCallback<LocationSettingsResult>, GoogleMap.CancelableCallback {

    private GoogleMap mMap;

    public static final int REQUEST_GPS_PERMISSION = 101;
    double lat = 31.1048, lng = 77.1734;
    boolean isEditable = true;
    Address picked_address;
    GoogleApiClient googleApiClient;
    private boolean canSetCurrentLocation = true;
    Location mLastLocation;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    ActivityMapsBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mBinding.btnConfirmAddress.setVisibility(View.VISIBLE);
        canSetCurrentLocation = picked_address == null;
        initMap();


    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.edt_search) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mBinding.progressBar.setVisibility(View.VISIBLE);
                final String address = v.getText().toString();
                setPosition(address);
            }
        }
        return false;

    }


    public void setPosition(String address) {
        Geocode.getAddressFromGoogle(address.replaceAll(" ", "+"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Address address = setAddress(response);
                mBinding.progressBar.setVisibility(View.GONE);
                canSetCurrentLocation = false;
                setMarker(address);

            }
        });
    }

    private void setMarker(Address address) {
        if (address != null && address.getLat() != 0 && address.getLng() != 0) {
            animateCamera(new LatLng(address.getLat(), address.getLng()));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10f), this);
        }
    }


    private void initMap() {
        mBinding.edtSearch.setOnEditorActionListener(this);
        mBinding.btnConfirmAddress.setOnClickListener(this);
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = googleApiAvailability.getErrorDialog(MapsActivity.this, status, requestCode);
            dialog.show();
        } else {
            MapsInitializer.initialize(this);
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        }
        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("onConnectionSuspended", i + "");
        DialogUtil.showOkCancelDialog(this, R.string.unable_to_retrieve, new DialogUtil.CallBack() {
            @Override
            public void onDismiss(boolean isPressedOK) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed", connectionResult.toString());
        onConnectionSuspended(-1);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (isEditable) {
            mMap.setOnMapClickListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraMoveStartedListener(this);
        }
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            googleApiClient.connect();
        }
        setMarker(picked_address);
        mBinding.icCurrentLocation.performClick();
    }


    @Override
    public void onCameraMoveStarted(int reason) {
        Log.e("onCameraMoveStarted", "onCameraMoveStarted");
        mBinding.txtSearchAddress.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.ivPinDot.getLayoutParams();
        params.setMargins(0, 22, 0, 0);
        mBinding.ivPinDot.setLayoutParams(params);
    }

    @Override
    public void onCameraIdle() {
        Log.e("onCameraIdle", "onCameraIdle");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.ivPinDot.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        mBinding.ivPinDot.setLayoutParams(params);
        getAddressFromGeoCode(lat, lng);
    }

    private void getAddressFromGeoCode(double lat, double lng) {
        Geocode.getAddressFromGoogle(lat, lng, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setAddress(response);
            }
        });
    }

    @Override
    public void onCameraMove() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        lat = cameraPosition.target.latitude;
        lng = cameraPosition.target.longitude;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm_address:
                if (picked_address != null && picked_address.getFormatted_address() != null) {
                    String location = picked_address.getFormatted_address();
                    DialogUtil.showOkDialogBox(v.getContext(),location,null);
                }
                break;
            case R.id.ic_current_location:
                canSetCurrentLocation = true;
                requestLocationPermission();
                break;
        }
    }


    @AfterPermissionGranted(REQUEST_GPS_PERMISSION)
    private boolean requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getCurrentLocation(canSetCurrentLocation);
            return true;
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_location), REQUEST_GPS_PERMISSION, perms);
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("EasyPermission", "onPermissionsGranted:" + requestCode + ":" + perms.size());
        getCurrentLocation(canSetCurrentLocation);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("EasyPermission", "onPermissionsDenied:" + requestCode + ":" + perms.size());
    }


    private void getCurrentLocation(boolean canSetCurrentLocation) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            this.mLastLocation = mLastLocation;
            if (canSetCurrentLocation) {
                animateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        } else if (this.mLastLocation != null && canSetCurrentLocation) {
            animateCamera(new LatLng(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude()));
        } else if (googleApiClient.isConnected()) {
            LocationRequest mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10 * 1000).setFastestInterval(100);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            if (canSetCurrentLocation) {
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                builder.setAlwaysShow(true);
                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mLastLocation = location;
            if (canSetCurrentLocation) {
                animateCamera(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        }
    }

    private void animateCamera(LatLng latLng) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f), this);
    }


    private Address setAddress(JSONObject jsonObject) {
        HashMap<String, String> addressMap = Geocode.getFormattedAddress(jsonObject);
        String full_address = addressMap.get(Geocode.formatted_address);

        if (full_address != null && !full_address.isEmpty()) {
            mBinding.txtSearchAddress.setText(full_address);
            mBinding.txtSearchAddress.setVisibility(View.VISIBLE);
        }

        picked_address = new Address();
        picked_address.setAddressMap(addressMap);
        return picked_address;
    }

    @Override
    public void onStart() {
        if (googleApiClient != null)
            googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mBinding.txtSearchAddress.getVisibility() == View.VISIBLE) {
            mBinding.txtSearchAddress.setVisibility(View.GONE);
        } else {
            mBinding.txtSearchAddress.setText("");
            mBinding.txtSearchAddress.setVisibility(View.VISIBLE);
            mBinding.txtSearchAddress.setText(R.string.generating_address);
            getAddressFromGeoCode(lat, lng);
        }

    }


    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                getCurrentLocation(canSetCurrentLocation);
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(this, REQUEST_GPS_PERMISSION);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Request Code" + requestCode, "Result Code " + resultCode + " " + data.getData());
        if (requestCode == REQUEST_GPS_PERMISSION && resultCode == -1) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(this, null, getString(R.string.please_wait), true, true);
        }

    }

    @Override
    public void onFinish() {
        if (isEditable) {
            isEditable = false;
            requestLocationPermission();
        }
    }

    @Override
    public void onCancel() {

    }

}
