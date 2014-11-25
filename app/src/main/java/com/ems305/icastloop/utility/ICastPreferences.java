package com.ems305.icastloop.utility;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ems305.icastloop.base.ICastApplication;

/**
 * Created by eriksmith on 7/5/14.
 */
public class ICastPreferences {

    private static ICastPreferences mInstance = null;
    private static SharedPreferences mSharedPrefs;

    @SuppressWarnings("unused")
    protected ICastPreferences() {
        // No Instantiation In Singleton
    }

    // Singleton Access
    public static ICastPreferences getInstance() {
        if (mInstance == null) {
            mInstance = new ICastPreferences();
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
        return getSharedPreferences().getString("defaultLocation", null);
    }

    public void setDefaultLocation(String location) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString("defaultLocation", location);
        editor.apply();
    }

    public Boolean getDefaultMode() {
        return getSharedPreferences().getBoolean("defaultMode", false);
    }

    public void setDefaultMode(Boolean defaultMode) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("defaultMode", defaultMode);
        editor.apply();
    }
}
