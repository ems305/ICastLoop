package com.ems305.icastloop.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by eriksmith on 7/5/14.
 */
public class ICastApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        ICastApplication.mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return ICastApplication.mContext;
    }
}