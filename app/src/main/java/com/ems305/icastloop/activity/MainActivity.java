package com.ems305.icastloop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.ems305.icastloop.R;
import com.ems305.icastloop.utility.ICastLocation;
import com.ems305.icastloop.utility.ICastPreferences;

public class MainActivity extends Activity {

    // todo: add splash screen
    // todo: update icons

    private WebView mWebView;
    private Spinner mSpinner;

    // ------------------------------------
    // Lifecycle Methods
    // ------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Check For Network Availability
        if (!this.isOnline()) {
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

        mWebView = (WebView) findViewById(R.id.radarWebView);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(mItemSelectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setupActivity();
    }

    // ------------------------------------
    // Menu Methods
    // ------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    // ------------------------------------
    // Private Methods
    // ------------------------------------

    private void setupActivity() {

        // Update Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        // Set Defaults
        boolean useDefaultLocation = ICastPreferences.getInstance().getDefaultMode();
        if (useDefaultLocation) {
            String selLocation = ICastPreferences.getInstance().getDefaultLocation();
            if (!TextUtils.isEmpty(selLocation)) {
                mSpinner.setSelection(adapter.getPosition(selLocation));
            }
        } else {

            // This Will Get Location
            ICastLocation.LocationResult locationResult = new ICastLocation.LocationResult() {

                @Override
                public void gotLocation(Location location) {
                    //Got the location!

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();

                    // This Will Maintain Precision
                    editor.putLong("latitude", Double.doubleToLongBits(location.getLatitude()));
                    editor.putLong("longitude", Double.doubleToLongBits(location.getLongitude()));

                    editor.commit();
                }
            };

            // Acquire Location... This Will Callback Above
            ICastLocation myLocation = new ICastLocation();
            myLocation.getLocation(this, locationResult);

            // Find Closest Radar Location
            double latitude = Double.longBitsToDouble(settings.getLong("latitude", 0));
            double longitude = Double.longBitsToDouble(settings.getLong("longitude", 0));

            if (latitude != 0 && longitude != 0) {

                Location userLoc = new android.location.Location("iCastUserLocation");
                userLoc.setLatitude(latitude);
                userLoc.setLongitude(longitude);

                String[] radarCoordinates = getResources().getStringArray(R.array.radar_coordinate_array);
                float maxDist = Float.MAX_VALUE;
                int nearLocPos = 0;

                for (int i = 0; i < radarCoordinates.length; i++) {

                    if (radarCoordinates[i].contains(",")) {

                        // Coordinates Are Formatted Like '34.746481,-92.289595'
                        String[] coordinate = radarCoordinates[i].split(",");

                        Double dLat = Double.parseDouble(coordinate[0]);
                        Double dLng = Double.parseDouble(coordinate[1]);

                        // Turn It To Android Location Object
                        Location coordinateLoc = new Location("coordinate");
                        coordinateLoc.setLatitude(dLat);
                        coordinateLoc.setLongitude(dLng);

                        float distTo = userLoc.distanceTo(coordinateLoc); // Android Built In Method To Compare Two Locations, Returns In Meters
                        if (distTo < maxDist) {
                            maxDist = distTo;
                            nearLocPos = i;
                        }
                    }
                }

                // Don't Need To Zero Check, If It's Zero It Will Set To First Item In Spinner
                mSpinner.setSelection(nearLocPos);
            }
        }

        // Now Set the Image
        this.updateImages();
    }

    private void updateImages() {

        // Set Our WebView Defaults
        this.setupWebView(mWebView);

        // Get Code
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_codes_array, android.R.layout.simple_spinner_item);
        String radarCode = adapter.getItem(mSpinner.getSelectedItemPosition()).toString();

        // Set Defaults
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Determine Endpoint Based On Still/Animation
        boolean useLoop = settings.getBoolean("useLoop", true);
        String radarEndpoint = "/WxImages/" + (useLoop ? "RadarLoop/" + radarCode + "_None_anim" : "Radar/" + radarCode);

        // Rather Than Dealing With Sizing Issues On The Page, We'll Embed The Image In Markup And Render That
        int webViewWidth = mWebView.getWidth();
        final String html = "<body><table width=" + webViewWidth + "px ><tr><td><img src=\""
                + "http://images.intellicast.com" + radarEndpoint
                + ".gif\" width=" + webViewWidth + "px /></td></tr></table>" + "</body>";

        mWebView.loadData(html, "text/html", "utf-8");
        mWebView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setupWebView(WebView webView) {

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
        settings.setLoadWithOverviewMode(true);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {

                progressBar.setProgress(progress);
                if (progress == 100) {
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
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ------------------------------------
    // Listeners
    // ------------------------------------

    final AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            updateImages();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
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