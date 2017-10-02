package com.example.hivian.myweather.gps;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by hivian on 10/2/17.
 */

public class LocationService extends Service implements LocationListener {

    private LocationManager locationManager;

    //public Location location;

    private static final long LOCATION_REFRESH_TIME = 10000;
    private static final long LOCATION_REFRESH_DISTANCE = 0;

    public static final String
            ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationService",
            EXTRA_LATITUDE = "latitude",
            EXTRA_LONGITUDE = "longitude";

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Log.d("SERVICE", "START");

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled) {
                Log.d("GPS", "ENABLED");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE, this);
                sendBroadcastMessage(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            else if (isNetworkEnabled) {
                Log.d("NETWORK", "ENABLED");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE, this);
            }
            //sendBroadcastMessage(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

        } catch (SecurityException e) {
            Log.d("GPS", "NOPE");
        }

        return START_STICKY;
    }


    @Override
    public void onLocationChanged(Location location) {
        //this.location = location;
        Log.d("SERVICE", "BRODCAST SENT");
        this.sendBroadcastMessage(location);
    }

    @Override
    public void onStatusChanged(String s, int status, Bundle bundle) {
        String newStatus = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                newStatus = "OUT_OF_SERVICE";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                newStatus = "TEMPORARILY_UNAVAILABLE";
                break;
            case LocationProvider.AVAILABLE:
                newStatus = "AVAILABLE";
                break;
        }
        Log.d("STATUS", newStatus);
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        locationManager = null;
        super.onDestroy();
    }

    private void sendBroadcastMessage(Location location) {
        Log.d("SERVICE", "HERE");
        if (location != null) {
            Log.d("SERVICE", "BRODCAST SENT");
            Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
            intent.putExtra(EXTRA_LATITUDE, location.getLatitude());
            intent.putExtra(EXTRA_LONGITUDE, location.getLongitude());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
