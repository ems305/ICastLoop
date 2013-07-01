package com.ems305.icastloop;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.Window;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private WebView webView;
    private Spinner spinner;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Our Exit Button
        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        mProgressDialog = new ProgressDialog(this);

        // Update Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);

        // Setup Our WebView Control
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setInitialScale(127); // So the Gif Fits The Container
        webView.loadUrl("http://images.intellicast.com/WxImages/RadarLoop/den_None_anim.gif"); // Set Default To Denver

        webView.setWebViewClient(new ICastWebViewClient());
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        // Get Corresponding Radar URL And Load That
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_urls_array, android.R.layout.simple_spinner_item);
        CharSequence radarUrl = adapter.getItem(pos);

        webView = (WebView) findViewById(R.id.webView1);
        webView.setVisibility(View.GONE);
        webView.loadUrl(radarUrl.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // interface callback
    }



    private class ICastWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.setVisibility(View.GONE);
            mProgressDialog.setTitle("Loading");
            mProgressDialog.show();
            mProgressDialog.setMessage("Loading " + url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressDialog.dismiss();
            animate(view);
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }


    private void animate(final WebView view) {
        Animation anim = AnimationUtils.loadAnimation(getBaseContext(), android.R.anim.slide_in_left);
        view.startAnimation(anim);
    }


    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
