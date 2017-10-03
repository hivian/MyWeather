package com.example.hivian.myweather.gps;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

/**
 * Created by hivian on 10/2/17.
 */

public class LocationService extends Service {

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    public static final String TAG = LocationService.class.getSimpleName();
    private Handler serviceHandler;

    private Location mLocation;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 10;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public static final String EXTRA_LOCATION = "location";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                sendBroadcast(locationResult.getLastLocation());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                if (!locationAvailability.isLocationAvailable()) {
                    Log.d("TOTO", "NO");
                    serviceHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Localisation unavailable",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                super.onLocationAvailability(locationAvailability);
            }
        };

        Log.d("START", "START");
        createLocationRequest();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());

        requestLocationUpdate();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //getLastLocation();

        return START_STICKY;
    }

    public void requestLocationUpdate() {
        Log.d(TAG, "Requesting location updates");
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback, serviceHandler.getLooper());
        } catch (SecurityException e) {
            Log.d(TAG, "Lost location permission. Could not request updates. " + e);
        }
    }

    private void sendBroadcast(Location location) {
        mLocation = location;

        // Notify anyone listening for broadcasts about the new location.
        if (location != null) {
            Log.d("SERVICE", "BRODCAST SENT");
            Intent intent = new Intent(TAG);
            intent.putExtra(EXTRA_LOCATION, mLocation);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getLastLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.d("getLastLocation", "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e("getLastLocation", "Lost location permission." + unlikely);
        }
    }

    @Override
    public void onDestroy() {
        Log.d("SERVICE", "DESTROYED");
        serviceHandler.removeCallbacksAndMessages(locationCallback);
        super.onDestroy();
    }

}
