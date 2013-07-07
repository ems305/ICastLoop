package com.ems305.icastloop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private WebView webView;
    private Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Check For Network Availability
        if(!this.isOnline()){
            // Bounce, They Don't Have A Connection
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You must have an active network connection")
                   .setTitle("ICastLoop Error")
                   .setCancelable(false)
                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // Exit App
                            finish();
                            System.exit(0);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Update Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);

        // Set Defaults
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean useDefaultLocation = settings.getBoolean("defaultMode", false);
        if(useDefaultLocation){
            String selLocation = settings.getString("defaultLocation", null);
            if(selLocation != null){
                int pos = adapter.getPosition(selLocation);
                spinner.setSelection(pos);
            }
        }

        // Now Set the Image
        this.updateImages();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Build ActionBar Menu Items
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preferences:
                // Go to Settings Screen
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_exit:
                // Exit The App
                finish();
                System.exit(0);
                break;
            case R.id.action_refresh:
                // Refresh Image
                this.updateImages();
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        this.updateImages();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // interface callback
    }


    private void updateImages() {

        webView = (WebView) findViewById(R.id.radarWebView);

        // Set Our WebView Defaults
        this.setupWebView(webView);

        spinner = (Spinner) findViewById(R.id.spinner);
        int pos = spinner.getSelectedItemPosition();

        // Get Code
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_codes_array, android.R.layout.simple_spinner_item);
        CharSequence radarCode = adapter.getItem(pos);

        int webViewWidth = webView.getWidth();
        final String html = "<body><table width=" + webViewWidth + "px ><tr><td><img src=\""
                + "http://images.intellicast.com/WxImages/RadarLoop/" + radarCode + "_None_anim.gif"
                + "\" width=" + webViewWidth + "px /></td></tr></table>" + "</body>";

        webView.loadData(html, "text/html", "utf-8");
        webView.setBackgroundColor(Color.TRANSPARENT);
    }


    private void setupWebView(WebView webView){

        // Clear Cache
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);

        // Set All The Settings We Need To Have It Look Nice In The Device
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLightTouchEnabled(true);
        settings.setLoadWithOverviewMode(true);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        webView.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view, int progress) {

                progressBar.setProgress(progress);
                if(progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
          <option value="den">CO - Denver</option>
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