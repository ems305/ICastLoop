package com.ems305.icastloop.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ems305.icastloop.base.ICastApplication;

/**
 * Created by Erik Smith on 5/9/15.
 */
public class NetworkUtils {

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) ICastApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
