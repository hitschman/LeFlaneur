package ch.cf.leflaneur.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationService.class.getSimpleName();

    // Binder given to clients
    private final IBinder binder = new LocalBinder();
    // Registered callbacks
    private ServiceCallbacks serviceCallbacks;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        this.stopSelf();
    }

    @Override
    public void onConnected(Bundle bundle) {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(0);
        this.mLocationRequest.setFastestInterval(0);
        this.mLocationRequest.setMaxWaitTime(0);
        this.mLocationRequest.setSmallestDisplacement(0);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: "+ location.toString());
        this.serviceCallbacks.onServiceLocationChanged(location);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service is destroyed");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        this.serviceCallbacks = callbacks;
    }

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public LocationService getService() {
            // Return this instance of MyService so clients can call public methods
            return LocationService.this;
        }
    }
}

