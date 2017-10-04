package com.example.hivian.myweather.views.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.hivian.myweather.R;

/**
 * Created by hivian on 10/4/17.
 */

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Fragment fragment = null;
    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("POS", String.valueOf(position));
        switch (position) {
            case 0:
                fragment = Fragment.instantiate(context, CurrentWeatherFragment.class.getName());
                break;
            case 1:
                fragment = Fragment.instantiate(context, CurrentWeatherFragment.class.getName());
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Current weather";
            case 1:
                return "Forecast weather";
        }
        return null;
    }

}
