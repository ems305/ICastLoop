package com.ems305.icastloop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

        initControls();
        setupActivity();
    }

    // ------------------------------------
    // Init Methods
    // ------------------------------------

    private void initControls() {

        mAboutText = (TextView) findViewById(R.id.activity_about_text);
        mCopyright= (TextView) findViewById(R.id.activity_about_copyright);
    }

    private void setupActivity() {

        mAboutText.setText("Android Intellicast radar viewer\n\nICastLoop has no affiliation with Intellicast or WSI");

        Calendar calendar = Calendar.getInstance();
        mCopyright.setText("© " + calendar.get(Calendar.YEAR) + " Erik Smith");
    }
}
