package com.ems305.icastloop;

import android.app.ProgressDialog;
import android.content.Intent;
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
        this.updateImage();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // interface callback
    }


    // Button Clicks
    public void onBtnClicked(View v){
        if(v.getId() == R.id.exitButton){
            // Exit The App
            finish();
            System.exit(0);
        }

        if(v.getId() == R.id.settingsButton){
            // TODO: Go to Settings Screen
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        if(v.getId() == R.id.refreshButton){
            this.updateImage();
        }
    }



    private void updateImage() {

        spinner = (Spinner) findViewById(R.id.spinner);
        int pos = spinner.getSelectedItemPosition();

        // Get Corresponding Radar URL And Load That
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_urls_array, android.R.layout.simple_spinner_item);
        CharSequence radarUrl = adapter.getItem(pos);

        webView = (WebView) findViewById(R.id.webView1);
        webView.setVisibility(View.GONE);
        webView.loadUrl(radarUrl.toString());
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


/*
          Non-Animation
          http://images.intellicast.com/WxImages/RadarLoop/[REPLACE].gif

          Animation
          http://images.intellicast.com/WxImages/RadarLoop/[REPLACE]_None_anim.gif

          <option value="default">United States</option>
          <option value="lit">AR - Little Rock</option>
          <option value="prc">AZ - Prescott</option>
          <option value="bfl">CA - Bakersfield</option>
          <option value="den" selected="selected">CO - Denver</option>
          <option value="hfd">CT - Hartford</option>
          <option value="eyw">FL - Key West</option>
          <option value="pie">FL - Saint Petersburg</option>
          <option value="csg">GA - Columbus</option>
          <option value="dsm">IA - Des Moines</option>
          <option value="myl">ID - McCall</option>
          <option value="spi">IL - Springfield</option>
          <option value="sln">KS - Salina</option>
          <option value="bwg">KY - Bowling Green</option>
          <option value="msy">LA - New Orleans</option>
          <option value="cad">MI - Cadillac</option>
          <option value="stc">MN - Saint Cloud</option>
          <option value="jef">MO - Jefferson City</option>
          <option value="tvr">MS - Vicksburg</option>
          <option value="lwt">MT - Lewistown</option>
          <option value="clt">NC - Charlotte</option>
          <option value="bis">ND - Bismarck</option>
          <option value="lbf">NE - North Platte</option>
          <option value="bml">NH - Berlin</option>
          <option value="row">NM - Roswell</option>
          <option value="rno">NV - Reno</option>
          <option value="bgm">NY - Binghamton</option>
          <option value="day">OH - Dayton</option>
          <option value="law">OK - Lawton</option>
          <option value="rdm">OR - Redmond</option>
          <option value="pir">SD - Pierre</option>
          <option value="bro">TX - Brownsville</option>
          <option value="sat">TX - San Antonio</option>
          <option value="pvu">UT - Provo</option>
          <option value="fcx">VA - Roanoke</option>
          <option value="shd">VA - Staunton</option>
          <option value="tiw">WA - Tacoma</option>
          <option value="riw">WY - Riverton</option>

*/