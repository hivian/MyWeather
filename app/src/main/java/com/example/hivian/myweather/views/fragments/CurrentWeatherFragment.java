package com.example.hivian.myweather.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hivian.myweather.R;
import com.example.hivian.myweather.views.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class CurrentWeatherFragment extends Fragment {
    private Typeface weatherFont;
    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView descriptionField;
    private TextView humidityField;
    private TextView pressureField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;

    private JSONObject details;
    private JSONObject main;
    private DateFormat df;
    private String updatedOn;
    private String temperature;
    private SharedPreferences preferences;
    private static final String PREFS = "PREFS";
    private static final String PREFS_JSON = "PREFS_JSON";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current_weather, container, false);

        cityField = rootView.findViewById(R.id.city_field);
        updatedField = rootView.findViewById(R.id.updated_field);
        descriptionField = rootView.findViewById(R.id.description_field);
        humidityField = rootView.findViewById(R.id. humidity_field);
        pressureField = rootView.findViewById(R.id.pressure_field);
        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = rootView.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);

        loadPreferences();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather_icons.ttf");
        preferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private void renderWeather(JSONObject json) {
        try {
            details = json.getJSONArray("weather").getJSONObject(0);
            main = json.getJSONObject("main");
            df = DateFormat.getDateTimeInstance();
            updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            temperature = String.format("%.2f", main.getDouble("temp")) + " ℃";

            cityField.setText(json.getString("name").toUpperCase(Locale.getDefault()) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));
            updatedField.setText("Last update: " + updatedOn);
            descriptionField.setText(details.getString("description").toUpperCase(Locale.getDefault()));
            humidityField.setText("Humidity: " + main.getString("humidity") + "%");
            pressureField.setText("Pressure: " + main.getString("pressure") + " hPa");
            currentTemperatureField.setText(temperature);
            currentTemperatureField.setTextColor(getTemperatureColor(
                    main.getDouble("temp")));
            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch(Exception e){
            Log.d("updateCurrentWeather", "JSON field(s) missing");
        }
    }


    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime >= sunrise && currentTime < sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void loadPreferences() {
        try {
            if (preferences != null && preferences.contains(PREFS_JSON)) {
                JSONObject json = new JSONObject(preferences.getString(PREFS_JSON, null));
                renderWeather(json);
            } else {
                descriptionField.setText("Error");
            }
        } catch (JSONException e) {
            Log.d("loadPreferences", "Json FAIL");
        }
    }

    public void savePreferences(JSONObject json) {
        if (preferences != null) {
            preferences.edit()
                    .putString(PREFS_JSON, json.toString())
                    .apply();
        }
    }

    public void updateCurrentWeather(JSONObject json) {
        renderWeather(json);
        savePreferences(json);
    }

    public int getTemperatureColor(double temperature) {
        if (temperature <= 15)
            return Color.parseColor("#7997A1");
        else if (temperature > 15 && temperature <= 25)
            return Color.parseColor("#CC8400");
        else
            return Color.RED;
    }

}
