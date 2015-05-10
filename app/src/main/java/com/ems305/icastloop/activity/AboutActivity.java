package com.ems305.icastloop.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ems305.icastloop.R;

import java.util.Calendar;

/**
 * Created by erik on 5/9/15.
 */
public class AboutActivity extends Activity {

    private TextView mAboutText;
    private TextView mCopyright;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initControls();
        setupActivity();
    }

    // ------------------------------------
    // Init Methods
    // ------------------------------------

    private void initControls() {

        mAboutText = (TextView) findViewById(R.id.activity_about_text);
        mCopyright = (TextView) findViewById(R.id.activity_about_copyright);
    }

    private void setupActivity() {

        mAboutText.setText("Android Intellicast radar viewer\n\nICastLoop has no affiliation with Intellicast or WSI");

        Calendar calendar = Calendar.getInstance();
        mCopyright.setText("© " + calendar.get(Calendar.YEAR) + " Erik Smith");
    }

    // ------------------------------------
    // Menu Methods
    // ------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Treat "Up" Button As Back Button So We Don't Go Through Full Lifecycle Again...
                onBackPressed();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
}
