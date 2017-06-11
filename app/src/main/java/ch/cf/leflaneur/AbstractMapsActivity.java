package ch.cf.leflaneur;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import ch.cf.leflaneur.Services.LocationService;
import ch.cf.leflaneur.Services.ServiceCallbacks;
import ch.cf.leflaneur.enums.LocationUpdateInterval;
import ch.cf.leflaneur.model.PedestrianGuide;
import ch.cf.leflaneur.model.Navigation;
import ch.cf.leflaneur.output.Vibration;

public abstract class AbstractMapsActivity extends FragmentActivity implements
        ServiceCallbacks,
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        TextToSpeech.OnInitListener
{


    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int CAMERA_ZOOM = 16;


    private static final String TAG = MapsActivity.class.getSimpleName();

    private LocationService myService;

    protected GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    protected LatLng currentLatLng = new LatLng(0, 0);
    protected Location currentLocation = new Location("");
    private  boolean firstLocation = true;
    protected Navigation navigation = new Navigation();

    protected PedestrianGuide guide; //TODO Build own Guide

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
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
        this.mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);
        this.mMap.getUiSettings().setRotateGesturesEnabled(false);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                this.mMap.setMyLocationEnabled(true);
                this.mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
        else {
            buildGoogleApiClient();
            this.mMap.setMyLocationEnabled(true);
            this.mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    /**
     * Handles the callback when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged"+location.toString());

        this.currentLocation = location;
        this.currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (this.firstLocation){
            this.mMap.moveCamera(CameraUpdateFactory.newLatLng(this.currentLatLng));
            this.mMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_ZOOM));
        }
        this.firstLocation = false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "Abstract.onMapClick: "+latLng.toString());
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "Abstract.onMapLongClick: "+latLng.toString());
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (this.mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        this.mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        this.mLocationRequest = new LocationRequest();
        this.updateLocationUpdateInterval(LocationUpdateInterval.SLOW);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        this.guide = new PedestrianGuide(new Vibration((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)), this, new TextToSpeech(this, this));

    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void updateLocationUpdateInterval(LocationUpdateInterval interval){

        switch(interval){
            case FAST:
                this.mLocationRequest.setInterval(0);
                this.mLocationRequest.setFastestInterval(0);
                this.mLocationRequest.setMaxWaitTime(0);
                this.mLocationRequest.setSmallestDisplacement(0);
                this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                break;
            case SLOW:
                this.mLocationRequest.setInterval(10000);
                this.mLocationRequest.setFastestInterval(7500);
                this.mLocationRequest.setMaxWaitTime(15000);
                this.mLocationRequest.setSmallestDisplacement(20);
                this.mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                break;
            default:
                this.updateLocationUpdateInterval(LocationUpdateInterval.SLOW);
        }

    }

    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            myService.setCallbacks(AbstractMapsActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


    @Override
    public void onServiceLocationChanged(Location location) {
        this.onLocationChanged(location);
    }

    public void startBackgroundLocationUpdate(){
        try{
            bindService(new Intent(this, LocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            startService(new Intent(this, LocationService.class));
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getStackTrace().toString());
        }

    }

    public void stopBackgroundLocationUpdate(){
        try{
            if (this.myService != null) {
                stopService(new Intent(this, LocationService.class));
                unbindService(serviceConnection);
            }
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    public void drawMap(){
        this.mMap.clear();
    }

    @Override
    public void onInit(int status) {

        if (status != TextToSpeech.SUCCESS) {
            Log.e(TAG, "TTS: Initilization Failed!");
        }
    }

    public void goToCurrentLocation(View view){
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(this.currentLatLng));
        this.mMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_ZOOM));
    }
}