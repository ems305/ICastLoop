package com.ems305.icastloop.utility;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ems305.icastloop.base.ICastApplication;

/**
 * Created by Erik Smith on 7/5/14.
 */

public class ICastPrefs {

    private static final String PREFERENCE_DEFAULT_LOCATION = "defaultLocation";
    private static final String PREFERENCE_DEFAULT_MODE = "defaultMode";
    private static final String PREFERENCE_USE_LOOP = "useLoop";
    private static final String PREFERENCE_LATITUDE = "longitude";
    private static final String PREFERENCE_LONGITUDE = "longitude";

    private static ICastPrefs mInstance = null;
    private static SharedPreferences mSharedPrefs;

    @SuppressWarnings("unused")
    protected ICastPrefs() {
        // No Instantiation In Singleton
    }

    // Singleton Access
    public static ICastPrefs getInstance() {
        if (mInstance == null) {
            mInstance = new ICastPrefs();
        }
        return mInstance;
    }

    private SharedPreferences getSharedPreferences() {
        if (mSharedPrefs == null) {
            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(ICastApplication.getAppContext());
        }
        return mSharedPrefs;
    }

    public String getDefaultLocation() {
        return getSharedPreferences().getString(PREFERENCE_DEFAULT_LOCATION, null);
    }

    public void setDefaultLocation(String location) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_DEFAULT_LOCATION, location);
        editor.apply();
    }

    public Boolean getDefaultMode() {
        return getSharedPreferences().getBoolean(PREFERENCE_DEFAULT_MODE, false);
    }

    public void setDefaultMode(boolean defaultMode) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_DEFAULT_MODE, defaultMode);
        editor.apply();
    }

    public Boolean getUseLoop() {
        return getSharedPreferences().getBoolean(PREFERENCE_USE_LOOP, true);
    }

    public void setUseLoop(boolean defaultMode) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_USE_LOOP, defaultMode);
        editor.apply();
    }

    public long getLatitude() {
        return getSharedPreferences().getLong(PREFERENCE_LATITUDE, 0);
    }

    public void setLatitude(long latitude) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(PREFERENCE_LATITUDE, latitude);
        editor.apply();
    }

    public long getLongitude() {
        return getSharedPreferences().getLong(PREFERENCE_LONGITUDE, 0);
    }

    public void setLongitude(long longitude) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(PREFERENCE_LONGITUDE, longitude);
        editor.apply();
    }
}
