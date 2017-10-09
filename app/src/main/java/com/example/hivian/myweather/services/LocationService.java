package com.example.hivian.myweather.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.hivian.myweather.R;
import com.example.hivian.myweather.utilities.NotificationHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 30;
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
                NotificationHandler.cancelNotification(LocationService.this);
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                if (!locationAvailability.isLocationAvailable()) {
                    serviceHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("DEBUG", "Localisation unavailable");
                        }
                    });
                }
                super.onLocationAvailability(locationAvailability);
            }
        };

        createLocationRequest();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());

        //requestLocationUpdate();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationHandler.notify(this, android.R.drawable.ic_popup_sync,
                getString(R.string.app_name), "Location synchronization ...");
        getLastLocation();

        return START_STICKY;
    }

    public void requestLocationUpdate() {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback, serviceHandler.getLooper());
        } catch (SecurityException e) {
            Log.d(TAG, "Lost location permission. Could not request updates. " + e);
        }
    }

    private void sendBroadcast(Location location) {
        mLocation = location;

        if (location != null) {
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
                                sendBroadcast(mLocation);
                            } else {
                                Toast.makeText(LocationService.this, "Localisation unavailable",
                                        Toast.LENGTH_SHORT).show();
                                Log.d("getLastLocation", "Failed to get location.");
                            }
                            NotificationHandler.cancelNotification(LocationService.this);
                        }
                    });
        } catch (SecurityException e) {
            Log.d("getLastLocation", "Lost location permission." + e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceHandler.removeCallbacksAndMessages(locationCallback);
    }

}
