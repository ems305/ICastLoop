package com.ems305.icastloop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ems305.icastloop.R;

/**
 * Created by erik on 5/9/15.
 */
public class SplashActivity extends Activity {

    private final int SPLASH_TIME = 2500;

    // --------------------------------
    // Lifecycle Methods
    // --------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Go to Main After A Bit
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        }, SPLASH_TIME);
    }
}
