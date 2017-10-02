package com.example.hivian.myweather.http;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.hivian.myweather.R;

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

public class HttpRequest {

    private static final String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&mode=json&lang=%s";
    private static final String CURRENT_WEATHER_LOCATION_URL = "http://api.openweathermap.org/data/2.5/weather?&lat=%s&lon=%s&units=metric&mode=json&lang=%s";

    private static String load(Context context, String weatherUrl, Location location) {
        String data = null;
        BufferedReader bufferedReader = null;


        try {
            URL url = new URL(String.format(weatherUrl,
                    location.getLatitude() + location.getLongitude(),
                    Locale.getDefault()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            Log.d("LOCALE", Locale.getDefault().toString());

            // expecting HTTP 200
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                String message = "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
                //Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                Log.d("API", "FAIL");
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONObject obj = new JSONObject(bufferedReader.toString());

            Log.d("JSON", obj.toString());


        } catch (Exception e) {

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

    /*public static String loadCurrentWeather(Context context, String city) {
        return load(context, CURRENT_WEATHER_URL, city);
    }*/

    public static String loadCurrentWeatherLocation(Context context, Location location) {
        return load(context, CURRENT_WEATHER_LOCATION_URL, location);
    }

}
