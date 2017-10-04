package com.example.hivian.myweather.http;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.hivian.myweather.R;
import com.example.hivian.myweather.views.MainActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.Locale;

import org.json.JSONObject;

/**
 * Created by hivian on 10/2/17.
 */

public class HttpRequest extends AsyncTask<String, String, String> {

    private static final String API_KEY = "5392c33bc0a686e68bc840ec3375c815";
    private static final String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&mode=json&lang=%s";
    private static final String CURRENT_WEATHER_LOCATION_URL = "http://api.openweathermap.org/data/2.5/weather?&lat=%s&lon=%s&units=metric&mode=json&lang=%s&APPID=%s";

    private Location location;
    private MainActivity mainActivity;
    String errorMessage;

    public HttpRequest(MainActivity mainActivity, Location location) {
        this.mainActivity = mainActivity;
        this.location = location;
    }


    @Override
    protected String doInBackground(String... strings) {
        String data = null;
        BufferedReader bufferedReader = null;

        Log.d("format",  String.format(CURRENT_WEATHER_LOCATION_URL,
                String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                Locale.getDefault().getLanguage(), API_KEY));
        try {
            URL url = new URL(String.format(CURRENT_WEATHER_LOCATION_URL,
                    String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                    Locale.getDefault().getLanguage(), API_KEY));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expecting HTTP 200
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                errorMessage = "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_LONG).show();
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                builder.append(tmp);
            }

            data = builder.toString();

            //JSONObject obj = new JSONObject(data);
            //Log.d("JSON", obj.toString());

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            errorMessage = "Url connection: no data found";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {

                }
            }
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            mainActivity.setData(s);
        } else {
            Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

}
