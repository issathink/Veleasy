package com.veleasy.veleasy;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION=123;

    private boolean hashLocationPermission = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation;
    private Marker user_PosMarker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
            mGoogleApiClient.connect();

        // Check if user gave the permission for Location
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION);
        }else {
            hashLocationPermission = true;
           initMapAsync();
        }


    }
    public void initMapAsync(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void updatePos(Location l){
        LatLng pos;
        // Get last position or ridiculous position
        if(mLastLocation!=null)
            pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        else {
            pos = new LatLng(2, 2);
            Log.e("Erreur","No lastLocation");
        }

        //Change the marker for position
        if(user_PosMarker == null) {
            user_PosMarker = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title("Tu es la :)"));
        }
        else{
            user_PosMarker.setPosition( pos);
            user_PosMarker.setTitle("toz");
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMIISSION","Jai la permission");
                    hashLocationPermission = true;
                    initMapAsync();

                } else {
                    hashLocationPermission = false;
                    Log.d("PERMIISSION","Jai pas la permission");
                }
                return;

            }
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        // onConnect gets user last Location and update the GoogleMap
        Log.e("Lol","Je passe connecte");
        updatePos(mLastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Lol","Je passe suspend");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Lol","Je passe failed");

    }
}
