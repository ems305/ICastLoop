package com.ems305.icastloop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Erik on 7/5/13.
 */
public class SettingsActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }


    // Button Clicks
    public void onBtnClicked(View v){

        if(v.getId() == R.id.backButton){
            // Go back to Main Screen
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }


}

