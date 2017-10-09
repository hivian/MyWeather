package com.example.hivian.myweather.views.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hivian.myweather.R;
import com.example.hivian.myweather.services.LocationService;
import com.example.hivian.myweather.http.HttpRequest;
import com.example.hivian.myweather.views.fragments.CurrentWeatherFragment;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_WEATHER_LOCATION_URL =
            "http://api.openweathermap.org/data/2.5/weather?&lat=%s&lon=%s&units=metric&mode=json&lang=%s&APPID=%s";
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new CurrentWeatherFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyStoragePermissions(this);
        } else {
            startService(new Intent(this, LocationService.class));
        }
    }

    public void setData(JSONObject json) {
        updateCurrentWeather(json);
    }

    public void updateCurrentWeather(JSONObject json) {
        CurrentWeatherFragment wf = (CurrentWeatherFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.updateCurrentWeather(json);
    }


    public void verifyStoragePermissions(Activity activity) {
        int checkCoarseLocationPermission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int checkFineLocationPermission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkCoarseLocationPermission + checkFineLocationPermission
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            startService(new Intent(this, LocationService.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this, LocationService.class));
                } else {
                    //finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                Location location = (Location) b.get(LocationService.EXTRA_LOCATION);
                Log.d("Lat = ", String.valueOf(location.getLatitude()));
                Log.d("Lon = ", String.valueOf(location.getLongitude()));

                new HttpRequest(MainActivity.this, location).execute(CURRENT_WEATHER_LOCATION_URL);
            }
        };
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(LocationService.TAG));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, LocationService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
