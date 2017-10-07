package com.example.hivian.myweather.http;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioRouting;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hivian.myweather.utilities.NotificationHandler;
import com.example.hivian.myweather.views.activities.MainActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hivian on 10/2/17.
 */

public class HttpRequest extends AsyncTask<String, String, String> {

    private static final String API_KEY = "5392c33bc0a686e68bc840ec3375c815";
    private static final String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&mode=json&lang=%s";
    private static final String CURRENT_WEATHER_LOCATION_URL = "http://api.openweathermap.org/data/2.5/weather?&lat=%s&lon=%s&units=metric&mode=json&lang=%s&APPID=%s";

    private NotificationManager nm;
    private Location location;
    private Context context;
    private JSONObject jsonObject;
    private String errorMessage;

    public HttpRequest(Context context, Location location) {
        this.context = context;
        this.location = location;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progressBar.setVisibility(View.VISIBLE);
        //NotificationHandler.notify(context, android.R.drawable.ic_popup_sync, "", "");
    }

    @Override
    protected String doInBackground(String... strings) {
        String data = null;
        BufferedReader bufferedReader = null;

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
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                builder.append(tmp);
            }

            data = builder.toString();

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            errorMessage = "Url connection: no data found";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    Log.d("Error", e.getMessage());
                }
            }
        }
        return data;
    }

    @Override
    protected void onPostExecute(String postData) {
        if (postData != null) {
            try {
                jsonObject = new JSONObject(postData);
                if(jsonObject.getInt("cod") != 200) {
                    errorMessage = "Api request unsuccessful";
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }
                ((MainActivity) context).setData(jsonObject);
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

}
