package com.example.hivian.myweather.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hivian.myweather.R;
import com.thbs.skycons.library.CloudFogView;
import com.thbs.skycons.library.CloudHvRainView;
import com.thbs.skycons.library.CloudRainView;
import com.thbs.skycons.library.CloudSnowView;
import com.thbs.skycons.library.CloudSunView;
import com.thbs.skycons.library.CloudThunderView;
import com.thbs.skycons.library.CloudView;
import com.thbs.skycons.library.MoonView;
import com.thbs.skycons.library.SunView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class CurrentWeatherFragment extends Fragment {
    private TextView cityField;
    private TextView updatedField;
    private TextView descriptionField;
    private TextView windField;
    private TextView humidityField;
    private TextView pressureField;
    private TextView currentTemperatureField;

    private JSONObject details;
    private JSONObject main;
    private JSONObject wind;
    private DateFormat df;
    private String updatedOn;
    private String temperature;
    private LinearLayout weatherIconLayout;
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
        windField = rootView.findViewById(R.id.wind_field);
        humidityField = rootView.findViewById(R.id. humidity_field);
        pressureField = rootView.findViewById(R.id.pressure_field);
        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        weatherIconLayout = rootView.findViewById(R.id.weather_icon);

        loadPreferences();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather_icons.ttf");
        preferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private void renderWeather(JSONObject json) {
        try {
            details = json.getJSONArray("weather").getJSONObject(0);
            main = json.getJSONObject("main");
            wind = json.getJSONObject("wind");
            df = DateFormat.getDateTimeInstance();
            updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            temperature = String.format("%.2f", main.getDouble("temp")) + " ℃";

            cityField.setText(json.getString("name").toUpperCase(Locale.getDefault()) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));
            updatedField.setText("Last update: " + updatedOn);
            descriptionField.setText(details.getString("description").toUpperCase(Locale.getDefault()));
            windField.setText(wind.getString("speed") + " km/h");
            humidityField.setText(main.getString("humidity") + " %");
            pressureField.setText(main.getString("pressure") + " hPa");
            currentTemperatureField.setText(temperature);
            currentTemperatureField.setTextColor(getTemperatureColor(
                    main.getDouble("temp")));
            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e){
            Log.d("updateCurrentWeather", "JSON field(s) missing");
        }
    }


    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        weatherIconLayout = getActivity().findViewById(R.id.weather_icon);

        int id = actualId / 100;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                SunView sunView = new SunView(
                        getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
                weatherIconLayout.addView(sunView);
            } else {
                MoonView moonView = new MoonView(
                        getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
                weatherIconLayout.addView(moonView);
            }
        } else if (actualId == 801) {
            CloudSunView cloudSunView = new CloudSunView(
                    getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
            weatherIconLayout.addView(cloudSunView);
        } else {
            switch(id) {
                case 2 :
                    CloudThunderView cloudThunderView = new CloudThunderView(
                            getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
                    weatherIconLayout.addView(cloudThunderView);
                    break;
                case 3 :
                    CloudRainView cloudRainView = new CloudRainView(
                            getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
                    weatherIconLayout.addView(cloudRainView);
                    break;
                case 7 :
                    CloudFogView cloudFogView = new CloudFogView(
                            getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
                    weatherIconLayout.addView(cloudFogView);
                    break;
                case 8 :
                    CloudView cloudView = new CloudView(
                            getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
                    weatherIconLayout.addView(cloudView);
                    break;
                case 6 :
                    CloudSnowView cloudSnowView = new CloudSnowView(
                            getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
                    weatherIconLayout.addView(cloudSnowView);
                    break;
                case 5 :
                    CloudHvRainView cloudHvRainView = new CloudHvRainView(
                            getActivity(), false, true, Color.parseColor("#FFFFFF"), Color.parseColor("#00000000"));
                    weatherIconLayout.addView(cloudHvRainView);
                    break;
            }
        }
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
